package vertexlink.network;

import java.net.InetAddress;

import vertexlink.controller.MouseController;
import vertexlink.network.protocol.Protocol;
import vertexlink.network.server.ClientHandler;
import vertexlink.network.server.TCPServer;
import vertexlink.network.server.UDPServer;

public class NetworkManager {
  private final int tcpPort;
  private final int udpPort;
  private final MouseInputHandler mouseInputHandler = new MouseInputHandler();

  private TCPServer tcpServer;
  private UDPServer udpServer;
  private boolean isRunning;
  private PairingListener pairingListener;
  private DataListener dataListener;

  private volatile InetAddress trustedUdpAddress;

  public interface PairingListener {
    void onPairRequest(String deviceId, String deviceName, String publicKey, ClientHandler client);

    void onAuth(String deviceId, String token, ClientHandler client);

    void onDisconnect(ClientHandler client);
  }

  public interface DataListener {
    void onData(String data, ClientHandler client);
  }

  public NetworkManager(int tcpPort, int udpPort) {
    this.tcpPort = tcpPort;
    this.udpPort = udpPort;
  }

  public void setPairingListener(PairingListener listener) {
    this.pairingListener = listener;
  }

  public void setDataListener(DataListener listener) {
    this.dataListener = listener;
  }

  public void setMouseController(MouseController controller) {
    this.mouseInputHandler.setMouseController(controller);
  }

  public void setTrustedUdpAddress(InetAddress address) {
    this.trustedUdpAddress = address;
  }

  public void clearTrustedUdpAddress() {
    this.trustedUdpAddress = null;
  }

  public void start() {
    if (isRunning) {
      System.out.println("[Network] Already running!");
      return;
    }

    isRunning = true;
    tcpServer = new TCPServer(this, tcpPort);
    tcpServer.start();

    udpServer = new UDPServer(this, udpPort);
    udpServer.start();

    System.out.println("[Network] Server started!");
  }

  public void stop() {
    if (!isRunning) {
      return;
    }

    System.out.println("[Network] Shutting down...");

    isRunning = false;

    if (tcpServer != null) {
      tcpServer.shutdown();
      tcpServer = null;
    }

    if (udpServer != null) {
      udpServer.shutdown();
      udpServer = null;
    }

    clearTrustedUdpAddress();
  }

  public void handleData(String data, ClientHandler client) {
    if (data == null || data.isEmpty()) {
      return;
    }

    if (mouseInputHandler.handleCommand(data)) {
      return;
    }

    Protocol.Decoded decoded = Protocol.decode(data);

    if ("PAIR_REQUEST".equals(decoded.type)) {
      if (pairingListener != null) {
        pairingListener.onPairRequest(
            decoded.fields.get("deviceId"),
            decoded.fields.get("deviceName"),
            decoded.fields.get("publicKey"),
            client);
      }
    } else if ("AUTH".equals(decoded.type)) {
      if (pairingListener != null) {
        pairingListener.onAuth(
            decoded.fields.get("deviceId"),
            decoded.fields.get("token"),
            client);
      }
    } else {
      System.out.println("[Network] Received data: " + data);

      if (dataListener != null) {
        dataListener.onData(data, client);
      }
    }
  }

  public void handleUdpData(String data, InetAddress sourceAddress) {
    if (data == null || data.isEmpty()) {
      return;
    }

    if (trustedUdpAddress == null || !trustedUdpAddress.equals(sourceAddress)) {
      System.out.println("[Network] Dropping UDP packet from untrusted source: " + sourceAddress);
      return;
    }

    mouseInputHandler.handleCommand(data);
  }

  public void handleDisconnect(ClientHandler client) {
    mouseInputHandler.releaseIfHeld();

    if (trustedUdpAddress != null && trustedUdpAddress.equals(client.getAddress())) {
      clearTrustedUdpAddress();
    }

    if (pairingListener != null) {
      pairingListener.onDisconnect(client);
    }
  }
}
