package vertexlink.network.security;

import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyAgreement;

public class CryptoUtils {

  public static KeyPair generateKeyPair() {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
      ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");

      keyPairGenerator.initialize(ecSpec);

      return keyPairGenerator.generateKeyPair();
    } catch (Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  public static PublicKey decodePublicKey(String base64Key) {
    try {
      byte[] keyBytes = Base64.getDecoder().decode(base64Key);

      KeyFactory keyFactory = KeyFactory.getInstance("EC");

      return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String encodePublicKey(PublicKey publicKey) {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }

  public static String calculatePin(PrivateKey localPrivateKey, PublicKey remotePublicKey) {
    try {
      KeyAgreement agreement = KeyAgreement.getInstance("ECDH");
      agreement.init(localPrivateKey);
      agreement.doPhase(remotePublicKey, true);

      byte[] sharedSecret = agreement.generateSecret();

      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      byte[] hash = digest.digest(sharedSecret);

      int positiveHash = ByteBuffer.wrap(hash).getInt() & 0x7FFFFFFF;
      int pinNumber = positiveHash % 1000000;

      return String.format("%06d", pinNumber);
    } catch (Exception e) {
      e.printStackTrace();
      return "000000";
    }
  }
}
