package vertexlink;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import vertexlink.controller.DashboardController;
import vertexlink.ui.resources.global.TitleBar;
import vertexlink.ui.view.DashboardView;

public class App extends Application {
  @Override
  public void start(Stage primaryStage) {
    primaryStage.initStyle(StageStyle.UNDECORATED);

    TitleBar titleBar = new TitleBar(primaryStage, "VertexLink");

    DashboardController controller = new DashboardController();
    DashboardView dashboard = new DashboardView(primaryStage, controller);

    Canvas whiteCanvas = new Canvas();
    whiteCanvas.getStyleClass().add("white-canvas");

    StackPane canvasLayer = new StackPane(whiteCanvas, dashboard.getRoot());
    whiteCanvas.widthProperty().bind(canvasLayer.widthProperty());
    whiteCanvas.heightProperty().bind(canvasLayer.heightProperty());

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

  public static void main(String[] args) {
    System.setProperty("prism.text", "native");
    System.setProperty("prism.allowhidpi", "true");
    System.setProperty("prism.lcdtext", "false");
    launch(args);
  }
}
