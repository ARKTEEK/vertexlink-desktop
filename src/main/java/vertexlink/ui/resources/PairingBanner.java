package vertexlink.ui.resources;

import java.util.function.BiConsumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import vertexlink.enums.DeviceStatus;
import vertexlink.ui.resources.global.Elements;

public class PairingBanner extends HBox {

  public PairingBanner() {
    super(12);
    setAlignment(Pos.CENTER_LEFT);
    setPadding(new Insets(12, 16, 12, 16));
    getStyleClass().add("pairing-banner");
    hide();
  }

  public void showRequest(String deviceName, String address, String pin, BiConsumer<String, Boolean> onResponse) {
    StackPane phoneIcon = Elements.createPhoneIcon(DeviceStatus.ONLINE, 20, 32);

    Label textLabel = new Label(deviceName + " pairing request. PIN: " + pin);
    textLabel.getStyleClass().add("pairing-banner-text");

    HBox infoBox = new HBox(10, phoneIcon, textLabel);
    infoBox.setAlignment(Pos.CENTER_LEFT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button acceptBtn = Elements.createIconButton(IconPaths.CHECK, "pairing-banner-button pairing-banner-accept");
    acceptBtn.setTooltip(new Tooltip("Accept"));
    acceptBtn.setOnAction(e -> {
      hide();
      onResponse.accept(address, true);
    });

    Button declineBtn = Elements.createIconButton(IconPaths.CLOSE, "pairing-banner-button pairing-banner-decline");
    declineBtn.setTooltip(new Tooltip("Decline"));
    declineBtn.setOnAction(e -> {
      hide();
      onResponse.accept(address, false);
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
