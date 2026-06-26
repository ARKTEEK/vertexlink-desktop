package vertexlink.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import vertexlink.ui.resources.Elements;
import vertexlink.ui.resources.IconPaths;
import vertexlink.ui.view.DashboardView;
import vertexlink.ui.view.SettingsView;

public class Sidebar {
  private final VBox root;

  public Sidebar() {
    this.root = new VBox(15);
    this.root.getStyleClass().add("sidebar");
    this.root.setAlignment(Pos.CENTER);

    Button dashboardBtn = Elements.createIconButton(IconPaths.DASHBOARD);
    ViewManager.registerNavButton(ViewManager.Screen.DASHBOARD, dashboardBtn);

    dashboardBtn.setOnAction(e -> {
      ViewManager.navigateTo(ViewManager.Screen.DASHBOARD, new DashboardView().getRoot());
    });

    Button settingsBtn = Elements.createIconButton(IconPaths.SETTINGS);
    ViewManager.registerNavButton(ViewManager.Screen.SETTINGS, settingsBtn);

    settingsBtn.setOnAction(e -> {
      ViewManager.navigateTo(ViewManager.Screen.SETTINGS, new SettingsView().getRoot());
    });

    this.root.getChildren().addAll(dashboardBtn, settingsBtn);
  }

  public VBox getRoot() {
    return this.root;
  }

}
