package vertexlink.listener;

public interface DeviceDiscoveredListener {
  void onDiscovered(String id, String name, String address);
}
