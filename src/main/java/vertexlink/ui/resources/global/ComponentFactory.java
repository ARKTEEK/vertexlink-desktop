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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ComponentFactory {
  private static final Duration TOOLTIP_DELAY = Duration.millis(1000);

  public static Tooltip createTooltip(String text) {
    Tooltip tooltip = new Tooltip(text);
    tooltip.setShowDelay(TOOLTIP_DELAY);
    tooltip.setHideDelay(Duration.millis(100));
    tooltip.setShowDuration(Duration.seconds(8));

    return tooltip;
  }

  public static void setTooltip(Button button, String text) {
    button.setTooltip(createTooltip(text));
  }

  public static Button createIconButton(String svgContent) {
    Button button = new Button();
    button.getStyleClass().add("icon");
    button.setGraphic(IconFactory.createIcon(svgContent, "icon"));

    return button;
  }

  public static Button createIconButton(String svgContent, String styleClass) {
    Button button = new Button();
    button.getStyleClass().addAll("icon-button", styleClass);
    button.setGraphic(IconFactory.createIcon(svgContent, "icon"));

    return button;
  }

  public static Button createPowerToggle(boolean active) {
    Button btn = new Button();
    btn.getStyleClass().add("power-toggle");

    if (active) {
      btn.getStyleClass().add("active");
    }

    btn.setGraphic(IconFactory.createIcon(IconPaths.POWER, "power-icon"));

    return btn;
  }

  public static Label createStatusBadge(String text, boolean positive) {
    Label badge = new Label(text);
    badge.getStyleClass().add("status-badge");
    badge.setMaxWidth(Region.USE_PREF_SIZE);
    badge.getStyleClass().add(positive ? "status-on" : "status-off");

    return badge;
  }

  public static Label createNeutralBadge(String text) {
    Label badge = new Label(text);
    badge.getStyleClass().addAll("status-badge", "status-neutral");
    badge.setMaxWidth(Region.USE_PREF_SIZE);

    return badge;
  }

  public static VBox createInfoRow(String label, String value) {
    Label lbl = new Label(label);
    lbl.getStyleClass().add("info-label");

    Label val = new Label(value == null || value.isEmpty() ? "\u2014" : value);
    val.getStyleClass().add("info-value");

    Tooltip tooltip = createTooltip(value);

    val.boundsInLocalProperty().addListener((obs, oldBounds, newBounds) -> {
      if (isTextClipped(val)) {
        val.setTooltip(tooltip);
      } else {
        val.setTooltip(null);
      }
    });

    Button copyBtn = createIconButton(IconPaths.COPY, "copy-button");
    setTooltip(copyBtn, "Copy");
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

  private static boolean isTextClipped(Label label) {
    Text helperText = new Text(label.getText());
    helperText.setFont(label.getFont());

    double neededWidth = helperText.getLayoutBounds().getWidth();
    double availableWidth = label.getWidth() - label.getInsets().getLeft() - label.getInsets().getRight();

    return neededWidth > availableWidth;
  }
}
