package vertexlink.device;

import java.util.UUID;
import java.util.prefs.Preferences;

public class DeviceIdentity {
  private final Preferences prefs = Preferences.userNodeForPackage(DeviceIdentity.class);

  public String getId() {
    String existing = prefs.get("device_id", null);

    if (existing != null) {
      return existing;
    }

    String newId = UUID.randomUUID().toString();
    prefs.put("device_id", newId);

    return newId;
  }
}
