package vertexlink.pairing;

import vertexlink.device.Device;
import vertexlink.device.DeviceDirectory;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.NetworkManager;
import vertexlink.network.protocol.ProtocolMessenger;
import vertexlink.network.security.CryptoUtils;
import vertexlink.network.server.ClientHandler;

public class PairingCoordinator {
  private final PairingService pairingService;
  private final ProtocolMessenger messenger;
  private final DeviceDirectory devices;
  private final NetworkManager networkManager;
  private final String desktopId;
  private final String desktopName;

  private DashboardEventListener eventListener;

  public PairingCoordinator(PairingService pairingService, ProtocolMessenger messenger,
      DeviceDirectory devices, NetworkManager networkManager, String desktopId, String desktopName) {
    this.pairingService = pairingService;
    this.messenger = messenger;
    this.devices = devices;
    this.networkManager = networkManager;
    this.desktopId = desktopId;
    this.desktopName = desktopName;
  }

  public void setEventListener(DashboardEventListener eventListener) {
    this.eventListener = eventListener;
  }

  public void onPairRequest(String deviceId, String deviceName, String clientPublicKeyStr, ClientHandler client) {
    String addressKey = client.getAddress().getHostAddress();
    PairingService.Challenge challenge = pairingService.createChallenge(addressKey, clientPublicKeyStr);

    messenger.sendPairChallenge(client, desktopId, desktopName, challenge.desktopPublicKey);
    devices.addPendingClient(addressKey, client);

    if (eventListener != null) {
      eventListener.onPairRequest(deviceName, addressKey, challenge.pin, client, deviceId);
    }
  }

  public void onAuth(String deviceId, String token, ClientHandler client) {
    boolean ok = pairingService.verifyAuth(deviceId, token);

    if (!ok) {
      messenger.sendAuthResult(client, false, "Unknown device or invalid token");
      client.close();
      return;
    }

    String addressKey = client.getAddress().getHostAddress();
    String deviceName = pairingService.findPaired(deviceId).get().deviceName();

    Device existingConnected = devices.getConnectedDevice();

    if (existingConnected == null || deviceId.equals(existingConnected.getClientId())) {
      completeConnection(client, addressKey, deviceId, deviceName, token);
      return;
    }

    devices.addPendingClient(addressKey, client);

    if (eventListener != null) {
      eventListener.onConnectionConflict(existingConnected, deviceName, addressKey, deviceId, client);
    }
  }

  public void resolveConnectionConflict(ClientHandler client, String addressKey, String deviceId, String deviceName,
      boolean keepNew) {
    devices.removePendingClient(addressKey);

    if (keepNew) {
      Device previouslyConnected = devices.getConnectedDevice();

      if (previouslyConnected != null) {
        devices.disconnectClient(previouslyConnected.getClientId());
      }

      String token = pairingService.findPaired(deviceId).get().token();
      completeConnection(client, addressKey, deviceId, deviceName, token);
    } else {
      messenger.sendAuthResult(client, false, "Another device is already connected");
      client.close();
    }
  }

  public void handlePairingResponse(ClientHandler client, String addressKey, String deviceId, String deviceName,
      boolean accepted) {
    pairingService.discardChallenge(addressKey);

    if (accepted) {
      String token = pairingService.completePairing(deviceId, deviceName);

      byte[] sessionKey = CryptoUtils.deriveKeyFromToken(token);
      networkManager.setUdpSessionKey(sessionKey);
      networkManager.setTrustedUdpAddress(client.getAddress());

      messenger.sendPairSuccess(client, desktopId, desktopName, token);
      devices.markPaired(addressKey, deviceName, deviceId);

      notifyDevicesChanged();
    } else {
      devices.removePendingClient(addressKey);
      messenger.sendPairDecision(client, false, "Rejected by user");

      client.close();
    }
  }

  public void onDisconnect(ClientHandler client) {
    Device disconnected = devices.disconnectClientHandler(client);

    if (disconnected != null) {
      networkManager.clearTrustedUdpAddress();
      notifyDevicesChanged();
      notifyConnectedDeviceChanged();
    }
  }

  public void disconnectConnectedDevice() {
    Device connected = devices.getConnectedDevice();

    if (connected == null) {
      return;
    }

    devices.disconnectClient(connected.getClientId());
    networkManager.clearTrustedUdpAddress();

    notifyDevicesChanged();
    notifyConnectedDeviceChanged();
  }

  private void completeConnection(ClientHandler client, String addressKey, String deviceId, String deviceName,
      String token) {
    byte[] sessionKey = CryptoUtils.deriveKeyFromToken(token);
    networkManager.setUdpSessionKey(sessionKey);
    networkManager.setTrustedUdpAddress(client.getAddress());

    messenger.sendAuthResult(client, true, null);
    devices.markConnected(addressKey, deviceName, deviceId, client);

    notifyDevicesChanged();
    notifyConnectedDeviceChanged();
  }

  private void notifyDevicesChanged() {
    if (eventListener != null) {
      eventListener.onDeviceListUpdated(devices.getDevicesList());
    }
  }

  private void notifyConnectedDeviceChanged() {
    if (eventListener != null) {
      eventListener.onConnectedDeviceChanged(devices.getConnectedDevice());
    }
  }
}
