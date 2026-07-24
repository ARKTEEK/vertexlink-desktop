package vertexlink.device;

import vertexlink.enums.DeviceStatus;

public class Device {
  private String id;
  private String name;
  private String clientId;
  private String ipv4Address;
  private String ipv6Address;
  private String appVersion;
  private DeviceStatus status;

  public Device(String id, String name, String clientId) {
    this.id = id;
    this.name = name;
    this.clientId = clientId;
    this.status = DeviceStatus.OFFLINE;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientId() {
    return clientId;
  }

  public String getIpv4Address() {
    return ipv4Address;
  }

  public void setIpv4Address(String v) {
    this.ipv4Address = v;
  }

  public String getIpv6Address() {
    return ipv6Address;
  }

  public void setIpv6Address(String v) {
    this.ipv6Address = v;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String v) {
    this.appVersion = v;
  }

  public DeviceStatus getStatus() {
    return status;
  }

  public void setStatus(DeviceStatus status) {
    this.status = status;
  }
}
