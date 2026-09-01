package vertexlink.ui.resources.information;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconPaths;

public class InformationPanel extends VBox {
  private final VBox heroBox = new VBox();
  private final VBox detailsBox = new VBox(12);
  private final VBox emptyState = new InformationEmptyState();

  public InformationPanel(Runnable onClose) {
    super(18);
    getStyleClass().add("info-panel");
    setPrefWidth(300);
    setPadding(new Insets(18));

    Label title = new Label("Device Info");
    title.getStyleClass().add("info-title");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button closeBtn = ComponentFactory.createIconButton(IconPaths.CLOSE, "close-button");
    closeBtn.setOnAction(e -> {
      if (onClose != null) {
        onClose.run();
      }
    });

    HBox header = new HBox(title, spacer, closeBtn);
    header.setAlignment(Pos.CENTER_LEFT);

    detailsBox.getChildren().add(emptyState);
    VBox.setVgrow(detailsBox, Priority.ALWAYS);

    getChildren().addAll(header, heroBox, detailsBox);
  }

  public void showDevice(Device device) {
    heroBox.getChildren().setAll(new DeviceHeroCard(device));
    detailsBox.getChildren().setAll(new DeviceDetailsCard(device));
  }

  public void clear() {
    heroBox.getChildren().clear();
    detailsBox.getChildren().setAll(emptyState);
  }
}
