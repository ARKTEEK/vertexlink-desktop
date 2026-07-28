package vertexlink.controller;

import java.security.KeyPair;
import java.security.PublicKey;

import vertexlink.device.Device;
import vertexlink.device.DeviceIdentity;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.NetworkManager;
import vertexlink.network.discovery.DeviceBroadcaster;
import vertexlink.network.discovery.DeviceScanner;
import vertexlink.network.security.CryptoUtils;
import vertexlink.network.server.ClientHandler;
import vertexlink.ui.resources.DeviceState;

public class DashboardController {
  private static final int TCP_PORT = 28401;

  private KeyPair desktopKeyPair;
  private final DeviceIdentity identity = new DeviceIdentity();
  private final DeviceBroadcaster broadcaster = new DeviceBroadcaster();
  private final DeviceState deviceState = new DeviceState();
  private final NetworkManager networkManager = new NetworkManager(TCP_PORT);
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
    networkManager.setPairingListener(this::onPairRequest);
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
    networkManager.sendPairDecision(client, accepted, accepted ? null : "Rejected by user");

    if (accepted) {
      deviceState.upsertDevice(addressKey, deviceName, deviceId);

      if (eventListener != null) {
        eventListener.onDeviceListUpdated(deviceState.getDevicesList());
      }
    } else {
      deviceState.removePendingClient(addressKey);

      client.close();
    }
  }

  private void onPairRequest(String deviceId, String deviceName, String clientPublicKeyStr, ClientHandler client) {
    String addressKey = client.getAddress().getHostAddress();

    desktopKeyPair = CryptoUtils.generateKeyPair();

    PublicKey clientPublicKey = CryptoUtils.decodePublicKey(clientPublicKeyStr);
    String desktopPublicKeyStr = CryptoUtils.encodePublicKey(desktopKeyPair.getPublic());

    networkManager.sendPairAck(client, identity.getId(), "VertexLink Desktop", desktopPublicKeyStr);

    String calculatedPin = CryptoUtils.calculatePin(desktopKeyPair.getPrivate(), clientPublicKey);

    deviceState.addPendingClient(addressKey, client);

    if (eventListener != null) {
      eventListener.onPairRequest(deviceName, addressKey, calculatedPin, client, deviceId);
    }
  }

  private void onDeviceDiscovered(String id, String name, String address) {
    deviceState.upsertDevice(address, name, id);

    if (eventListener != null) {
      eventListener.onDeviceListUpdated(deviceState.getDevicesList());
    }
  }

  private void onDataReceived(String data, ClientHandler client) {
    if (eventListener != null) {
      eventListener.onDataReceived(data, client.getAddress().getHostAddress());
    }
  }

  public boolean isConnected() {
    return connected;
  }

  public java.util.List<Device> getDevicesList() {
    return deviceState.getDevicesList();
  }
}
