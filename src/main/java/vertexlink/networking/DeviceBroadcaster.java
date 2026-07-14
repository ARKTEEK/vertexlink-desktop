package vertexlink.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class DeviceBroadcaster {
  private JmDNS jmdns;
  private final String serviceType = "_vertexlink._tcp.local.";

  public void start(String deviceName, int port, String deviceId) {
    try {
      jmdns = JmDNS.create(InetAddress.getLocalHost());

      Map<String, String> properties = new HashMap<>();
      properties.put("device_id", deviceId);
      properties.put("version", "1");
      properties.put("type", "desktop");

      ServiceInfo serviceInfo = ServiceInfo.create(
          serviceType,
          deviceName,
          port,
          0,
          0,
          properties);

      jmdns.registerService(serviceInfo);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
    if (jmdns == null) {
      return;
    }

    jmdns.unregisterAllServices();

    try {
      jmdns.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }

    jmdns = null;
  }
}
