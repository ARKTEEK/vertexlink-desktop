package vertexlink.ui.resources;

import java.util.function.BiConsumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import vertexlink.enums.DeviceStatus;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class PairingBanner extends HBox {

  public PairingBanner() {
    super(12);
    setAlignment(Pos.CENTER_LEFT);
    getStyleClass().add("pairing-banner");
    hide();
  }

  public void showRequest(String deviceName, String address, String pin, BiConsumer<String, Boolean> onResponse) {
    StackPane phoneIcon = IconFactory.createPhoneIcon(DeviceStatus.ONLINE, 20, 32);

    Label nameLabel = new Label(deviceName);
    nameLabel.getStyleClass().add("pairing-banner-title");

    Label pinLabel = new Label("PIN: " + pin);
    pinLabel.getStyleClass().add("pairing-banner-pin");

    HBox infoBox = new HBox(10, phoneIcon, nameLabel, pinLabel);
    infoBox.setAlignment(Pos.CENTER_LEFT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button acceptBtn = ComponentFactory.createIconButton(IconPaths.CHECK,
        "pairing-banner-button pairing-banner-accept");
    acceptBtn.setTooltip(new Tooltip("Accept"));
    acceptBtn.setOnAction(e -> {
      if (onResponse != null) {
        onResponse.accept(address, true);
      }
      hide();
    });

    Button declineBtn = ComponentFactory.createIconButton(IconPaths.CLOSE,
        "pairing-banner-button pairing-banner-decline");
    declineBtn.setTooltip(new Tooltip("Decline"));
    declineBtn.setOnAction(e -> {
      if (onResponse != null) {
        onResponse.accept(address, false);
      }
      hide();
    });

    HBox actions = new HBox(8, declineBtn, acceptBtn);
    actions.setAlignment(Pos.CENTER_RIGHT);

    getChildren().setAll(infoBox, spacer, actions);
    setVisible(true);
    setManaged(true);
  }

  public void hide() {
    setVisible(false);
    setManaged(false);
    getChildren().clear();
  }
}
