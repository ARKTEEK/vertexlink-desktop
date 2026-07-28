package vertexlink.listener;

import vertexlink.device.Device;
import vertexlink.network.server.ClientHandler;

public interface DashboardEventListener {
  void onPairRequest(String deviceName, String addressKey, String calculatedPin, ClientHandler client, String deviceId);

  void onDeviceListUpdated(java.util.List<Device> devices);

  void onDataReceived(String data, String hostAddress);
}
