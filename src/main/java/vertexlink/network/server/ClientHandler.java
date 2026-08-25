package vertexlink.network.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import vertexlink.network.NetworkManager;

public class ClientHandler extends Thread {
  private final Socket socket;
  private final NetworkManager manager;
  private final TCPServer server;
  private BufferedReader reader;
  private BufferedWriter writer;
  private volatile boolean isRunning = true;

  public ClientHandler(Socket socket, NetworkManager manager, TCPServer server) {
    this.socket = socket;
    this.manager = manager;
    this.server = server;
  }

  @Override
  public void run() {
    try {
      if (socket instanceof SSLSocket sslSocket) {
        sslSocket.startHandshake();
      }

      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      String line;

      while (isRunning && (line = reader.readLine()) != null) {
        manager.handleData(line, this);
      }
    } catch (IOException e) {
      if (isRunning) {
        System.err.println("[TCP] Connection error: " + e.getMessage());
      }
    } finally {
      close();

      server.removeClient(this);
    }
  }

  public synchronized void send(String message) {
    try {
      if (writer != null) {
        writer.write(message);
        writer.write("\n");
        writer.flush();
      }
    } catch (IOException e) {
      System.err.println("[TCP] Failed to send: " + e.getMessage());
    }
  }

  public InetAddress getAddress() {
    return socket.getInetAddress();
  }

  public void close() {
    isRunning = false;

    try {
      if (reader != null) {
        reader.close();
      }
    } catch (IOException ignored) {
    }
    try {
      if (writer != null) {
        writer.close();
      }
    } catch (IOException ignored) {
    }
    try {
      if (!socket.isClosed()) {
        socket.close();
      }
    } catch (IOException ignored) {
    }
  }
}
