package vertexlink;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vertexlink.ui.Sidebar;
import vertexlink.ui.ViewManager;
import vertexlink.ui.view.DashboardView;

public class App extends Application {

  @Override
  public void start(Stage primaryStage) {
    BorderPane windowShell = new BorderPane();

    Sidebar sidebar = new Sidebar();
    windowShell.setLeft(sidebar.getRoot());

    StackPane contentWrapper = new StackPane();
    windowShell.setCenter(contentWrapper);

    ViewManager.setContentWrapper(contentWrapper);
    ViewManager.navigateTo(ViewManager.Screen.DASHBOARD, new DashboardView().getRoot());

    Scene scene = new Scene(windowShell, 700, 450);
    String cssPath = getClass().getResource("/styles/styles.css").toExternalForm();
    scene.getStylesheets().add(cssPath);

    primaryStage.setTitle("VertexLink");
    primaryStage.getIcons().add(new Image("file:icon.png"));
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
