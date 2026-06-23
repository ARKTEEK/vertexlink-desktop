package vertexlink.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer extends Thread {
  private final int port;
  private DatagramSocket socket;
  private boolean isRunning;
  private NetworkManager manager;

  public UDPServer(NetworkManager manager, int port) {
    this.manager = manager;
    this.port = port;
    this.isRunning = true;
  }

  @Override
  public void run() {
    this.isRunning = true;
    try {
      this.socket = new DatagramSocket(port);

      byte[] buffer = new byte[2048];
      while (isRunning) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String data = new String(packet.getData(), 0, packet.getLength());
        manager.handleData(data);
      }
    } catch (IOException exception) {
      System.err.println("[UDP] Server exception: " + exception.getMessage());
    }
  }

  public void shutdown() {
    this.isRunning = false;

    if (this.socket != null) {
      this.socket.close();
    }

    System.out.println("[UDP] Shut down...");
  }

}
