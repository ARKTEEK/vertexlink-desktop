package vertexlink;

import vertexlink.networking.NetworkManager;

public class App {
  public static void main(String[] args) {
    System.out.println("Program running...");

    NetworkManager networkManager = new NetworkManager(28401);
    networkManager.start();
  }
}
