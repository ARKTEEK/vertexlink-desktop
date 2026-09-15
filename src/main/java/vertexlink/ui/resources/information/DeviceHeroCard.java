package vertexlink.ui.resources.information;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.enums.DeviceStatus;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class DeviceHeroCard extends VBox {

  public DeviceHeroCard(Device device) {
    getStyleClass().add("hero-card");
    setPadding(new Insets(16));

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("hero-device-name");

    boolean connected = device.getStatus() == DeviceStatus.ONLINE;
    Label pairedBadge = ComponentFactory.createStatusBadge(
        device.isPaired() ? "Paired" : "Not Paired",
        device.isPaired());

    Label connectionBadge = ComponentFactory.createStatusBadge(
        connected ? "Online" : "Offline",
        connected);

    HBox badgeRow = new HBox(6, pairedBadge, connectionBadge);
    badgeRow.setAlignment(Pos.CENTER_LEFT);
    badgeRow.getStyleClass().add("hero-badge-row");

    VBox textBox = new VBox(8, nameLabel, badgeRow);
    textBox.setAlignment(Pos.CENTER_LEFT);

    StackPane avatar = new StackPane();
    avatar.getStyleClass().add("hero-device-avatar");
    avatar.setMinSize(64, 64);
    avatar.setPrefSize(64, 64);
    avatar.setMaxSize(64, 64);

    avatar.getChildren().add(
        IconFactory.createIcon(IconPaths.PHONE, "phone-icon-big"));

    HBox content = new HBox(14, avatar, textBox);
    content.setAlignment(Pos.CENTER_LEFT);

    getChildren().add(content);
  }
}
