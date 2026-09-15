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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.enums.DeviceStatus;
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

    StackPane avatar = new StackPane();
    avatar.getStyleClass().add("header-avatar");
    avatar.setMinSize(40, 40);
    avatar.setPrefSize(40, 40);
    avatar.setMaxSize(40, 40);
    avatar.getChildren().add(IconFactory.createIcon(IconPaths.PHONE, "phone-icon"));
    getChildren().add(avatar);

    VBox textContainer = new VBox(2);
    textContainer.setAlignment(Pos.CENTER_LEFT);

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("device-name");
    textContainer.getChildren().add(nameLabel);

    boolean isOnline = false;
    if (device.getStatus() == DeviceStatus.ONLINE) {
      isOnline = true;
    }

    Label statusPill = ComponentFactory.createStatusBadge(isOnline ? "Online" : "Offline", isOnline);
    textContainer.getChildren().add(statusPill);

    getChildren().add(textContainer);

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

    setOnMouseClicked(e -> {
      onSelect.accept(device);
    });
  }

  public Device getDevice() {
    return device;
  }
}
