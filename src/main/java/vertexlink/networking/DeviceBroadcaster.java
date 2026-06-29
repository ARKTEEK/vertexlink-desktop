package vertexlink.networking;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class DeviceBroadcaster {
  private JmDNS jmdns;
  private final String serviceType = "_vertexlink._tcp.local.";

  public void start(String deviceName, int port) {
    try {
      jmdns = JmDNS.create(InetAddress.getLocalHost());

      ServiceInfo serviceInfo = ServiceInfo.create(
          serviceType,
          deviceName,
          port,
          "VertexLink Service");

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
