package vertexlink.network.protocol;

import vertexlink.handler.KeyboardInputHandler;
import vertexlink.handler.MouseInputHandler;
import vertexlink.listener.NetworkDataListener;
import vertexlink.listener.NetworkPairingListener;
import vertexlink.network.server.ClientHandler;

public class InboundMessageRouter {
  private final MouseInputHandler mouseInputHandler;
  private final KeyboardInputHandler keyboardInputHandler;

  private NetworkPairingListener pairingListener;
  private NetworkDataListener dataListener;

  public InboundMessageRouter(MouseInputHandler mouseInputHandler, KeyboardInputHandler keyboardInputHandler) {
    this.mouseInputHandler = mouseInputHandler;
    this.keyboardInputHandler = keyboardInputHandler;
  }

  public void setPairingListener(NetworkPairingListener listener) {
    this.pairingListener = listener;
  }

  public void setDataListener(NetworkDataListener listener) {
    this.dataListener = listener;
  }

  public void route(String data, ClientHandler client) {
    if (data == null || data.isEmpty()) {
      return;
    }

    if (mouseInputHandler.handleCommand(data)) {
      return;
    }

    if (keyboardInputHandler.handleCommand(data)) {
      return;
    }

    Protocol.Decoded decoded = Protocol.decode(data);

    if ("PAIR_REQUEST".equals(decoded.type)) {
      routePairRequest(decoded, client);
    } else if ("AUTH".equals(decoded.type)) {
      routeAuth(decoded, client);
    } else {
      routeApplicationData(data, client);
    }
  }

  private void routePairRequest(Protocol.Decoded decoded, ClientHandler client) {
    if (pairingListener != null) {
      pairingListener.onPairRequest(
          decoded.fields.get("deviceId"),
          decoded.fields.get("deviceName"),
          decoded.fields.get("publicKey"),
          client);
    }
  }

  private void routeAuth(Protocol.Decoded decoded, ClientHandler client) {
    if (pairingListener != null) {
      pairingListener.onAuth(
          decoded.fields.get("deviceId"),
          decoded.fields.get("token"),
          client);
    }
  }

  private void routeApplicationData(String data, ClientHandler client) {
    if (dataListener != null) {
      dataListener.onData(data, client);
    }
  }
}
