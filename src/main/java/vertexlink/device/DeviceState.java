package vertexlink.device;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import vertexlink.enums.DeviceStatus;
import vertexlink.network.server.ClientHandler;

public class DeviceState {
  private final Map<String, Device> discoveredDevices = new LinkedHashMap<>();
  private final Map<String, ClientHandler> pendingClients = new LinkedHashMap<>();

  public Device upsertDevice(String address, String name, String clientId) {
    Device device = discoveredDevices.get(address);

    if (device == null) {
      device = new Device(UUID.randomUUID().toString(), name, clientId);

      discoveredDevices.put(address, device);
    } else {
      device.setName(name);
      device.setClientId(clientId);
    }

    device.setStatus(DeviceStatus.ONLINE);
    device.setIpv4Address(address);

    return device;
  }

  public void addPendingClient(String address, ClientHandler client) {
    pendingClients.put(address, client);
  }

  public ClientHandler removePendingClient(String address) {
    return pendingClients.remove(address);
  }

  public List<Device> getDevicesList() {
    return new ArrayList<>(discoveredDevices.values());
  }

  public Device getDevice(String address) {
    return discoveredDevices.get(address);
  }

  public void clear() {
    discoveredDevices.clear();
    pendingClients.clear();
  }
}
