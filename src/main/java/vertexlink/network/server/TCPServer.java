package vertexlink.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import vertexlink.network.NetworkManager;
import vertexlink.network.security.TLSContextFactory;

public class TCPServer extends Thread {
  private static final int MAX_CONCURRENT_CLOSES = 8;
  private static final int SHUTDOWN_TIMEOUT_SECONDS = 2;

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
      SSLContext sslContext = TLSContextFactory.createServerContext();
      SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
      SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(port);

      sslServerSocket.setEnabledProtocols(new String[] { "TLSv1.2", "TLSv1.3" });
      sslServerSocket.setNeedClientAuth(false);

      this.serverSocket = sslServerSocket;

      System.out.println("[TCP] TLS server listening on " + port);

      while (isRunning) {
        Socket socket = serverSocket.accept();
        ClientHandler handler = new ClientHandler(socket, manager, this);

        clients.add(handler);

        handler.start();
      }
    } catch (Exception e) {
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

    List<ClientHandler> toClose;

    synchronized (clients) {
      toClose = new ArrayList<>(clients);
      clients.clear();
    }

    if (!toClose.isEmpty()) {
      int poolSize = Math.min(toClose.size(), MAX_CONCURRENT_CLOSES);
      ExecutorService closer = Executors.newFixedThreadPool(poolSize);

      for (ClientHandler handler : toClose) {
        closer.submit(handler::close);
      }

      closer.shutdown();

      try {
        if (!closer.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
          System.err.println("[TCP] Timed out waiting for client sockets to close cleanly");
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
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
