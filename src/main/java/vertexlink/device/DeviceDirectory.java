package vertexlink.device;

import java.util.List;

import vertexlink.network.server.ClientHandler;
import vertexlink.pairing.PairingService;

public class DeviceDirectory {
  private final DeviceState deviceState;
  private final PairingService pairingService;

  public DeviceDirectory(DeviceState deviceState, PairingService pairingService) {
    this.deviceState = deviceState;
    this.pairingService = pairingService;
  }

  public Device upsertDiscovered(String address, String name, String id) {
    Device device = deviceState.upsertDevice(address, name, id);
    device.setPaired(pairingService.isPaired(id));

    return device;
  }

  public Device markPaired(String addressKey, String deviceName, String deviceId) {
    Device device = deviceState.upsertDevice(addressKey, deviceName, deviceId);
    device.setPaired(true);

    return device;
  }

  public void addPendingClient(String addressKey, ClientHandler client) {
    deviceState.addPendingClient(addressKey, client);
  }

  public void removePendingClient(String addressKey) {
    deviceState.removePendingClient(addressKey);
  }

  public void unpair(Device device) {
    pairingService.forget(device.getClientId());
    device.setPaired(false);
  }

  public void clear() {
    deviceState.clear();
  }

  public List<Device> getDevicesList() {
    return deviceState.getDevicesList();
  }
}
