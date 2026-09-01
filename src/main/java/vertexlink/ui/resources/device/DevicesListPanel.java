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
  private final VBox emptyState = new DevicesEmptyState();
  private final Consumer<Device> onSelectDevice;
  private final Consumer<Device> onUnpairDevice;
  private DeviceHeaderCard headerCard;

  public DevicesListPanel(
      String deviceName,
      boolean connected,
      List<Device> devices,
      Consumer<Device> onSelectDevice,
      Consumer<Device> onUnpairDevice,
      Runnable onToggleConnection,
      Runnable onRefresh) {
    super(12);
    this.onSelectDevice = onSelectDevice;
    this.onUnpairDevice = onUnpairDevice;

    getStyleClass().add("devices-panel");
    setPrefWidth(260);
    setPadding(new Insets(16));

    this.headerCard = new DeviceHeaderCard(deviceName, connected, onToggleConnection, onRefresh, this::filter);

    ScrollPane scrollPane = createScrollPane();
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    getChildren().addAll(headerCard, scrollPane);
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
      rowsBox.getChildren().add(new DeviceSectionHeader("Paired"));

      for (Device device : paired) {
        rowsBox.getChildren().add(new DeviceRow(device, onSelectDevice, onUnpairDevice));
      }
    }

    if (!unpaired.isEmpty()) {
      rowsBox.getChildren().add(new DeviceSectionHeader("Not Paired"));

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
