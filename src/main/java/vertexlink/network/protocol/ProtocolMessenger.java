package vertexlink.network.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

import vertexlink.network.server.ClientHandler;

public class ProtocolMessenger {

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
