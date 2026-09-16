package vertexlink.ui.resources.device;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;

public class DevicesListPanel extends VBox {
  private final VBox rowsBox = new VBox(4);
  private final VBox connectedDeviceBox = new VBox();
  private final VBox emptyState = new DevicesEmptyState();
  private final Consumer<Device> onSelectDevice;
  private final Consumer<Device> onUnpairDevice;
  private final Runnable onDisconnect;
  private DeviceHeaderCard headerCard;

  public DevicesListPanel(
      String deviceName,
      boolean connected,
      List<Device> devices,
      Consumer<Device> onSelectDevice,
      Consumer<Device> onUnpairDevice,
      Runnable onToggleConnection,
      Runnable onRefresh,
      Runnable onDisconnect) {
    super(12);
    this.onSelectDevice = onSelectDevice;
    this.onUnpairDevice = onUnpairDevice;
    this.onDisconnect = onDisconnect;

    getStyleClass().add("devices-panel");
    setPrefWidth(260);
    setPadding(new Insets(16));

    this.headerCard = new DeviceHeaderCard(deviceName, connected, onToggleConnection, onRefresh, this::filter);

    connectedDeviceBox.setManaged(false);
    connectedDeviceBox.setVisible(false);

    ScrollPane scrollPane = createScrollPane();
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    getChildren().addAll(headerCard, connectedDeviceBox, scrollPane);
    setDevices(devices);
  }

  public void setDevices(List<Device> devices) {
    rowsBox.getChildren().clear();

    List<Device> paired = devices == null ? List.of() : devices.stream().filter(Device::isPaired).toList();
    List<Device> unpaired = devices == null ? List.of() : devices.stream().filter(d -> !d.isPaired()).toList();

    if (paired.isEmpty() && unpaired.isEmpty()) {
      rowsBox.setAlignment(Pos.CENTER);

      VBox.setVgrow(rowsBox, Priority.ALWAYS);

      rowsBox.getChildren().add(emptyState);

      return;
    }

    rowsBox.setAlignment(Pos.TOP_LEFT);

    if (!paired.isEmpty()) {
      rowsBox.getChildren().add(new DeviceSectionHeader("PAIRED"));

      for (Device device : paired) {
        rowsBox.getChildren().add(new DeviceRow(device, onSelectDevice, onUnpairDevice));
      }
    }

    if (!unpaired.isEmpty()) {
      rowsBox.getChildren().add(new DeviceSectionHeader("AVAILABLE"));

      for (Device device : unpaired) {
        rowsBox.getChildren().add(new DeviceRow(device, onSelectDevice, onUnpairDevice));
      }
    }
  }

  public void setConnected(boolean connected) {
    if (headerCard != null) {
      headerCard.setConnected(connected);
    }
  }

  public void setShuttingDown(boolean shuttingDown) {
    headerCard.setShuttingDown(shuttingDown);
  }

  public void setConnectedDevice(Device device) {
    connectedDeviceBox.getChildren().clear();

    if (device == null) {
      connectedDeviceBox.setManaged(false);
      connectedDeviceBox.setVisible(false);

      return;
    }

    connectedDeviceBox.getChildren().add(new DeviceSectionHeader("CONNECTED"));
    connectedDeviceBox.getChildren().add(new ConnectedDeviceCard(device, onSelectDevice, onDisconnect));
    connectedDeviceBox.setManaged(true);
    connectedDeviceBox.setVisible(true);
  }

  private ScrollPane createScrollPane() {
    ScrollPane scrollPane = new ScrollPane(rowsBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.getStyleClass().add("groups-scroll");

    return scrollPane;
  }

  private void filter(String query) {
    String q = query == null ? "" : query.trim().toLowerCase();

    for (Node node : rowsBox.getChildren()) {
      if (node instanceof DeviceRow row) {
        boolean matches = q.isEmpty() || row.getDevice().getName().toLowerCase().contains(q);

        node.setVisible(matches);
        node.setManaged(matches);
      }
    }
  }
}
