package vertexlink.pairing;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import vertexlink.network.security.CryptoUtils;
import vertexlink.store.PairedDeviceStore;

public class PairingService {
  private final PairedDeviceStore pairedDevices;

  private final Map<String, KeyPair> pendingChallenges = new ConcurrentHashMap<>();

  public PairingService(PairedDeviceStore pairedDevices) {
    this.pairedDevices = pairedDevices;
  }

  public static class Challenge {
    public final String desktopPublicKey;
    public final String pin;

    Challenge(String desktopPublicKey, String pin) {
      this.desktopPublicKey = desktopPublicKey;
      this.pin = pin;
    }
  }

  public Challenge createChallenge(String addressKey, String clientPublicKeyStr) {
    KeyPair desktopKeyPair = CryptoUtils.generateKeyPair();
    pendingChallenges.put(addressKey, desktopKeyPair);

    PublicKey clientPublicKey = CryptoUtils.decodePublicKey(clientPublicKeyStr);
    String desktopPublicKeyStr = CryptoUtils.encodePublicKey(desktopKeyPair.getPublic());
    String pin = CryptoUtils.calculatePin(desktopKeyPair.getPrivate(), clientPublicKey);

    return new Challenge(desktopPublicKeyStr, pin);
  }

  public void discardChallenge(String addressKey) {
    pendingChallenges.remove(addressKey);
  }

  public String completePairing(String deviceId, String deviceName) {
    String token = UUID.randomUUID().toString();

    pairedDevices.save(deviceId, deviceName, token);

    return token;
  }

  public boolean verifyAuth(String deviceId, String token) {
    Optional<PairedDeviceStore.PairedDevice> stored = pairedDevices.find(deviceId);

    return stored.isPresent() && stored.get().token().equals(token);
  }

  public Optional<PairedDeviceStore.PairedDevice> findPaired(String deviceId) {
    return pairedDevices.find(deviceId);
  }

  public boolean isPaired(String deviceId) {
    return pairedDevices.find(deviceId).isPresent();
  }

  public void forget(String deviceId) {
    pairedDevices.remove(deviceId);
  }
}
