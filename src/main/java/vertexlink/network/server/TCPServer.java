package vertexlink.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vertexlink.network.NetworkManager;

public class TCPServer extends Thread {
  private final int port;
  private ServerSocket serverSocket;
  private volatile boolean isRunning;
  private final NetworkManager manager;
  private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

  public TCPServer(NetworkManager manager, int port) {
    this.manager = manager;
    this.port = port;
  }

  @Override
  public void run() {
    isRunning = true;
    try {
      serverSocket = new ServerSocket(port);

      while (isRunning) {
        Socket socket = serverSocket.accept();
        ClientHandler handler = new ClientHandler(socket, manager, this);

        clients.add(handler);

        handler.start();
      }
    } catch (IOException e) {
      if (isRunning) {
        System.err.println("[TCP] Server exception: " + e.getMessage());
      }
    }
  }

  void removeClient(ClientHandler handler) {
    clients.remove(handler);
  }

  public void shutdown() {
    isRunning = false;

    synchronized (clients) {
      for (ClientHandler handler : clients) {
        handler.close();
      }

      clients.clear();
    }
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }

    } catch (IOException e) {
      System.err.println("[TCP] Error closing server socket: " + e.getMessage());
    }
    System.out.println("[TCP] Shut down...");
  }
}
