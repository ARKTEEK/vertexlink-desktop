package vertexlink.ui.resources.device;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class DeviceSectionHeader extends HBox {

  public DeviceSectionHeader(String title) {
    super(8);
    setAlignment(Pos.CENTER_LEFT);
    getStyleClass().add("devices-section-container");

    Label label = new Label(title);
    label.getStyleClass().add("devices-section-label");

    Region line = new Region();
    line.getStyleClass().add("devices-section-line");
    HBox.setHgrow(line, Priority.ALWAYS);

    getChildren().addAll(label, line);
  }
}
