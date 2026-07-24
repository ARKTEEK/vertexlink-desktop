package vertexlink.enums;

public enum DeviceStatus {
  ONLINE("#4ade80"),
  BUSY("#ec4899"),
  AWAY("#f59e0b"),
  OFFLINE("#4b4470");

  private final String colorHex;

  DeviceStatus(String colorHex) {
    this.colorHex = colorHex;
  }

  public String getColorHex() {
    return colorHex;
  }
}
