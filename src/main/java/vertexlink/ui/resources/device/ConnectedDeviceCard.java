package vertexlink.ui.resources.device;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class ConnectedDeviceCard extends VBox {

  public ConnectedDeviceCard(Device device, Consumer<Device> onSelect, Runnable onDisconnect) {
    super(8);
    getStyleClass().add("header-card");

    StackPane avatar = IconFactory.createPhoneIcon(device.getStatus(), 28, 18, false);
    avatar.getStyleClass().add("header-avatar");

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("device-title");

    Label connectedBadge = ComponentFactory.createStatusBadge("Connected", true);

    VBox textBox = new VBox(2, nameLabel, connectedBadge);
    textBox.setAlignment(Pos.CENTER_LEFT);

    HBox deviceDetails = new HBox(10, avatar, textBox);
    deviceDetails.setAlignment(Pos.CENTER_LEFT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button disconnectBtn = ComponentFactory.createIconButton(IconPaths.UNPAIR, "header-action-btn disconnect-btn");
    disconnectBtn.setOnAction(e -> {
      e.consume();

      if (onDisconnect != null) {
        onDisconnect.run();
      }
    });
    disconnectBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);

    HBox topRow = new HBox(8, deviceDetails, spacer, disconnectBtn);
    topRow.setAlignment(Pos.CENTER_LEFT);

    getChildren().add(topRow);

    setOnMouseClicked(e -> {
      if (onSelect != null) {
        onSelect.accept(device);
      }
    });
  }
}
