package vertexlink.ui;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ViewManager {
  public enum Screen {
    DASHBOARD, SETTINGS
  }

  private static StackPane contentWrapper;
  private static final Map<Screen, Button> navButtons = new HashMap<>();
  private static Screen currentScreen;

  public static void setContentWrapper(StackPane wrapper) {
    contentWrapper = wrapper;
  }

  public static void registerNavButton(Screen screen, Button button) {
    navButtons.put(screen, button);
  }

  public static void navigateTo(Screen screen, Pane newView) {
    if (contentWrapper == null) {
      return;
    }

    if (currentScreen != null && navButtons.containsKey(currentScreen)) {
      navButtons.get(currentScreen).getStyleClass().remove("active-nav");
    }

    currentScreen = screen;

    if (navButtons.containsKey(currentScreen)) {
      navButtons.get(currentScreen).getStyleClass().add("active-nav");
    }

    contentWrapper.getChildren().clear();
    contentWrapper.getChildren().add(newView);
  }
}
