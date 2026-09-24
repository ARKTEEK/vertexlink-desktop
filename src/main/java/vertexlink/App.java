package vertexlink;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import vertexlink.controller.KeyboardController;
import vertexlink.controller.MouseController;
import vertexlink.controller.PeripheralControllers;
import vertexlink.device.DeviceDirectory;
import vertexlink.device.DeviceIdentity;
import vertexlink.device.DeviceState;
import vertexlink.handler.KeyboardInputHandler;
import vertexlink.handler.MouseInputHandler;
import vertexlink.listener.NetworkPairingListener;
import vertexlink.network.NetworkManager;
import vertexlink.network.discovery.DeviceBroadcaster;
import vertexlink.network.discovery.DeviceScanner;
import vertexlink.network.lifecycle.ConnectionLifecycleManager;
import vertexlink.network.protocol.InboundMessageRouter;
import vertexlink.network.protocol.ProtocolMessenger;
import vertexlink.network.server.ClientHandler;
import vertexlink.network.session.UDPSessionState;
import vertexlink.pairing.PairingCoordinator;
import vertexlink.pairing.PairingService;
import vertexlink.store.PairedDeviceStore;
import vertexlink.ui.resources.global.ResizableCanvas;
import vertexlink.ui.resources.global.TitleBar;
import vertexlink.ui.view.DashboardView;

public class App extends Application {
  private static final int TCP_PORT = 28401;
  private static final int UDP_PORT = 28402;
  private static final String DESKTOP_NAME = "DesktopServer";

  @Override
  public void start(Stage primaryStage) {
    primaryStage.initStyle(StageStyle.UNDECORATED);

    TitleBar titleBar = new TitleBar(primaryStage, "VertexLink");

    AppCoordinator controller = createAppCoordinator();
    DashboardView dashboard = new DashboardView(primaryStage, controller);

    ResizableCanvas canvas = new ResizableCanvas();
    canvas.getStyleClass().add("white-canvas");

    StackPane canvasLayer = new StackPane(canvas, dashboard.getRoot());

    VBox.setVgrow(canvasLayer, Priority.ALWAYS);
    VBox.setVgrow(dashboard.getRoot(), Priority.ALWAYS);

    VBox root = new VBox(titleBar, canvasLayer);
    root.getStyleClass().add("app-shell");

    Scene scene = new Scene(root, 470, 600);
    String cssPath = getClass().getResource("/styles/styles.css").toExternalForm();
    scene.getStylesheets().add(cssPath);

    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private AppCoordinator createAppCoordinator() {
    DeviceIdentity identity = new DeviceIdentity();
    PairedDeviceStore store = new PairedDeviceStore();
    PairingService pairingService = new PairingService(store);
    DeviceDirectory devices = new DeviceDirectory(new DeviceState(), pairingService);

    MouseController mouseController = PeripheralControllers.createMouseController();
    KeyboardController keyboardController = PeripheralControllers.createKeyboardController();

    MouseInputHandler mouseInputHandler = new MouseInputHandler();
    mouseInputHandler.setMouseController(mouseController);

    KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler();
    keyboardInputHandler.setKeyboardController(keyboardController);

    ProtocolMessenger messenger = new ProtocolMessenger();
    InboundMessageRouter messageRouter = new InboundMessageRouter(mouseInputHandler, keyboardInputHandler, messenger);
    UDPSessionState udpSession = new UDPSessionState();

    NetworkManager networkManager = new NetworkManager(TCP_PORT, UDP_PORT, messageRouter, udpSession);
    PairingCoordinator pairing = new PairingCoordinator(
        pairingService,
        messenger,
        devices,
        networkManager,
        identity.getId(),
        DESKTOP_NAME);

    AppCoordinator controller = new AppCoordinator(pairing, devices);

    DeviceScanner scanner = new DeviceScanner(
        (id, name, address) -> {
          controller.onDeviceDiscovered(id, name, address);
        },
        identity.getId());

    DeviceBroadcaster broadcaster = new DeviceBroadcaster();
    ConnectionLifecycleManager connectionLifecycle = new ConnectionLifecycleManager(
        broadcaster,
        scanner,
        networkManager,
        devices,
        TCP_PORT,
        identity.getId());

    controller.setConnectionLifecycle(connectionLifecycle);

    networkManager.setPairingListener(new NetworkPairingListener() {
      @Override
      public void onPairRequest(
          String deviceId,
          String deviceName,
          String publicKey,
          ClientHandler client) {
        pairing.onPairRequest(deviceId, deviceName, publicKey, client);
      }

      @Override
      public void onAuth(
          String deviceId,
          String token,
          ClientHandler client) {
        networkManager.setTrustedUdpAddress(client.getAddress());
        pairing.onAuth(deviceId, token, client);
      }

      @Override
      public void onDisconnect(ClientHandler client) {
        pairing.onDisconnect(client);
      }
    });

    networkManager.setDataListener(controller::onDataReceived);

    return controller;
  }

  public static void main(String[] args) {
    System.setProperty("prism.text", "native");
    System.setProperty("prism.allowhidpi", "true");
    System.setProperty("prism.lcdtext", "false");
    launch(args);
  }
}
