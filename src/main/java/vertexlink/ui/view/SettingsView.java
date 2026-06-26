package vertexlink.ui.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsView {
  private final VBox root;

  public SettingsView() {
    this.root = new VBox(20);
    this.root.setAlignment(Pos.CENTER);
    this.root.getStyleClass().add("root");

    Label settingsLabel = new Label("VertexLink Settings");
    settingsLabel.getStyleClass().add("title-text");

    this.root.getChildren().addAll(settingsLabel);
  }

  public VBox getRoot() {
    return this.root;
  }
}
