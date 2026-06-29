package vertexlink.ui.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import vertexlink.networking.DeviceBroadcaster;
import vertexlink.networking.DeviceScanner;

public class DashboardView {
  private final VBox root;
  private final Label statusLabel;
  private final Button toggleButton;
  private final ListView<String> deviceListView;
  private final DeviceScanner scanner;
  private final DeviceBroadcaster broadcaster = new DeviceBroadcaster();
  private boolean isScanning = false;

  public DashboardView() {
    this.root = new VBox(20);
    this.root.setAlignment(Pos.CENTER);
    this.root.getStyleClass().add("root");

    Label titleLabel = new Label("VertexLink Dashboard");
    titleLabel.getStyleClass().add("title-text");

    this.statusLabel = new Label("Server Status: Idle");
    this.statusLabel.getStyleClass().add("status-label");

    this.deviceListView = new ListView<>();

    this.scanner = new DeviceScanner((name, address) -> {
      Platform.runLater(() -> {
        if ("DesktopServer".equals(name)) {
          return;
        }

        String entry = name + " [" + address + "]";

        if (!this.deviceListView.getItems().contains(entry)) {
          this.deviceListView.getItems().add(entry);
        }
      });
    });

    this.toggleButton = new Button("Start Server");
    this.toggleButton.getStyleClass().add("control-button");
    this.toggleButton.setOnAction(event -> {
      handleServerToggle();
    });

    this.root.getChildren().addAll(titleLabel, this.statusLabel, this.toggleButton, this.deviceListView);
  }

  public VBox getRoot() {
    return this.root;
  }

  private void handleServerToggle() {
    if (this.isScanning) {
      this.scanner.stop();
      this.broadcaster.stop();
      this.statusLabel.setText("Server Status: Idle");
      this.toggleButton.setText("Start Server");
      this.deviceListView.getItems().clear();
      this.isScanning = false;
    } else {
      this.broadcaster.start("DesktopServer", 28401);
      this.scanner.start();
      this.statusLabel.setText("Server Status: Running");
      this.toggleButton.setText("Stop Server");
      this.isScanning = true;
    }
  }
}
