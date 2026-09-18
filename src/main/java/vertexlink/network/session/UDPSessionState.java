package vertexlink.network.session;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import vertexlink.network.security.UDPCrypto;

public class UDPSessionState {
  private volatile InetAddress trustedAddress;
  private volatile UDPCrypto crypto;

  public void setSessionKey(byte[] keyBytes) {
    this.crypto = keyBytes != null ? new UDPCrypto(keyBytes) : null;
  }

  public void setTrustedAddress(InetAddress address) {
    this.trustedAddress = address;
  }

  public void clear() {
    this.trustedAddress = null;
    this.crypto = null;
  }

  public boolean isTrustedSource(InetAddress address) {
    return trustedAddress != null && trustedAddress.equals(address);
  }

  public String decrypt(byte[] data, int length, InetAddress sourceAddress) {
    if (!isTrustedSource(sourceAddress)) {
      System.out.println("[Network] Dropping UDP packet from untrusted source: " + sourceAddress);

      return null;
    }

    UDPCrypto activeCrypto = crypto;

    if (activeCrypto == null) {
      System.out.println("[Network] Dropping UDP packet, no session key configured");

      return null;
    }

    try {
      byte[] decryptedBytes = activeCrypto.decrypt(data, length);

      return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      System.err.println("[Network] Failed to decrypt UDP packet: " + e.getMessage());

      return null;
    }
  }
}
