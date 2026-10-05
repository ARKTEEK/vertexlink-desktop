package vertexlink.ui.view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import vertexlink.AppCoordinator;
import vertexlink.device.Device;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.server.ClientHandler;
import vertexlink.ui.resources.PairingBanner;
import vertexlink.ui.resources.device.DevicesListPanel;
import vertexlink.ui.resources.information.InformationPanel;

public class DashboardView implements DashboardEventListener {
  private static final double SHEET_OFFSET = 700;

  private final VBox rootContainer;
  private final StackPane mainContent;
  private final Region scrim = new Region();
  private ParallelTransition sheetAnimation;
  private final PairingBanner pairingBanner = new PairingBanner();

  private DevicesListPanel devicesListPanel;
  private InformationPanel informationPanel;
  private final AppCoordinator controller;

  public DashboardView(Stage ownerStage, AppCoordinator controller) {
    this.controller = controller;

    this.controller.setEventListener(this);
    this.controller.setConnectionStateListener(this::onConnectionStateChanged);
    this.controller.setConnectionTransitionListener(this::onConnectionTransitionStarted);

    initPanels();

    scrim.getStyleClass().add("sheet-scrim");
    scrim.setVisible(false);
    scrim.setOnMouseClicked(e -> closeInformationPanel());

    mainContent = new StackPane(devicesListPanel, scrim, informationPanel);
    mainContent.getStyleClass().add("dashboard-main");
    StackPane.setAlignment(informationPanel, Pos.BOTTOM_CENTER);

    Rectangle clip = new Rectangle();
    clip.widthProperty().bind(mainContent.widthProperty());
    clip.heightProperty().bind(mainContent.heightProperty());
    mainContent.setClip(clip);

    rootContainer = new VBox(mainContent, pairingBanner);
    VBox.setVgrow(mainContent, Priority.ALWAYS);
    VBox.setMargin(pairingBanner, new Insets(0, 12, 12, 12));
  }

  private void initPanels() {
    informationPanel = new InformationPanel(this::closeInformationPanel);
    informationPanel.setVisible(false);

    devicesListPanel = new DevicesListPanel(
        "Desktop",
        controller.isConnected(),
        controller.getDevicesList(),
        this::onDeviceSelected,
        controller::unpairDevice,
        this::handleToggleConnection,
        controller::refreshDevices,
        controller::disconnectConnectedDevice);

    devicesListPanel.setConnectedDevice(controller.getConnectedDevice());
  }

  private void onDeviceSelected(Device device) {
    informationPanel.showDevice(device);

    if (!informationPanel.isVisible()) {
      openSheet();
    }
  }

  private void openSheet() {
    stopSheetAnimation();

    scrim.setOpacity(0);
    scrim.setVisible(true);
    informationPanel.setTranslateY(SHEET_OFFSET);
    informationPanel.setVisible(true);

    TranslateTransition slide = new TranslateTransition(Duration.millis(240), informationPanel);
    slide.setToY(0);
    slide.setInterpolator(Interpolator.EASE_OUT);

    FadeTransition fade = new FadeTransition(Duration.millis(240), scrim);
    fade.setToValue(1);

    sheetAnimation = new ParallelTransition(slide, fade);
    sheetAnimation.play();
  }

  private void closeInformationPanel() {
    if (!informationPanel.isVisible()) {
      return;
    }

    stopSheetAnimation();

    TranslateTransition slide = new TranslateTransition(Duration.millis(190), informationPanel);
    slide.setToY(SHEET_OFFSET);
    slide.setInterpolator(Interpolator.EASE_IN);

    FadeTransition fade = new FadeTransition(Duration.millis(190), scrim);
    fade.setToValue(0);

    sheetAnimation = new ParallelTransition(slide, fade);
    sheetAnimation.setOnFinished(e -> {
      informationPanel.setVisible(false);
      scrim.setVisible(false);
      informationPanel.clear();
    });
    sheetAnimation.play();
  }

  private void stopSheetAnimation() {
    if (sheetAnimation != null) {
      sheetAnimation.stop();
      sheetAnimation = null;
    }
  }

  private void handleToggleConnection() {
    controller.toggleConnection();
  }

  private void onConnectionTransitionStarted(boolean goingOnline) {
    if (!goingOnline) {
      devicesListPanel.setShuttingDown(true);
    }
  }

  private void onConnectionStateChanged(boolean nowConnected) {
    devicesListPanel.setConnected(nowConnected);

    if (!nowConnected) {
      closeInformationPanel();
    }
  }

  @Override
  public void onPairRequest(
      String deviceName,
      String addressKey,
      String calculatedPin,
      ClientHandler client,
      String deviceId) {
    Platform.runLater(() -> {
      pairingBanner.showRequest(deviceName, addressKey, calculatedPin, (address, accepted) -> {
        controller.handlePairingResponse(client, address, deviceId, deviceName, accepted);
      });
    });
  }

  @Override
  public void onConnectionConflict(
      Device connectedDevice,
      String incomingDeviceName,
      String addressKey,
      String deviceId,
      ClientHandler client) {
    Platform.runLater(() -> {
      pairingBanner.showConnectionConflict(connectedDevice.getName(), incomingDeviceName, keepNew -> {
        controller.resolveConnectionConflict(client, addressKey, deviceId, incomingDeviceName, keepNew);
      });
    });
  }

  @Override
  public void onConnectedDeviceChanged(Device device) {
    Platform.runLater(() -> {
      devicesListPanel.setConnectedDevice(device);
    });
  }

  @Override
  public void onDeviceListUpdated(java.util.List<Device> devices) {
    Platform.runLater(() -> {
      devicesListPanel.setDevices(devices);
    });
  }

  @Override
  public void onDataReceived(String data, String hostAddress) {
    Platform.runLater(() -> {
      System.out.println("[Dashboard] Data from " + hostAddress + ": " + data);
    });
  }

  public VBox getRoot() {
    return rootContainer;
  }
}
