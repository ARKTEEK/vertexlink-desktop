package vertexlink.ui.resources;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.ui.resources.global.Elements;

public class InformationPanel extends VBox {

  private static final double PANEL_PADDING = 16;
  private static final double SECTION_SPACING = 16;

  private final VBox summaryBox = new VBox();
  private final VBox detailsBox = new VBox(10);
  private final Label emptyLabel = new Label("Select a device to view details");

  public InformationPanel(Runnable onClose) {
    super(SECTION_SPACING);
    getStyleClass().add("info-panel");
    setPrefWidth(300);
    setPadding(new Insets(PANEL_PADDING));

    Label title = new Label("Information");
    title.getStyleClass().add("info-title");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button closeBtn = Elements.createIconButton(IconPaths.CLOSE, "close-button");
    closeBtn.setOnAction(e -> onClose.run());

    HBox header = new HBox(title, spacer, closeBtn);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(0));

    emptyLabel.getStyleClass().add("info-empty");
    detailsBox.getChildren().add(emptyLabel);
    detailsBox.setPadding(new Insets(0));

    getChildren().addAll(header, new Separator(), summaryBox, detailsBox);
  }

  public void showDevice(Device device) {
    summaryBox.getChildren().setAll(Elements.createDeviceSummary(device));

    Label sectionLabel = new Label("Connection Details");
    sectionLabel.getStyleClass().add("info-section-label");

    VBox card = new VBox();
    card.getStyleClass().add("info-card");
    card.getChildren().addAll(
        Elements.createInfoRow("Client ID", device.getClientId()),
        Elements.createInfoRow("IPv4 Address", device.getIpv4Address()),
        Elements.createInfoRow("IPv6 Address", device.getIpv6Address()),
        Elements.createInfoRow("App Version", device.getAppVersion()));

    if (!card.getChildren().isEmpty()) {
      card.getChildren().get(card.getChildren().size() - 1)
          .getStyleClass().add("info-row-last");
    }

    detailsBox.getChildren().setAll(sectionLabel, card);
  }

  public void clear() {
    summaryBox.getChildren().clear();
    detailsBox.getChildren().setAll(emptyLabel);
  }
}
