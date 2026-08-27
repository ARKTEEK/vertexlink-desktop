package vertexlink.ui.resources.device;

import java.util.function.Consumer;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import vertexlink.device.Device;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class DeviceRow extends HBox {
  private static final double ROW_PADDING = 10;
  private final Device device;

  public DeviceRow(Device device, Consumer<Device> onSelect, Consumer<Device> onUnpair) {
    this.device = device;

    setSpacing(10);
    setAlignment(Pos.CENTER_LEFT);
    setPadding(new Insets(ROW_PADDING));
    getStyleClass().add("device-row");

    getChildren().add(IconFactory.createPhoneIcon(device.getStatus()));

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("device-name");
    getChildren().add(nameLabel);

    if (device.isPaired()) {
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);

      getChildren().add(spacer);

      Button unpairBtn = ComponentFactory.createIconButton(IconPaths.UNPAIR, "fab fab-secondary");
      unpairBtn.getStyleClass().add("unpair-button");
      unpairBtn.setOnAction(e -> {
        e.consume();
        onUnpair.accept(device);
      });

      unpairBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);

      getChildren().add(unpairBtn);
    }

    setOnMouseClicked(e -> onSelect.accept(device));
  }

  public Device getDevice() {
    return device;
  }
}
