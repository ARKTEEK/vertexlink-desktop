package vertexlink.networking;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.function.BiConsumer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class DeviceScanner {
  private JmDNS jmdns;
  private ServiceListener listener;
  private final String deviceId;
  private final String serviceType = "_vertexlink._tcp.local.";
  private final BiConsumer<String, String> onDeviceDiscovered;

  public DeviceScanner(BiConsumer<String, String> onDeviceDiscovered, String deviceId) {
    this.onDeviceDiscovered = onDeviceDiscovered;
    this.deviceId = deviceId;
  }

  public void start() {
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
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
    if (jmdns == null) {
      return;
    }

    if (listener == null) {
      return;
    }

    jmdns.removeServiceListener(serviceType, listener);

    try {
      jmdns.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }

    jmdns = null;
    listener = null;
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
