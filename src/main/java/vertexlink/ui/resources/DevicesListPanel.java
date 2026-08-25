package vertexlink.ui.resources;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import vertexlink.device.Device;
import vertexlink.ui.resources.global.Elements;

public class DevicesListPanel extends VBox {
  private static final double PANEL_PADDING = 16;
  private static final double ROW_SPACING = 14;
  private final VBox rowsBox = new VBox(4);
  private final Label statusLabel = new Label();
  private final Label emptyLabel = new Label("No devices found");
  private final Consumer<Device> onSelectDevice;
  private final Consumer<Device> onUnpairDevice;
  private final String deviceName;
  private final Runnable onToggleConnection;
  private final Runnable onRefresh;

  public DevicesListPanel(String deviceName, boolean connected, List<Device> devices,
      Consumer<Device> onSelectDevice,
      Consumer<Device> onUnpairDevice,
      Runnable onToggleConnection,
      Runnable onRefresh) {
    super(ROW_SPACING);
    this.deviceName = deviceName;
    this.onSelectDevice = onSelectDevice;
    this.onUnpairDevice = onUnpairDevice;
    this.onToggleConnection = onToggleConnection;
    this.onRefresh = onRefresh;

    getStyleClass().add("devices-panel");
    setPrefWidth(260);
    setPadding(new Insets(PANEL_PADDING));
    setConnected(connected);
    initLayout(devices);
  }

  private void initLayout(List<Device> devices) {
    emptyLabel.getStyleClass().add("devices-empty");

    setDevices(devices);

    VBox listSection = createScrollPane();
    VBox.setVgrow(listSection, Priority.ALWAYS);

    getChildren().addAll(
        createTopBar(),
        createSearchBar(),
        listSection,
        createRefreshButton());
  }

  private HBox createTopBar() {
    Button powerBtn = Elements.createPowerToggle(false);
    powerBtn.setOnAction(e -> onToggleConnection.run());

    Label nameLabel = new Label(deviceName);
    nameLabel.getStyleClass().add("device-title");

    statusLabel.getStyleClass().add("device-subtitle");

    VBox titleBox = new VBox(2, nameLabel, statusLabel);

    Region topSpacer = new Region();

    HBox.setHgrow(topSpacer, Priority.ALWAYS);
    HBox topBar = new HBox(10, powerBtn, titleBox, topSpacer);

    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.setPadding(new Insets(0));

    return topBar;
  }

  private HBox createSearchBar() {
    TextField searchField = new TextField();

    searchField.setPromptText("Search devices");
    searchField.getStyleClass().add("search-field");
    searchField.textProperty().addListener((obs, oldV, newV) -> filter(newV));

    HBox searchBar = new HBox(searchField);
    searchBar.setPadding(new Insets(0));

    HBox.setHgrow(searchField, Priority.ALWAYS);

    return searchBar;
  }

  private VBox createScrollPane() {
    ScrollPane scrollPane = new ScrollPane(rowsBox);
    scrollPane.setFitToWidth(true);
    scrollPane.getStyleClass().add("groups-scroll");
    scrollPane.setPadding(new Insets(0));

    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    VBox wrapper = new VBox(scrollPane);
    wrapper.setPadding(new Insets(0));

    VBox.setVgrow(wrapper, Priority.ALWAYS);

    return wrapper;
  }

  private Button createRefreshButton() {
    Button refreshBtn = Elements.createIconButton(IconPaths.REFRESH, "fab fab-secondary");
    refreshBtn.setOnAction(e -> onRefresh.run());

    return refreshBtn;
  }

  public void setDevices(List<Device> devices) {
    rowsBox.getChildren().clear();

    if (devices == null || devices.isEmpty()) {
      rowsBox.getChildren().add(emptyLabel);
      return;
    }

    List<Device> paired = devices.stream().filter(Device::isPaired).toList();
    List<Device> unpaired = devices.stream().filter(d -> !d.isPaired()).toList();

    if (!paired.isEmpty()) {
      rowsBox.getChildren().add(createSectionLabel("Paired Devices"));
      for (Device device : paired) {
        rowsBox.getChildren().add(new DeviceRow(device, onSelectDevice, onUnpairDevice));
      }
    }

    if (!unpaired.isEmpty()) {
      rowsBox.getChildren().add(createSectionLabel("Available Devices"));

      for (Device device : unpaired) {
        rowsBox.getChildren().add(new DeviceRow(device, onSelectDevice, onUnpairDevice));
      }
    }

    if (paired.isEmpty() && unpaired.isEmpty()) {
      rowsBox.getChildren().add(emptyLabel);
    }
  }

  private Label createSectionLabel(String text) {
    Label label = new Label(text);
    label.getStyleClass().add("devices-section-label");

    return label;
  }

  public void setConnected(boolean connected) {
    statusLabel.setText(connected ? "Connected" : "Disconnected");
  }

  private void filter(String query) {
    String q = query == null ? "" : query.trim().toLowerCase();

    for (Node node : rowsBox.getChildren()) {
      if (node instanceof DeviceRow row) {
        boolean matches = q.isEmpty() || row.getDevice().getName().toLowerCase().contains(q);

        node.setVisible(matches);
        node.setManaged(matches);
      }
    }
  }
}
