package vertexlink.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public class PairedDeviceStore {
  private static final Path STORE_PATH = Path.of(
      System.getProperty("user.home"),
      ".vertexlink",
      "paired_devices.properties");

  public record PairedDevice(String deviceId, String deviceName, String token) {
  }

  private final Properties props = new Properties();

  public PairedDeviceStore() {
    load();
  }

  private synchronized void load() {
    if (Files.exists(STORE_PATH)) {
      try (InputStream in = Files.newInputStream(STORE_PATH)) {
        props.load(in);
      } catch (IOException e) {
        System.err.println("[Pairing] Could not load paired devices: " + e.getMessage());
      }
    }
  }

  private synchronized void persist() {
    try {
      Files.createDirectories(STORE_PATH.getParent());

      try (OutputStream out = Files.newOutputStream(STORE_PATH)) {
        props.store(out, "VertexLink paired devices");
      }
    } catch (IOException e) {
      System.err.println("[Pairing] Could not save paired devices: " + e.getMessage());
    }
  }

  public synchronized void save(String deviceId, String deviceName, String token) {
    props.setProperty(deviceId + ".name", deviceName);
    props.setProperty(deviceId + ".token", token);

    persist();
  }

  public synchronized Optional<PairedDevice> find(String deviceId) {
    String name = props.getProperty(deviceId + ".name");
    String token = props.getProperty(deviceId + ".token");

    return (name == null || token == null)
        ? Optional.empty()
        : Optional.of(new PairedDevice(deviceId, name, token));
  }

  public synchronized void remove(String deviceId) {
    props.remove(deviceId + ".name");
    props.remove(deviceId + ".token");

    persist();
  }
}
