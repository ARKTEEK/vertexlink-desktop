package vertexlink.network.security;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class UDPCrypto {
  private static final int IV_SIZE = 12;
  private static final int TAG_BIT_LENGTH = 128;

  private final SecretKeySpec keySpec;
  private final AtomicLong outboundSequence = new AtomicLong(1);
  private volatile long highestInboundSequence = 0;

  public UDPCrypto(byte[] keyBytes) {
    this.keySpec = new SecretKeySpec(keyBytes, "AES");
  }

  public byte[] encrypt(byte[] plainText) throws Exception {
    long seq = outboundSequence.getAndIncrement();
    byte[] iv = new byte[IV_SIZE];
    ByteBuffer.wrap(iv).putLong(4, seq);

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec spec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);

    byte[] cipherText = cipher.doFinal(plainText);
    ByteBuffer packet = ByteBuffer.allocate(IV_SIZE + cipherText.length);
    packet.put(iv);
    packet.put(cipherText);

    return packet.array();
  }

  public byte[] decrypt(byte[] packetBytes, int length) throws Exception {
    if (length < IV_SIZE) {
      throw new IllegalArgumentException("Packet too short");
    }

    ByteBuffer buffer = ByteBuffer.wrap(packetBytes, 0, length);
    byte[] iv = new byte[IV_SIZE];
    buffer.get(iv);

    long sequence = ByteBuffer.wrap(iv).getLong(4);
    if (sequence <= highestInboundSequence) {
      throw new SecurityException("Replay attack detected or packet out of order");
    }

    byte[] cipherText = new byte[length - IV_SIZE];
    buffer.get(cipherText);

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec spec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
    cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

    byte[] plainText = cipher.doFinal(cipherText);
    highestInboundSequence = sequence;

    return plainText;
  }
}
