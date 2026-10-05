package vertexlink.ui.resources.device;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.enums.DeviceStatus;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class DeviceRow extends HBox {
  private static final double ROW_PADDING = 10;
  private final Device device;
  private Label timeLabel;
  private Instant since;
  private boolean confirming = false;

  public DeviceRow(
      Device device,
      Instant connectedSince,
      Consumer<Device> onSelect,
      Consumer<Device> onUnpair,
      Runnable onDisconnect) {
    this.device = device;
    boolean connected = connectedSince != null;

    setSpacing(12);
    setAlignment(Pos.CENTER_LEFT);
    setPadding(new Insets(ROW_PADDING));
    getStyleClass().add("device-row");

    if (connected) {
      getStyleClass().add("connected");
    }

    StackPane avatar = new StackPane();
    avatar.getStyleClass().add("header-avatar");
    avatar.setMinSize(40, 40);
    avatar.setPrefSize(40, 40);
    avatar.setMaxSize(40, 40);
    avatar.getChildren().add(IconFactory.createIcon(IconPaths.PHONE, "phone-icon"));
    getChildren().add(avatar);

    VBox textContainer = new VBox(2);
    textContainer.setAlignment(Pos.CENTER_LEFT);

    Label nameLabel = new Label(device.getName());
    nameLabel.getStyleClass().add("device-name");

    textContainer.getChildren().add(nameLabel);

    HBox badgeRow = new HBox(6);
    badgeRow.setAlignment(Pos.CENTER_LEFT);

    if (connected) {
      since = connectedSince;
      timeLabel = ComponentFactory.createStatusBadge("00:00", false);

      badgeRow.getChildren().addAll(ComponentFactory.createStatusBadge("Connected", true), timeLabel);
      startTicker();
    } else {
      boolean isOnline = device.getStatus() == DeviceStatus.ONLINE;

      badgeRow.getChildren().add(ComponentFactory.createStatusBadge(isOnline ? "Online" : "Offline", isOnline));

      if (device.isPaired()) {
        badgeRow.getChildren().add(ComponentFactory.createNeutralBadge("Paired"));
      } else {
        badgeRow.getChildren().add(ComponentFactory.createStatusBadge("Not paired", false));
        getStyleClass().add("unpaired");
      }
    }

    textContainer.getChildren().add(badgeRow);
    getChildren().add(textContainer);

    if (device.isPaired() || connected) {
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      getChildren().add(spacer);
    }

    if (device.isPaired() && !connected) {
      Button unpairBtn = ComponentFactory.createIconButton(IconPaths.UNPAIR, "fab fab-secondary");
      unpairBtn.getStyleClass().add("unpair-button");
      ComponentFactory.setTooltip(unpairBtn, "Unpair device");

      unpairBtn.setOnAction(e -> {
        e.consume();
        showUnpairConfirmation(onUnpair);
      });

      unpairBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
      getChildren().add(unpairBtn);
    }

    if (connected) {
      Button disconnectBtn = ComponentFactory.createIconButton(IconPaths.CLOSE, "disconnect-action-btn");
      ComponentFactory.setTooltip(disconnectBtn, "Disconnect");

      disconnectBtn.setOnAction(e -> {
        e.consume();

        if (onDisconnect != null) {
          onDisconnect.run();
        }
      });

      disconnectBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);

      getChildren().add(disconnectBtn);
    }

    setOnMouseClicked(e -> {
      if (!confirming) {
        onSelect.accept(device);
      }
    });
  }

  private void showUnpairConfirmation(Consumer<Device> onUnpair) {
    if (confirming) {
      return;
    }

    confirming = true;

    List<Node> normalContent = new ArrayList<>(getChildren());
    setMinHeight(getHeight());
    getStyleClass().add("confirming");

    Label title = new Label("Unpair this device?");
    title.getStyleClass().add("confirm-title");

    Label detail = new Label(device.getName());
    detail.getStyleClass().add("confirm-detail");

    VBox textBox = new VBox(2, title, detail);
    textBox.setAlignment(Pos.CENTER_LEFT);
    textBox.setMinWidth(0);

    HBox.setHgrow(textBox, Priority.ALWAYS);

    Button cancelBtn = ComponentFactory.createIconButton(IconPaths.CLOSE, "confirm-btn");
    cancelBtn.getStyleClass().add("confirm-cancel");

    ComponentFactory.setTooltip(cancelBtn, "Cancel");
    cancelBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
    cancelBtn.setOnAction(e -> {
      e.consume();

      Platform.runLater(() -> {
        getChildren().setAll(normalContent);
        getStyleClass().remove("confirming");
        setMinHeight(Region.USE_COMPUTED_SIZE);

        confirming = false;
      });
    });

    Button confirmBtn = ComponentFactory.createIconButton(IconPaths.CHECK, "confirm-btn");
    confirmBtn.getStyleClass().add("confirm-danger");

    ComponentFactory.setTooltip(confirmBtn, "Confirm unpair");
    confirmBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
    confirmBtn.setOnAction(e -> {
      e.consume();

      onUnpair.accept(device);
    });

    getChildren().setAll(textBox, cancelBtn, confirmBtn);
  }

  private void startTicker() {
    updateTime();

    Timeline ticker = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> updateTime()));
    ticker.setCycleCount(Animation.INDEFINITE);

    sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene == null) {
        ticker.stop();
      } else {
        updateTime();

        ticker.play();
      }
    });
  }

  private void updateTime() {
    long total = Math.max(0, Duration.between(since, Instant.now()).getSeconds());
    long hours = total / 3600;
    long minutes = (total % 3600) / 60;
    long seconds = total % 60;

    timeLabel.setText(hours > 0
        ? String.format("%d:%02d:%02d", hours, minutes, seconds)
        : String.format("%02d:%02d", minutes, seconds));
  }

  public Device getDevice() {
    return device;
  }
}
