package vertexlink.ui.resources;

import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

public class Elements {
  public static Button createIconButton(String svgContent) {
    Button button = new Button();
    button.getStyleClass().add("sidebar-button");

    SVGPath icon = new SVGPath();
    icon.setContent(svgContent);
    icon.getStyleClass().add("sidebar-icon");

    button.setGraphic(icon);
    return button;
  }
}
