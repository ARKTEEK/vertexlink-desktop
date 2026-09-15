package vertexlink.ui.resources.global;

import javafx.scene.shape.SVGPath;

public class IconFactory {

  public static SVGPath createIcon(String svgContent, String styleClass) {
    SVGPath icon = new SVGPath();
    icon.setContent(svgContent);
    icon.getStyleClass().add(styleClass);

    return icon;
  }
}
