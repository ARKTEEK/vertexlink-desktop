package vertexlink.controller;

import java.util.List;

import vertexlink.device.Device;
import vertexlink.device.DeviceDirectory;
import vertexlink.device.DeviceIdentity;
import vertexlink.device.DeviceState;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.NetworkManager;
import vertexlink.network.discovery.DeviceBroadcaster;
import vertexlink.network.discovery.DeviceScanner;
import vertexlink.network.protocol.ProtocolMessenger;
import vertexlink.network.server.ClientHandler;
import vertexlink.pairing.PairingCoordinator;
import vertexlink.pairing.PairingService;
import vertexlink.store.PairedDeviceStore;

public class DashboardController {
  private static final int TCP_PORT = 28401;
  private static final String DESKTOP_NAME = "VertexLink Desktop";

  private final DeviceIdentity identity = new DeviceIdentity();
  private final DeviceBroadcaster broadcaster = new DeviceBroadcaster();
  private final NetworkManager networkManager = new NetworkManager(TCP_PORT);
  private final ProtocolMessenger messenger = new ProtocolMessenger();
  private final PairingService pairingService = new PairingService(new PairedDeviceStore());
  private final DeviceDirectory devices = new DeviceDirectory(new DeviceState(), pairingService);
  private final PairingCoordinator pairing;
  private final DeviceScanner scanner;

  private boolean connected = false;
  private DashboardEventListener eventListener;

  public DashboardController() {
    this.pairing = new PairingCoordinator(pairingService, messenger, devices, identity.getId(), DESKTOP_NAME);

    this.scanner = new DeviceScanner((id, name, address) -> onDeviceDiscovered(id, name, address), identity.getId());

    setupNetworkListeners();
  }

  public void setEventListener(DashboardEventListener listener) {
    this.eventListener = listener;
    this.pairing.setEventListener(listener);
  }

  private void setupNetworkListeners() {
    networkManager.setPairingListener(new NetworkManager.PairingListener() {

      @Override
      public void onPairRequest(String deviceId, String deviceName, String publicKey, ClientHandler client) {
        pairing.onPairRequest(deviceId, deviceName, publicKey, client);
      }

      @Override
      public void onAuth(String deviceId, String token, ClientHandler client) {
        pairing.onAuth(deviceId, token, client);
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
      devices.clear();
      notifyDevicesChanged();
    }
  }

  public void refreshDevices() {
    scanner.stop();
    scanner.start();
  }

  public void handlePairingResponse(ClientHandler client, String addressKey, String deviceId, String deviceName,
      boolean accepted) {
    pairing.handlePairingResponse(client, addressKey, deviceId, deviceName, accepted);
  }

  private void onDeviceDiscovered(String id, String name, String address) {
    devices.upsertDiscovered(address, name, id);
    notifyDevicesChanged();
  }

  private void onDataReceived(String data, ClientHandler client) {
    if (eventListener != null) {
      eventListener.onDataReceived(data, client.getAddress().getHostAddress());
    }
  }

  public void unpairDevice(Device device) {
    devices.unpair(device);
    notifyDevicesChanged();
  }

  private void notifyDevicesChanged() {
    if (eventListener != null) {
      eventListener.onDeviceListUpdated(devices.getDevicesList());
    }
  }

  public boolean isConnected() {
    return connected;
  }

  public List<Device> getDevicesList() {
    return devices.getDevicesList();
  }
}
