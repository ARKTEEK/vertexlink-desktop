package vertexlink.ui.resources.device;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class DeviceHeaderCard extends VBox {
  private static final String STATUS_ON_CLASS = "status-on";
  private static final String STATUS_OFF_CLASS = "status-off";
  private static final String STATUS_SHUTTING_DOWN_CLASS = "status-shutting-down";

  private final Label statusLabel = new Label();
  private final Button powerBtn;
  private final Button refreshBtn;
  private boolean lastConnected = false;

  public DeviceHeaderCard(
      String deviceName,
      boolean connected,
      Runnable onToggleConnection,
      Runnable onRefresh) {
    super(0);
    getStyleClass().add("header-card");

    Label nameLabel = new Label(deviceName);
    nameLabel.getStyleClass().add("device-title");

    statusLabel.getStyleClass().add("status-badge");

    VBox textBox = new VBox(2, nameLabel, statusLabel);
    textBox.setAlignment(Pos.CENTER_LEFT);

    StackPane avatar = new StackPane();
    avatar.getStyleClass().add("header-avatar");
    avatar.setMinSize(40, 40);
    avatar.setPrefSize(40, 40);
    avatar.setMaxSize(40, 40);

    avatar.getChildren().add(
        IconFactory.createIcon(IconPaths.DESKTOP, "desktop-icon"));

    HBox deviceDetails = new HBox(10, avatar, textBox);
    deviceDetails.setAlignment(Pos.CENTER_LEFT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    powerBtn = ComponentFactory.createPowerToggle(connected);
    powerBtn.setOnAction(e -> {
      if (onToggleConnection != null) {
        onToggleConnection.run();
      }
    });

    refreshBtn = ComponentFactory.createIconButton(IconPaths.REFRESH, "header-action-btn");
    ComponentFactory.setTooltip(refreshBtn, "Refresh devices");
    refreshBtn.setOnAction(e -> {
      if (onRefresh != null) {
        onRefresh.run();
      }
    });

    setConnected(connected);

    HBox actions = new HBox(6, refreshBtn, powerBtn);
    actions.setAlignment(Pos.CENTER_RIGHT);

    HBox topRow = new HBox(8, deviceDetails, spacer, actions);
    topRow.setAlignment(Pos.CENTER_LEFT);

    Label caption = new Label("LOCAL DEVICE");
    caption.getStyleClass().add("header-card-caption");

    HBox captionBar = new HBox(caption);
    captionBar.getStyleClass().add("header-card-caption-bar");

    VBox body = new VBox(topRow);
    body.getStyleClass().add("header-card-body");

    getChildren().addAll(captionBar, body);
  }

  public void setConnected(boolean connected) {
    lastConnected = connected;

    statusLabel.setText(connected ? "Discoverable" : "Hidden");
    statusLabel.getStyleClass().removeAll(STATUS_ON_CLASS, STATUS_OFF_CLASS, STATUS_SHUTTING_DOWN_CLASS);
    statusLabel.getStyleClass().add(connected ? STATUS_ON_CLASS : STATUS_OFF_CLASS);

    ComponentFactory.setTooltip(powerBtn, connected ? "Stop being discoverable" : "Become discoverable");
    powerBtn.setDisable(false);
    refreshBtn.setDisable(false);

    if (connected) {
      if (!powerBtn.getStyleClass().contains("active")) {
        powerBtn.getStyleClass().add("active");
      }
    } else {
      powerBtn.getStyleClass().remove("active");
    }
  }

  public void setShuttingDown(boolean shuttingDown) {
    if (shuttingDown) {
      statusLabel.setText("Shutting down...");
      statusLabel.getStyleClass().removeAll(STATUS_ON_CLASS, STATUS_OFF_CLASS);

      if (!statusLabel.getStyleClass().contains(STATUS_SHUTTING_DOWN_CLASS)) {
        statusLabel.getStyleClass().add(STATUS_SHUTTING_DOWN_CLASS);
      }

      powerBtn.setDisable(true);
      refreshBtn.setDisable(true);
    } else {
      setConnected(lastConnected);
    }
  }
}
