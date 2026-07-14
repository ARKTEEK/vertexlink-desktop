package vertexlink.networking;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class DeviceScanner {
  private JmDNS jmdns;
  private ServiceListener listener;
  private ScheduledExecutorService scheduler;
  private final String deviceId;
  private final String serviceType = "_vertexlink._tcp.local.";
  private final BiConsumer<String, String> onDeviceDiscovered;

  public DeviceScanner(BiConsumer<String, String> onDeviceDiscovered, String deviceId) {
    this.onDeviceDiscovered = onDeviceDiscovered;
    this.deviceId = deviceId;
  }

  public void start() {
    if (this.scheduler != null) {
      return;
    }

    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    this.scheduler.scheduleAtFixedRate(() -> {
      startScan();
    }, 0, 10, TimeUnit.SECONDS);
  }

  private void startScan() {
    try {
      jmdns = JmDNS.create(InetAddress.getLocalHost());

      listener = new ServiceListener() {
        @Override
        public void serviceAdded(ServiceEvent event) {
          jmdns.requestServiceInfo(event.getType(), event.getName());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
          String name = event.getName();
          String address = event.getInfo().getHostAddresses()[0];
          String id = event.getInfo().getPropertyString("device_id");

          if (!isIPv4(address)) {
            return;
          }

          if (id.equals(deviceId)) {
            return;
          }

          onDeviceDiscovered.accept(name, address);
        }
      };

      jmdns.addServiceListener(serviceType, listener);

      this.scheduler.schedule(() -> {
        stopScan();
      }, 3, TimeUnit.SECONDS);

    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  private void stopScan() {
    if (this.jmdns == null) {
      return;
    }

    if (this.listener == null) {
      return;
    }

    this.jmdns.removeServiceListener(serviceType, this.listener);

    try {
      this.jmdns.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }

    this.jmdns = null;
    this.listener = null;
  }

  public void stop() {
    if (this.scheduler != null) {
      this.scheduler.shutdown();
      this.scheduler = null;
    }

    stopScan();
  }

  private boolean isIPv4(String address) {
    try {
      InetAddress inetAddress = InetAddress.getByName(address);

      if (inetAddress instanceof Inet4Address) {
        return true;
      }
    } catch (Exception exception) {

    }

    return false;
  }
}
