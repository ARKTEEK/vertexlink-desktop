package vertexlink.ui.resources.information;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.ui.resources.global.ComponentFactory;

public class DeviceDetailsCard extends VBox {

  public DeviceDetailsCard(Device device) {
    super(4);

    Label sectionLabel = new Label("Connection Details");
    sectionLabel.getStyleClass().add("info-section-label");

    VBox card = new VBox();
    card.getStyleClass().add("info-card");
    card.getChildren().addAll(
        ComponentFactory.createInfoRow("Client ID", device.getClientId()),
        ComponentFactory.createInfoRow("IPv4 Address", device.getIpv4Address()),
        ComponentFactory.createInfoRow("IPv6 Address", device.getIpv6Address()),
        ComponentFactory.createInfoRow("App Version", device.getAppVersion()));

    if (!card.getChildren().isEmpty()) {
      card.getChildren().get(card.getChildren().size() - 1)
          .getStyleClass().add("info-row-last");
    }

    getChildren().addAll(sectionLabel, card);
  }
}
