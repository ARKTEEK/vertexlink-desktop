package vertexlink.ui.resources.global;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import vertexlink.device.Device;
import vertexlink.enums.DeviceStatus;
import vertexlink.ui.resources.IconPaths;

public class Elements {

  public static StackPane createPhoneIcon(DeviceStatus status) {
    return createPhoneIcon(status, 22, 36);
  }

  public static StackPane createPhoneIcon(DeviceStatus status, double width, double height) {
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

    Circle statusDot = new Circle(4);
    statusDot.setFill(Color.web(status.getColorHex()));
    statusDot.getStyleClass().add("phone-status-dot");

    StackPane container = new StackPane(phone, statusDot);

    StackPane.setAlignment(statusDot, Pos.TOP_RIGHT);

    container.setPrefSize(width + 6, height + 4);

    return container;
  }

  public static Button createIconButton(String svgContent) {
    Button button = new Button();
    button.getStyleClass().add("sidebar-button");
    button.setGraphic(createIcon(svgContent, "sidebar-icon"));

    return button;
  }

  public static Button createIconButton(String svgContent, String styleClass) {
    Button button = new Button();
    button.getStyleClass().addAll("icon-button", styleClass);
    button.setGraphic(createIcon(svgContent, "icon"));

    return button;
  }

  public static SVGPath createIcon(String svgContent, String styleClass) {
    SVGPath icon = new SVGPath();
    icon.setContent(svgContent);
    icon.getStyleClass().add(styleClass);

    return icon;
  }

  public static Button createPowerToggle(boolean active) {
    Button btn = new Button();
    btn.getStyleClass().add("power-toggle");

    if (active) {
      btn.getStyleClass().add("active");
    }

    btn.setGraphic(createIcon(IconPaths.POWER, "power-icon"));

    return btn;
  }

  public static VBox createInfoRow(String label, String value) {
    Label lbl = new Label(label);
    lbl.getStyleClass().add("info-label");

    Label val = new Label(value == null || value.isEmpty() ? "\u2014" : value);
    val.getStyleClass().add("info-value");

    Button copyBtn = createIconButton(IconPaths.COPY, "copy-button");
    copyBtn.setTooltip(new Tooltip("Copy"));
    copyBtn.setOnAction(e -> {
      ClipboardContent clipboardContent = new ClipboardContent();
      clipboardContent.putString(value == null ? "" : value);

      Clipboard.getSystemClipboard().setContent(clipboardContent);
    });

    Region spacer = new Region();

    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox valueRow = new HBox(8, val, spacer, copyBtn);
    valueRow.setAlignment(Pos.CENTER_LEFT);

    VBox row = new VBox(6, lbl, valueRow);
    row.getStyleClass().add("info-row");
    row.setPadding(new Insets(12));

    return row;
  }

  public static HBox createStatusPill(DeviceStatus status) {
    Circle dot = new Circle(4);
    dot.setFill(Color.web(status.getColorHex()));

    Label label = new Label(titleCase(status.name()));
    label.getStyleClass().add("status-pill-label");

    HBox pill = new HBox(6, dot, label);
    pill.setAlignment(Pos.CENTER_LEFT);
    pill.getStyleClass().add("status-pill");
    pill.setPadding(new Insets(4, 10, 4, 8));

    return pill;
  }

  public static HBox createDeviceSummary(Device device) {
    StackPane avatar = createPhoneIcon(device.getStatus(), 26, 42);

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("summary-device-name");

    HBox pill = createStatusPill(device.getStatus());

    VBox textBox = new VBox(8, nameLabel, pill);
    textBox.setAlignment(Pos.CENTER_LEFT);

    HBox summary = new HBox(14, avatar, textBox);
    summary.setAlignment(Pos.CENTER_LEFT);
    summary.getStyleClass().add("device-summary");
    summary.setPadding(new Insets(14));

    return summary;
  }

  private static String titleCase(String value) {
    if (value == null || value.isEmpty()) {
      return value;
    }

    String lower = value.toLowerCase();

    return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
  }
}
