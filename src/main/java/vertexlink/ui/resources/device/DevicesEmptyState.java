package vertexlink.ui.resources.device;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class DevicesEmptyState extends VBox {

  public DevicesEmptyState() {
    super(14);
    setAlignment(Pos.CENTER);
    VBox.setVgrow(this, Priority.ALWAYS);

    StackPane iconBadge = new StackPane();
    iconBadge.getStyleClass().add("empty-icon-badge");

    SVGPath icon = IconFactory.createIcon(IconPaths.SEARCH, "empty-icon");
    icon.setScaleX(1.7);
    icon.setScaleY(1.7);
    iconBadge.getChildren().add(icon);

    Label text = new Label("No devices found");
    text.getStyleClass().add("devices-empty");

    getChildren().addAll(iconBadge, text);
  }
}
