package vertexlink.network;

import java.util.LinkedHashMap;
import java.util.Map;

import vertexlink.network.protocol.Protocol;
import vertexlink.network.server.ClientHandler;
import vertexlink.network.server.TCPServer;

public class NetworkManager {
  private final TCPServer tcpServer;
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
    this.tcpServer = new TCPServer(this, tcpPort);
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

    tcpServer.start();

    System.out.println("[Network] Server started!");
  }

  public void stop() {
    System.out.println("[Network] Shutting down...");

    isRunning = false;

    tcpServer.shutdown();
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

  public void sendPairChallenge(ClientHandler client, String myId, String myName, String publicKey) {
    Map<String, String> fields = new LinkedHashMap<>();

    fields.put("deviceId", myId);
    fields.put("deviceName", myName);
    fields.put("publicKey", publicKey);

    client.send(Protocol.encode("PAIR_CHALLENGE", fields));
  }

  public void sendPairSuccess(ClientHandler client, String myId, String myName, String token) {
    Map<String, String> fields = new LinkedHashMap<>();

    fields.put("deviceId", myId);
    fields.put("deviceName", myName);
    fields.put("token", token);

    client.send(Protocol.encode("PAIR_SUCCESS", fields));
  }

  public void sendAuthResult(ClientHandler client, boolean ok, String reason) {
    Map<String, String> fields = new LinkedHashMap<>();

    if (!ok && reason != null) {
      fields.put("reason", reason);
    }

    client.send(Protocol.encode(ok ? "AUTH_OK" : "AUTH_FAIL", fields));
  }

  public void sendPairDecision(ClientHandler client, boolean accepted, String reason) {
    Map<String, String> fields = new LinkedHashMap<>();

    fields.put("accepted", String.valueOf(accepted));

    if (!accepted && reason != null) {
      fields.put("reason", reason);
    }

    client.send(Protocol.encode("PAIR_DECISION", fields));
  }
}
