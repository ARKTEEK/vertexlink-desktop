package vertexlink.ui.resources.device;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import vertexlink.ui.resources.global.IconFactory;
import vertexlink.ui.resources.global.IconPaths;

public class DeviceSearchBar extends HBox {
  private static final String FOCUSED_CLASS = "search-bar-focused";

  public DeviceSearchBar(Consumer<String> onSearch) {
    super(8);
    setAlignment(Pos.CENTER_LEFT);
    getStyleClass().add("search-bar");

    Node searchIcon = IconFactory.createIcon(IconPaths.SEARCH, "search-icon");

    TextField field = new TextField();
    field.setPromptText("Search devices...");
    field.getStyleClass().add("search-field");
    HBox.setHgrow(field, Priority.ALWAYS);

    field.textProperty().addListener((obs, oldV, newV) -> {
      if (onSearch != null) {
        onSearch.accept(newV);
      }
    });

    field.focusedProperty().addListener((obs, was, focused) -> {
      if (focused) {
        getStyleClass().add(FOCUSED_CLASS);
      } else {
        getStyleClass().remove(FOCUSED_CLASS);
      }
    });

    getChildren().addAll(searchIcon, field);
  }
}
