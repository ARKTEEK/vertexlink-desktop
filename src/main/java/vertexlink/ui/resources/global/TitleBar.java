package vertexlink.ui.resources.global;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TitleBar extends HBox {
  private double dragOffsetX;
  private double dragOffsetY;

  public TitleBar(Stage stage, String title) {
    getStyleClass().add("title-bar");
    setAlignment(Pos.CENTER_LEFT);
    setPadding(new Insets(0, 6, 0, 10));
    setSpacing(6);

    Label badge = new Label("V");
    badge.getStyleClass().add("title-bar-badge");

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("title-bar-label");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button minimizeBtn = ComponentFactory.createIconButton(IconPaths.MINIMIZE, "title-bar-button");
    minimizeBtn.setOnAction(e -> stage.setIconified(true));

    Button closeBtn = ComponentFactory.createIconButton(IconPaths.CLOSE, "title-bar-button close-window");
    closeBtn.setOnAction(e -> stage.close());

    getChildren().addAll(badge, titleLabel, spacer, minimizeBtn, closeBtn);

    setOnMousePressed(e -> {
      dragOffsetX = e.getSceneX();
      dragOffsetY = e.getSceneY();
    });

    setOnMouseDragged(e -> {
      stage.setX(e.getScreenX() - dragOffsetX);
      stage.setY(e.getScreenY() - dragOffsetY);
    });
  }
}
