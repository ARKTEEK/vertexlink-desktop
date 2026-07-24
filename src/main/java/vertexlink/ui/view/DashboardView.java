package vertexlink.ui.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import vertexlink.device.Device;
import vertexlink.device.DeviceIdentity;
import vertexlink.enums.DeviceStatus;
import vertexlink.networking.DeviceBroadcaster;
import vertexlink.networking.DeviceScanner;
import vertexlink.ui.resources.DevicesListPanel;
import vertexlink.ui.resources.InformationPanel;

public class DashboardView {
  private final HBox root;
  private final Stage ownerStage;
  private static final double COLLAPSED_WIDTH = 470;
  private static final double EXPANDED_WIDTH = 750;

  private final DeviceIdentity identity = new DeviceIdentity();
  private final DeviceBroadcaster broadcaster = new DeviceBroadcaster();
  private final DeviceScanner scanner;
  private boolean connected = false;
  private DevicesListPanel devicesListPanel;
  private InformationPanel informationPanel;
  private final Map<String, Device> discoveredDevices = new LinkedHashMap<>();

  public DashboardView(Stage ownerStage) {
    this.ownerStage = ownerStage;
    this.scanner = new DeviceScanner((id, name, address) -> {
      Platform.runLater(() -> {
        onDeviceDiscovered(id, name, address);
      });
    }, identity.getId());

    informationPanel = new InformationPanel(this::closeInformationPanel);
    informationPanel.setVisible(false);
    informationPanel.setManaged(false);

    devicesListPanel = new DevicesListPanel(
        "VertexLink Desktop",
        connected,
        new ArrayList<>(discoveredDevices.values()),
        this::onDeviceSelected,
        this::toggleConnection,
        this::refreshDevices);

    root = new HBox(devicesListPanel, informationPanel);
    HBox.setHgrow(devicesListPanel, Priority.ALWAYS);
  }

  private void onDeviceDiscovered(String id, String name, String address) {
    Device device = discoveredDevices.get(address);

    if (device == null) {
      device = new Device(UUID.randomUUID().toString(), name, id);
      discoveredDevices.put(address, device);
    } else {
      device.setName(name);
      device.setClientId(id);
    }

    device.setStatus(DeviceStatus.ONLINE);
    device.setIpv4Address(address);

    devicesListPanel.setDevices(new ArrayList<>(discoveredDevices.values()));
  }

  private void onDeviceSelected(Device device) {
    informationPanel.showDevice(device);

    if (!informationPanel.isVisible()) {
      informationPanel.setVisible(true);
      informationPanel.setManaged(true);

      ownerStage.setWidth(EXPANDED_WIDTH);
    }
  }

  private void closeInformationPanel() {
    informationPanel.clear();
    informationPanel.setVisible(false);
    informationPanel.setManaged(false);

    ownerStage.setWidth(COLLAPSED_WIDTH);
  }

  private void toggleConnection() {
    connected = !connected;

    if (connected) {
      broadcaster.start("DesktopServer", 28401, identity.getId());
      scanner.start();
    } else {
      scanner.stop();
      broadcaster.stop();
      discoveredDevices.clear();

      devicesListPanel.setDevices(new ArrayList<>());

      closeInformationPanel();
    }

    devicesListPanel.setConnected(connected);
  }

  private void refreshDevices() {
    scanner.stop();
    scanner.start();
  }

  public HBox getRoot() {
    return root;
  }
}
