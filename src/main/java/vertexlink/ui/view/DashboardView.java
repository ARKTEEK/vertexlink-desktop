package vertexlink.ui.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import vertexlink.device.DeviceIdentity;
import vertexlink.networking.DeviceBroadcaster;
import vertexlink.networking.DeviceScanner;

public class DashboardView {

  private final VBox root;
  private final Label statusLabel;
  private final Button toggleButton;
  private final ListView<String> deviceListView;

  private final DeviceIdentity identity = new DeviceIdentity();
  private final String deviceId = identity.getId();

  private final DeviceScanner scanner;
  private final DeviceBroadcaster broadcaster = new DeviceBroadcaster();

  private boolean isScanning = false;

  public DashboardView() {

    this.root = new VBox(20);
    this.root.setAlignment(Pos.CENTER);

    Label titleLabel = new Label("VertexLink Dashboard");

    this.statusLabel = new Label("Server Status: Idle");

    this.deviceListView = new ListView<>();

    this.scanner = new DeviceScanner((name, address) -> {
      Platform.runLater(() -> {
        String entry = name + " [" + address + "]";

        if (!deviceListView.getItems().contains(entry)) {
          deviceListView.getItems().add(entry);
        }
      });
    }, deviceId);

    this.toggleButton = new Button("Start Server");
    this.toggleButton.setOnAction(e -> handleServerToggle());

    this.root.getChildren().addAll(
        titleLabel,
        statusLabel,
        toggleButton,
        deviceListView);
  }

  private void handleServerToggle() {
    if (isScanning) {
      scanner.stop();
      broadcaster.stop();

      statusLabel.setText("Server Status: Idle");
      toggleButton.setText("Start Server");

      deviceListView.getItems().clear();

      isScanning = false;

    } else {
      broadcaster.start("DesktopServer", 28401, identity.getId());

      scanner.start();

      statusLabel.setText("Server Status: Running");
      toggleButton.setText("Stop Server");

      isScanning = true;
    }
  }

  public VBox getRoot() {
    return root;
  }
}
