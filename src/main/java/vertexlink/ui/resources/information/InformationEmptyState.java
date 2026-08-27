package vertexlink.ui.resources.information;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InformationEmptyState extends VBox {

  public InformationEmptyState() {
    super(10);
    setAlignment(Pos.CENTER);
    setPadding(new Insets(48, 10, 10, 10));

    Label mark = new Label("\u2726");
    mark.getStyleClass().add("info-empty-icon");

    Label text = new Label("Select a device to view details");
    text.getStyleClass().add("info-empty");

    getChildren().addAll(mark, text);
  }
}
