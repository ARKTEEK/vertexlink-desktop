package vertexlink.ui.view;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vertexlink.controller.DashboardController;
import vertexlink.device.Device;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.server.ClientHandler;
import vertexlink.ui.resources.PairingBanner;
import vertexlink.ui.resources.device.DevicesListPanel;
import vertexlink.ui.resources.information.InformationPanel;

public class DashboardView implements DashboardEventListener {
  private static final double COLLAPSED_WIDTH = 470;
  private static final double EXPANDED_WIDTH = 750;

  private final VBox rootContainer;
  private final HBox mainContent;
  private final PairingBanner pairingBanner = new PairingBanner();
  private final Stage ownerStage;

  private DevicesListPanel devicesListPanel;
  private InformationPanel informationPanel;
  private final DashboardController controller;

  public DashboardView(Stage ownerStage, DashboardController controller) {
    this.ownerStage = ownerStage;
    this.controller = controller;

    this.controller.setEventListener(this);

    initPanels();

    mainContent = new HBox(devicesListPanel, informationPanel);
    HBox.setHgrow(devicesListPanel, Priority.ALWAYS);

    rootContainer = new VBox(mainContent, pairingBanner);
    VBox.setVgrow(mainContent, Priority.ALWAYS);
  }

  private void initPanels() {
    informationPanel = new InformationPanel(this::closeInformationPanel);
    informationPanel.setVisible(false);
    informationPanel.setManaged(false);

    devicesListPanel = new DevicesListPanel(
        "Desktop",
        controller.isConnected(),
        controller.getDevicesList(),
        this::onDeviceSelected,
        controller::unpairDevice,
        this::handleToggleConnection,
        controller::refreshDevices);
  }

  private void onDeviceSelected(Device device) {
    informationPanel.showDevice(device);

    if (!informationPanel.isVisible()) {
      ownerStage.setWidth(EXPANDED_WIDTH);
      informationPanel.setManaged(true);
      informationPanel.setVisible(true);
    }
  }

  private void closeInformationPanel() {
    if (informationPanel.isVisible()) {
      informationPanel.clear();
      informationPanel.setVisible(false);
      informationPanel.setManaged(false);
      ownerStage.setWidth(COLLAPSED_WIDTH);
    }
  }

  private void handleToggleConnection() {
    controller.toggleConnection();
    devicesListPanel.setConnected(controller.isConnected());

    if (!controller.isConnected()) {
      closeInformationPanel();
    }
  }

  @Override
  public void onPairRequest(
      String deviceName,
      String addressKey,
      String calculatedPin,
      ClientHandler client,
      String deviceId) {
    Platform.runLater(() -> {
      pairingBanner.showRequest(deviceName, addressKey, calculatedPin, (address, accepted) -> {
        controller.handlePairingResponse(client, address, deviceId, deviceName, accepted);
      });
    });
  }

  @Override
  public void onDeviceListUpdated(java.util.List<Device> devices) {
    Platform.runLater(() -> {
      devicesListPanel.setDevices(devices);
    });
  }

  @Override
  public void onDataReceived(String data, String hostAddress) {
    Platform.runLater(() -> {
      System.out.println("[Dashboard] Data from " + hostAddress + ": " + data);
    });
  }

  public VBox getRoot() {
    return rootContainer;
  }
}
