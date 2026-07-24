package vertexlink.ui.resources;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import vertexlink.device.Device;
import vertexlink.ui.resources.global.Elements;

public class DeviceRow extends HBox {

  private static final double ROW_PADDING = 10;

  private final Device device;

  public DeviceRow(Device device, Consumer<Device> onSelect) {
    this.device = device;
    setSpacing(10);
    setAlignment(Pos.CENTER_LEFT);
    setPadding(new Insets(ROW_PADDING));
    getStyleClass().add("device-row");
    getChildren().add(Elements.createPhoneIcon(device.getStatus()));

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("device-name");

    getChildren().add(nameLabel);

    setOnMouseClicked(e -> onSelect.accept(device));
  }

  public Device getDevice() {
    return device;
  }
}
