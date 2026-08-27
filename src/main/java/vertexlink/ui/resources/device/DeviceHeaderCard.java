package vertexlink.ui.resources.device;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import vertexlink.enums.DeviceStatus;
import vertexlink.ui.resources.global.ComponentFactory;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class DeviceHeaderCard extends VBox {
  private final Label statusLabel = new Label();
  private final TextField searchField = new TextField();
  private final Button powerBtn;
  private final Button searchToggleBtn;
  private boolean searchOpen = false;

  public DeviceHeaderCard(
      String deviceName,
      boolean connected,
      Runnable onToggleConnection,
      Runnable onRefresh,
      Consumer<String> onSearch) {
    super(8);
    getStyleClass().add("header-card");

    StackPane avatar = IconFactory.createDesktopIcon(DeviceStatus.OFFLINE);
    avatar.getStyleClass().add("header-avatar");

    Label nameLabel = new Label(deviceName);
    nameLabel.getStyleClass().add("device-title");

    statusLabel.getStyleClass().add("status-badge");
    setConnected(connected);

    VBox textBox = new VBox(2, nameLabel, statusLabel);
    textBox.setAlignment(Pos.CENTER_LEFT);

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

    searchToggleBtn = ComponentFactory.createIconButton(IconPaths.SEARCH, "header-action-btn");
    searchToggleBtn.setOnAction(e -> {
      toggleSearch();
    });

    Button refreshBtn = ComponentFactory.createIconButton(IconPaths.REFRESH, "header-action-btn");
    refreshBtn.setOnAction(e -> {
      if (onRefresh != null) {
        onRefresh.run();
      }
    });

    HBox actions = new HBox(6, searchToggleBtn, refreshBtn, powerBtn);
    actions.setAlignment(Pos.CENTER_RIGHT);

    HBox topRow = new HBox(8, deviceDetails, spacer, actions);
    topRow.setAlignment(Pos.CENTER_LEFT);

    searchField.setPromptText("Search devices...");
    searchField.getStyleClass().add("search-field");
    searchField.setMaxWidth(Double.MAX_VALUE);
    searchField.textProperty().addListener((obs, oldV, newV) -> {
      if (onSearch != null) {
        onSearch.accept(newV);
      }
    });

    VBox searchContainer = new VBox(searchField);
    searchContainer.getStyleClass().add("search-container");
    searchContainer.setManaged(false);
    searchContainer.setVisible(false);

    getChildren().addAll(topRow, searchContainer);
  }

  public void setConnected(boolean connected) {
    statusLabel.setText(connected ? "Online" : "Offline");
    statusLabel.getStyleClass().removeAll("status-on", "status-off");
    statusLabel.getStyleClass().add(connected ? "status-on" : "status-off");

    if (powerBtn != null) {
      if (connected) {
        if (!powerBtn.getStyleClass().contains("active")) {
          powerBtn.getStyleClass().add("active");
        }
      } else {
        powerBtn.getStyleClass().remove("active");
      }
    }
  }

  private void toggleSearch() {
    searchOpen = !searchOpen;

    if (searchOpen) {
      searchField.setVisible(true);
      searchField.setManaged(true);
      searchToggleBtn.getStyleClass().add("active");
      searchField.requestFocus();
    } else {
      searchToggleBtn.getStyleClass().remove("active");
      searchField.setVisible(false);
      searchField.setManaged(false);
      searchField.clear();
    }
  }
}
