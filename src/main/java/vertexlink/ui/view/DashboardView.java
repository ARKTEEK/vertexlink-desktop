package vertexlink.ui.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardView {
  private final VBox root;
  private final Label statusLabel;
  private final Button toggleButton;

  public DashboardView() {
    this.root = new VBox(20);
    this.root.setAlignment(Pos.CENTER);
    this.root.getStyleClass().add("root");

    Label titleLabel = new Label("VertexLink Dashboard");
    titleLabel.getStyleClass().add("title-text");

    this.statusLabel = new Label("Server Status: Idle");
    this.statusLabel.getStyleClass().add("status-label");

    this.toggleButton = new Button("Start Server");
    this.toggleButton.getStyleClass().add("control-button");

    this.toggleButton.setOnAction(event -> {
      handleServerToggle();
    });

    this.root.getChildren().addAll(titleLabel, this.statusLabel, this.toggleButton);
  }

  public VBox getRoot() {
    return this.root;
  }

  private void handleServerToggle() {
    this.statusLabel.setText("Server Status: Running");
  }
}
