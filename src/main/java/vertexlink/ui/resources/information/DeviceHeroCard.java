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
    setPadding(new Insets(14));

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("hero-device-name");

    boolean connected = device.getStatus() == DeviceStatus.ONLINE;
    Label pairedBadge = device.isPaired()
        ? ComponentFactory.createNeutralBadge("Paired")
        : ComponentFactory.createStatusBadge("Not Paired", false);

    Label connectionBadge = ComponentFactory.createStatusBadge(
        connected ? "Online" : "Offline",
        connected);

    HBox badgeRow = new HBox(6, connectionBadge, pairedBadge);
    badgeRow.setAlignment(Pos.CENTER_LEFT);
    badgeRow.getStyleClass().add("hero-badge-row");

    VBox textBox = new VBox(6, nameLabel, badgeRow);
    textBox.setAlignment(Pos.CENTER_LEFT);

    StackPane avatar = new StackPane();
    avatar.getStyleClass().add("hero-device-avatar");
    avatar.setMinSize(40, 40);
    avatar.setPrefSize(40, 40);
    avatar.setMaxSize(40, 40);

    avatar.getChildren().add(
        IconFactory.createIcon(IconPaths.PHONE, "phone-icon"));

    HBox content = new HBox(12, avatar, textBox);
    content.setAlignment(Pos.CENTER_LEFT);

    getChildren().add(content);
  }
}
