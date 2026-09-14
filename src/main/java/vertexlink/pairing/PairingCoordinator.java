package vertexlink.pairing;

import vertexlink.device.DeviceDirectory;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.protocol.ProtocolMessenger;
import vertexlink.network.server.ClientHandler;

public class PairingCoordinator {
  private final PairingService pairingService;
  private final ProtocolMessenger messenger;
  private final DeviceDirectory devices;
  private final String desktopId;
  private final String desktopName;

  private DashboardEventListener eventListener;

  public PairingCoordinator(PairingService pairingService, ProtocolMessenger messenger,
      DeviceDirectory devices, String desktopId, String desktopName) {
    this.pairingService = pairingService;
    this.messenger = messenger;
    this.devices = devices;
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

    messenger.sendAuthResult(client, ok, ok ? null : "Unknown device or invalid token");

    if (ok) {
      String addressKey = client.getAddress().getHostAddress();
      String deviceName = pairingService.findPaired(deviceId).get().deviceName();

      devices.markPaired(addressKey, deviceName, deviceId);

      notifyDevicesChanged();
    } else {
      client.close();
    }
  }

  public void handlePairingResponse(ClientHandler client, String addressKey, String deviceId, String deviceName,
      boolean accepted) {
    pairingService.discardChallenge(addressKey);

    if (accepted) {
      String token = pairingService.completePairing(deviceId, deviceName);

      messenger.sendPairSuccess(client, desktopId, desktopName, token);
      devices.markPaired(addressKey, deviceName, deviceId);

      notifyDevicesChanged();
    } else {
      devices.removePendingClient(addressKey);
      messenger.sendPairDecision(client, false, "Rejected by user");

      client.close();
    }
  }

  private void notifyDevicesChanged() {
    if (eventListener != null) {
      eventListener.onDeviceListUpdated(devices.getDevicesList());
    }
  }
}
