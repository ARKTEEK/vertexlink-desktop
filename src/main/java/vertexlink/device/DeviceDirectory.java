package vertexlink.device;

import java.util.List;

import vertexlink.network.server.ClientHandler;
import vertexlink.pairing.PairingService;

public class DeviceDirectory {
  private final DeviceState deviceState;
  private final PairingService pairingService;

  private String connectedDeviceId;

  public DeviceDirectory(DeviceState deviceState, PairingService pairingService) {
    this.deviceState = deviceState;
    this.pairingService = pairingService;
  }

  public Device upsertDiscovered(String address, String name, String id) {
    Device device = deviceState.upsertDevice(address, name, id);
    device.setPaired(pairingService.isPaired(id));
    device.setConnected(id != null && id.equals(connectedDeviceId));

    return device;
  }

  public Device markPaired(String addressKey, String deviceName, String deviceId) {
    Device device = deviceState.upsertDevice(addressKey, deviceName, deviceId);
    device.setPaired(true);

    return device;
  }

  public Device markConnected(String addressKey, String deviceName, String deviceId, ClientHandler client) {
    Device device = deviceState.upsertDevice(addressKey, deviceName, deviceId);
    device.setPaired(true);
    device.setConnected(true);

    connectedDeviceId = deviceId;
    deviceState.setConnectedClient(deviceId, client);

    return device;
  }

  public Device getConnectedDevice() {
    if (connectedDeviceId == null) {
      return null;
    }

    return deviceState.findByClientId(connectedDeviceId);
  }

  public boolean hasConnectedDevice() {
    return connectedDeviceId != null;
  }

  public void disconnectClient(String deviceId) {
    if (deviceId == null) {
      return;
    }

    ClientHandler client = deviceState.getConnectedClient(deviceId);

    if (client != null) {
      client.close();
    }

    clearConnectedState(deviceId);
  }

  public Device disconnectClientHandler(ClientHandler client) {
    String deviceId = deviceState.findConnectedDeviceId(client);

    if (deviceId == null) {
      return null;
    }

    Device device = deviceState.findByClientId(deviceId);

    clearConnectedState(deviceId);

    return device;
  }

  private void clearConnectedState(String deviceId) {
    Device device = deviceState.findByClientId(deviceId);

    if (device != null) {
      device.setConnected(false);
    }

    deviceState.removeConnectedClient(deviceId);

    if (deviceId.equals(connectedDeviceId)) {
      connectedDeviceId = null;
    }
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

    if (device.getClientId() != null && device.getClientId().equals(connectedDeviceId)) {
      disconnectClient(device.getClientId());
    }
  }

  public void clear() {
    deviceState.clear();
    connectedDeviceId = null;
  }

  public List<Device> getDevicesList() {
    return deviceState.getDevicesList();
  }
}
