package vertexlink.enums;

public enum DeviceStatus {
  ONLINE("#4ade80"),
  OFFLINE("#4b4470");

  private final String colorHex;

  DeviceStatus(String colorHex) {
    this.colorHex = colorHex;
  }

  public String getColorHex() {
    return colorHex;
  }
}
