package vertexlink.ui.resources.global;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import vertexlink.enums.DeviceStatus;

public class IconFactory {

  public static StackPane createDesktopIcon(DeviceStatus status) {
    return createDesktopIcon(status, 28, 18, true);
  }

  public static StackPane createDesktopIcon(DeviceStatus status, double width, double height) {
    return createDesktopIcon(status, width, height, true);
  }

  public static StackPane createDesktopIcon(DeviceStatus status, double width, double height, boolean showStatusDot) {
    Rectangle body = new Rectangle(width, height);
    body.setArcWidth(4);
    body.setArcHeight(4);
    body.getStyleClass().add("desktop-icon-body");

    Rectangle screen = new Rectangle(width - 4, height - 4);
    screen.setArcWidth(2);
    screen.setArcHeight(2);
    screen.getStyleClass().add("desktop-icon-screen");

    StackPane display = new StackPane(body, screen);

    Rectangle stand = new Rectangle(width * 0.18, height * 0.25);
    stand.getStyleClass().add("desktop-icon-stand");

    Rectangle base = new Rectangle(width * 0.5, height * 0.15);
    base.setArcWidth(2);
    base.setArcHeight(2);
    base.getStyleClass().add("desktop-icon-base");

    VBox desktop = new VBox(1, display, stand, base);
    desktop.setAlignment(Pos.CENTER);

    if (showStatusDot) {
      Circle statusDot = new Circle(4);
      statusDot.setFill(Color.web(status.getColorHex()));
      statusDot.getStyleClass().add("desktop-status-dot");

      StackPane container = new StackPane(desktop, statusDot);

      StackPane.setAlignment(statusDot, Pos.TOP_RIGHT);

      container.setPrefSize(width + 6, height + 8);

      return container;
    }

    StackPane container = new StackPane(desktop);
    container.setPrefSize(width + 6, height + 8);

    return container;
  }

  public static StackPane createPhoneIcon(DeviceStatus status, double width, double height, boolean showStatusDot) {
    Rectangle body = new Rectangle(width, height);
    body.setArcWidth(8);
    body.setArcHeight(8);
    body.getStyleClass().add("phone-icon-body");

    Rectangle screen = new Rectangle(width - 6, height - 11);
    screen.setArcWidth(3);
    screen.setArcHeight(3);
    screen.getStyleClass().add("phone-icon-screen");

    Circle homeButton = new Circle(1.6);
    homeButton.getStyleClass().add("phone-icon-button");

    VBox inner = new VBox(3, screen, homeButton);
    inner.setAlignment(Pos.CENTER);

    StackPane phone = new StackPane(body, inner);

    StackPane container;

    if (showStatusDot) {
      Circle statusDot = new Circle(4);
      statusDot.setFill(Color.web(status.getColorHex()));
      statusDot.getStyleClass().add("phone-status-dot");

      container = new StackPane(phone, statusDot);

      StackPane.setAlignment(statusDot, Pos.TOP_RIGHT);
    } else {
      container = new StackPane(phone);
    }

    container.setPrefSize(width + 6, height + 4);

    return container;
  }

  public static StackPane createPhoneIcon(DeviceStatus status) {
    return createPhoneIcon(status, 22, 36, true);
  }

  public static StackPane createPhoneIcon(DeviceStatus status, double width, double height) {
    return createPhoneIcon(status, width, height, true);
  }

  public static SVGPath createIcon(String svgContent, String styleClass) {
    SVGPath icon = new SVGPath();
    icon.setContent(svgContent);
    icon.getStyleClass().add(styleClass);

    return icon;
  }
}
