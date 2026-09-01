package vertexlink.controller;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Optional;
import java.util.UUID;

import vertexlink.device.Device;
import vertexlink.device.DeviceIdentity;
import vertexlink.device.DeviceState;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.NetworkManager;
import vertexlink.network.discovery.DeviceBroadcaster;
import vertexlink.network.discovery.DeviceScanner;
import vertexlink.network.security.CryptoUtils;
import vertexlink.network.server.ClientHandler;
import vertexlink.store.PairedDeviceStore;

public class DashboardController {
  private static final int TCP_PORT = 28401;

  private KeyPair desktopKeyPair;
  private final DeviceIdentity identity = new DeviceIdentity();
  private final DeviceBroadcaster broadcaster = new DeviceBroadcaster();
  private final DeviceState deviceState = new DeviceState();
  private final NetworkManager networkManager = new NetworkManager(TCP_PORT);
  private final PairedDeviceStore pairedDevices = new PairedDeviceStore();
  private final DeviceScanner scanner;

  private boolean connected = false;
  private DashboardEventListener eventListener;

  public DashboardController() {
    this.scanner = new DeviceScanner((id, name, address) -> {
      onDeviceDiscovered(id, name, address);
    }, identity.getId());

    setupNetworkListeners();
  }

  public void setEventListener(DashboardEventListener listener) {
    this.eventListener = listener;
  }

  private void setupNetworkListeners() {
    networkManager.setPairingListener(new NetworkManager.PairingListener() {

      @Override
      public void onPairRequest(String deviceId, String deviceName, String publicKey, ClientHandler client) {
        DashboardController.this.onPairRequest(deviceId, deviceName, publicKey, client);
      }

      @Override
      public void onAuth(String deviceId, String token, ClientHandler client) {
        DashboardController.this.onAuth(deviceId, token, client);
      }

      @Override
      public void onDisconnect(ClientHandler client) {
        throw new UnsupportedOperationException("Unimplemented method 'onDisconnect'");
      }
    });
    networkManager.setDataListener(this::onDataReceived);
  }

  public void toggleConnection() {
    connected = !connected;

    if (connected) {
      broadcaster.start("DesktopServer", TCP_PORT, identity.getId());
      scanner.start();
      networkManager.start();
    } else {
      scanner.stop();
      broadcaster.stop();
      networkManager.stop();
      deviceState.clear();

      if (eventListener != null) {
        eventListener.onDeviceListUpdated(deviceState.getDevicesList());
      }
    }
  }

  public void refreshDevices() {
    scanner.stop();
    scanner.start();
  }

  public void handlePairingResponse(ClientHandler client, String addressKey, String deviceId, String deviceName,
      boolean accepted) {
    if (accepted) {
      String token = UUID.randomUUID().toString();

      pairedDevices.save(deviceId, deviceName, token);

      networkManager.sendPairSuccess(client, identity.getId(), "VertexLink Desktop", token);

      Device device = deviceState.upsertDevice(addressKey, deviceName, deviceId);
      device.setPaired(true);

      if (eventListener != null) {
        eventListener.onDeviceListUpdated(deviceState.getDevicesList());
      }
    } else {
      deviceState.removePendingClient(addressKey);

      networkManager.sendPairDecision(client, false, "Rejected by user");

      client.close();
    }
  }

  private void onAuth(String deviceId, String token, ClientHandler client) {
    Optional<PairedDeviceStore.PairedDevice> stored = pairedDevices.find(deviceId);
    boolean ok = stored.isPresent() && stored.get().token().equals(token);

    networkManager.sendAuthResult(client, ok, ok ? null : "Unknown device or invalid token");

    if (ok) {
      String addressKey = client.getAddress().getHostAddress();
      Device device = deviceState.upsertDevice(addressKey, stored.get().deviceName(), deviceId);

      device.setPaired(true);

      if (eventListener != null) {
        eventListener.onDeviceListUpdated(deviceState.getDevicesList());
      }
    } else {
      client.close();
    }
  }

  private void onPairRequest(String deviceId, String deviceName, String clientPublicKeyStr, ClientHandler client) {
    String addressKey = client.getAddress().getHostAddress();

    desktopKeyPair = CryptoUtils.generateKeyPair();

    PublicKey clientPublicKey = CryptoUtils.decodePublicKey(clientPublicKeyStr);
    String desktopPublicKeyStr = CryptoUtils.encodePublicKey(desktopKeyPair.getPublic());

    networkManager.sendPairChallenge(client, identity.getId(), "VertexLink Desktop", desktopPublicKeyStr);

    String calculatedPin = CryptoUtils.calculatePin(desktopKeyPair.getPrivate(), clientPublicKey);

    deviceState.addPendingClient(addressKey, client);

    if (eventListener != null) {
      eventListener.onPairRequest(deviceName, addressKey, calculatedPin, client, deviceId);
    }
  }

  private void onDeviceDiscovered(String id, String name, String address) {
    Device device = deviceState.upsertDevice(address, name, id);
    device.setPaired(pairedDevices.find(id).isPresent());

    if (eventListener != null) {
      eventListener.onDeviceListUpdated(deviceState.getDevicesList());
    }
  }

  private void onDataReceived(String data, ClientHandler client) {
    if (eventListener != null) {
      eventListener.onDataReceived(data, client.getAddress().getHostAddress());
    }
  }

  public void unpairDevice(Device device) {
    pairedDevices.remove(device.getClientId());
    device.setPaired(false);

    if (eventListener != null) {
      eventListener.onDeviceListUpdated(deviceState.getDevicesList());
    }
  }

  public boolean isConnected() {
    return connected;
  }

  public java.util.List<Device> getDevicesList() {
    return deviceState.getDevicesList();
  }
}
