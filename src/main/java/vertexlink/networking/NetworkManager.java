package vertexlink.networking;

public class NetworkManager {
  private final UDPServer udpServer;

  private boolean isRunning;

  public NetworkManager(int udpPort) {
    this.udpServer = new UDPServer(this, udpPort);
  }

  public void start() {
    if (isRunning) {
      System.out.println("[Network] Already running!");
      return;
    }

    this.isRunning = true;
    this.udpServer.start();

    System.out.println("[Network] Server started!");
  }

  public void stop() {
    System.out.println("[Network] Shutting down...");

    this.isRunning = false;
    this.udpServer.shutdown();
  }

  public void handleData(String data) {
    if (data == null || data.isEmpty()) {
      return;
    }

    System.out.println("[Network] Received data: " + data);
  }
}
