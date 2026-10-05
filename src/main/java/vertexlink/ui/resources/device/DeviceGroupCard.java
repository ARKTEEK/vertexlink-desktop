package vertexlink.ui.resources.device;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DeviceGroupCard extends VBox {
  private final List<DeviceRow> rows;

  public DeviceGroupCard(String title, List<DeviceRow> rows) {
    super(0);
    this.rows = rows;
    getStyleClass().add("group-card");

    Label caption = new Label(title);
    caption.getStyleClass().add("header-card-caption");

    HBox captionBar = new HBox(caption);
    captionBar.getStyleClass().add("header-card-caption-bar");

    VBox body = new VBox(0);
    body.getChildren().addAll(rows);

    getChildren().addAll(captionBar, body);
    refreshLastRow();
  }

  public void filter(String query) {
    boolean anyVisible = false;

    for (DeviceRow row : rows) {
      boolean matches = query.isEmpty() || row.getDevice().getName().toLowerCase().contains(query);

      row.setVisible(matches);
      row.setManaged(matches);
      anyVisible |= matches;
    }

    setVisible(anyVisible);
    setManaged(anyVisible);
  }

  private void refreshLastRow() {
    if (!rows.isEmpty()) {
      rows.get(rows.size() - 1).getStyleClass().add("group-last");
    }
  }
}
