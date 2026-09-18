package vertexlink.network;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import vertexlink.listener.NetworkDataListener;
import vertexlink.listener.NetworkPairingListener;
import vertexlink.network.protocol.InboundMessageRouter;
import vertexlink.network.server.ClientHandler;
import vertexlink.network.server.TCPServer;
import vertexlink.network.server.UDPServer;
import vertexlink.network.session.UDPSessionState;

public class NetworkManager {
  private final int tcpPort;
  private final int udpPort;
  private final InboundMessageRouter messageRouter;
  private final UDPSessionState udpSession;
  private final AtomicBoolean running = new AtomicBoolean(false);

  private TCPServer tcpServer;
  private UDPServer udpServer;
  private NetworkPairingListener pairingListener;

  public NetworkManager(
      int tcpPort,
      int udpPort,
      InboundMessageRouter messageRouter,
      UDPSessionState udpSession) {
    this.tcpPort = tcpPort;
    this.udpPort = udpPort;
    this.messageRouter = messageRouter;
    this.udpSession = udpSession;
  }

  public void setPairingListener(NetworkPairingListener listener) {
    this.pairingListener = listener;
    this.messageRouter.setPairingListener(listener);
  }

  public void setDataListener(NetworkDataListener listener) {
    this.messageRouter.setDataListener(listener);
  }

  public void setUdpSessionKey(byte[] keyBytes) {
    this.udpSession.setSessionKey(keyBytes);
  }

  public void setTrustedUdpAddress(InetAddress address) {
    this.udpSession.setTrustedAddress(address);
  }

  public void clearTrustedUdpAddress() {
    this.udpSession.clear();
  }

  public void start() {
    if (!this.running.compareAndSet(false, true)) {
      System.out.println("[Network] Already running!");
      return;
    }

    this.tcpServer = new TCPServer(this, this.tcpPort);
    this.tcpServer.start();

    this.udpServer = new UDPServer(this, this.udpPort);
    this.udpServer.start();

    System.out.println("[Network] Server started!");
  }

  public void stop() {
    if (!this.running.compareAndSet(true, false)) {
      return;
    }

    System.out.println("[Network] Shutting down...");

    if (this.tcpServer != null) {
      this.tcpServer.shutdown();
      this.tcpServer = null;
    }

    if (this.udpServer != null) {
      this.udpServer.shutdown();
      this.udpServer = null;
    }

    clearTrustedUdpAddress();
  }

  public void handleData(String data, ClientHandler client) {
    this.messageRouter.route(data, client);
  }

  public void handleUdpPacket(byte[] data, int length, InetAddress sourceAddress) {
    String command = this.udpSession.decrypt(data, length, sourceAddress);

    if (command != null) {
      this.messageRouter.route(command, null);
    }
  }

  public void handleDisconnect(ClientHandler client) {
    if (this.udpSession.isTrustedSource(client.getAddress())) {
      clearTrustedUdpAddress();
    }

    if (this.pairingListener != null) {
      this.pairingListener.onDisconnect(client);
    }
  }
}
