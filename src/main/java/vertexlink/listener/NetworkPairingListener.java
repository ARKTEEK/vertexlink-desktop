package vertexlink.listener;

import vertexlink.network.server.ClientHandler;

public interface NetworkPairingListener {
  void onPairRequest(String deviceId, String deviceName, String publicKey, ClientHandler client);

  void onAuth(String deviceId, String token, ClientHandler client);

  void onDisconnect(ClientHandler client);
}
