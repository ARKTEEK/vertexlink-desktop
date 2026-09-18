package vertexlink;

import java.util.List;
import java.util.function.Consumer;

import vertexlink.device.Device;
import vertexlink.device.DeviceDirectory;
import vertexlink.listener.DashboardEventListener;
import vertexlink.network.lifecycle.ConnectionLifecycleManager;
import vertexlink.network.server.ClientHandler;
import vertexlink.pairing.PairingCoordinator;

public class AppCoordinator {
  private final PairingCoordinator pairing;
  private final DeviceDirectory devices;

  private ConnectionLifecycleManager connectionLifecycle;
  private DashboardEventListener eventListener;
  private Consumer<Boolean> connectionStateListener;

  public AppCoordinator(
      PairingCoordinator pairing,
      DeviceDirectory devices) {
    this.pairing = pairing;
    this.devices = devices;
  }

  public void setConnectionLifecycle(ConnectionLifecycleManager connectionLifecycle) {
    this.connectionLifecycle = connectionLifecycle;

    setupConnectionLifecycleListener();
  }

  public void setEventListener(DashboardEventListener listener) {
    this.eventListener = listener;
    this.pairing.setEventListener(listener);
  }

  public void setConnectionStateListener(Consumer<Boolean> listener) {
    this.connectionStateListener = listener;
  }

  public void setConnectionTransitionListener(Consumer<Boolean> listener) {
    if (this.connectionLifecycle != null) {
      this.connectionLifecycle.setConnectionTransitionListener(listener);
    }
  }

  private void setupConnectionLifecycleListener() {
    if (this.connectionLifecycle == null) {
      return;
    }

    this.connectionLifecycle.setConnectionStateListener(isConnected -> {
      if (this.connectionStateListener != null) {
        this.connectionStateListener.accept(isConnected);
      }

      if (!isConnected) {
        notifyDevicesChanged();
      }
    });
  }

  public void toggleConnection() {
    if (this.connectionLifecycle != null) {
      this.connectionLifecycle.toggle();
    }
  }

  public void refreshDevices() {
    if (this.connectionLifecycle != null) {
      this.connectionLifecycle.refreshDevices();
    }
  }

  public void handlePairingResponse(
      ClientHandler client,
      String addressKey,
      String deviceId,
      String deviceName,
      boolean accepted) {
    this.pairing.handlePairingResponse(client, addressKey, deviceId, deviceName, accepted);
  }

  public void resolveConnectionConflict(
      ClientHandler client,
      String addressKey,
      String deviceId,
      String deviceName,
      boolean keepNew) {
    this.pairing.resolveConnectionConflict(client, addressKey, deviceId, deviceName, keepNew);
  }

  public void disconnectConnectedDevice() {
    this.pairing.disconnectConnectedDevice();
  }

  public void onDeviceDiscovered(
      String id,
      String name,
      String address) {
    this.devices.upsertDiscovered(address, name, id);

    notifyDevicesChanged();
  }

  public void onDataReceived(
      String data,
      ClientHandler client) {
    if (this.eventListener == null) {
      return;
    }

    String hostAddress = null;

    if (client != null && client.getAddress() != null) {
      hostAddress = client.getAddress().getHostAddress();
    }

    this.eventListener.onDataReceived(data, hostAddress);
  }

  public void unpairDevice(Device device) {
    this.devices.unpair(device);

    notifyDevicesChanged();
  }

  private void notifyDevicesChanged() {
    if (this.eventListener != null) {
      this.eventListener.onDeviceListUpdated(this.devices.getDevicesList());
    }
  }

  public boolean isConnected() {
    if (this.connectionLifecycle == null) {
      return false;
    }

    return this.connectionLifecycle.isConnected();
  }

  public Device getConnectedDevice() {
    return this.devices.getConnectedDevice();
  }

  public List<Device> getDevicesList() {
    return this.devices.getDevicesList();
  }

  public void shutdown() {
    if (this.connectionLifecycle != null) {
      this.connectionLifecycle.shutdown();
    }
  }
}
