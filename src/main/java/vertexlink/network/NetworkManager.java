package vertexlink.network;

import vertexlink.network.protocol.Protocol;
import vertexlink.network.server.ClientHandler;
import vertexlink.network.server.TCPServer;

public class NetworkManager {
  private final int tcpPort;
  private TCPServer tcpServer;
  private boolean isRunning;
  private PairingListener pairingListener;
  private DataListener dataListener;

  public interface PairingListener {
    void onPairRequest(String deviceId, String deviceName, String publicKey, ClientHandler client);

    void onAuth(String deviceId, String token, ClientHandler client);

    void onDisconnect(ClientHandler client);
  }

  public interface DataListener {
    void onData(String data, ClientHandler client);
  }

  public NetworkManager(int tcpPort) {
    this.tcpPort = tcpPort;
  }

  public void setPairingListener(PairingListener listener) {
    this.pairingListener = listener;
  }

  public void setDataListener(DataListener listener) {
    this.dataListener = listener;
  }

  public void start() {
    if (isRunning) {
      System.out.println("[Network] Already running!");

      return;
    }

    isRunning = true;

    tcpServer = new TCPServer(this, tcpPort);
    tcpServer.start();

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
  }

  public void handleData(String data, ClientHandler client) {
    if (data == null || data.isEmpty()) {
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

  public void handleDisconnect(ClientHandler client) {
    if (pairingListener != null) {
      pairingListener.onDisconnect(client);
    }
  }
}
