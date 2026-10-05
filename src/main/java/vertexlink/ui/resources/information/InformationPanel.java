package vertexlink.ui.resources.information;

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
  private final VBox detailsBox = new VBox();

  public InformationPanel(Runnable onClose) {
    super(16);
    getStyleClass().add("info-panel");
    setMinHeight(470);
    setMaxHeight(Region.USE_PREF_SIZE);

    Label title = new Label("Device Info");
    title.getStyleClass().add("info-title");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button closeBtn = ComponentFactory.createIconButton(IconPaths.CLOSE, "header-action-btn");
    ComponentFactory.setTooltip(closeBtn, "Close");
    closeBtn.setOnAction(e -> {
      if (onClose != null) {
        onClose.run();
      }
    });

    HBox titleRow = new HBox(8, title, spacer, closeBtn);
    titleRow.setAlignment(Pos.CENTER_LEFT);

    getChildren().addAll(titleRow, heroBox, detailsBox);
  }

  public void showDevice(Device device) {
    heroBox.getChildren().setAll(new DeviceHeroCard(device));
    detailsBox.getChildren().setAll(new DeviceDetailsCard(device));
  }

  public void clear() {
    heroBox.getChildren().clear();
    detailsBox.getChildren().clear();
  }
}
