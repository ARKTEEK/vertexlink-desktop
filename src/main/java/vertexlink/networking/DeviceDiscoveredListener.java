package vertexlink.networking;

public interface DeviceDiscoveredListener {
  void onDiscovered(String id, String name, String address);
}
