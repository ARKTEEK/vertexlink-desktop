package vertexlink.network.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class TLSContextFactory {
  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  public static SSLContext createServerContext() throws Exception {
    Path keystorePath = Path.of(System.getProperty("user.home"), ".vertexlink", "tls.p12");

    char[] password = loadOrCreatePassword();

    KeyStore keyStore = loadOrCreateKeyStore(keystorePath, password);

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, password);

    SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
    sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

    return sslContext;
  }

  private static char[] loadOrCreatePassword() {
    Preferences prefs = Preferences.userNodeForPackage(TLSContextFactory.class);
    String existing = prefs.get("keystore_password", null);

    if (existing != null && !existing.isEmpty()) {
      return existing.toCharArray();
    }

    String created = UUID.randomUUID().toString();
    prefs.put("keystore_password", created);

    return created.toCharArray();
  }

  private static KeyStore loadOrCreateKeyStore(Path keystorePath, char[] password) throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");

    if (Files.exists(keystorePath)) {
      try (InputStream input = Files.newInputStream(keystorePath)) {
        keyStore.load(input, password);

        return keyStore;
      } catch (Exception e) {
        System.err.println("[TLS] Could not load keystore, regenerating: " + e.getMessage());
      }
    }

    Files.createDirectories(keystorePath.getParent());
    keyStore.load(null, password);

    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);

    KeyPair keyPair = generator.generateKeyPair();

    X509Certificate certificate = selfSignedCertificate(keyPair);
    keyStore.setKeyEntry("vertexlink", keyPair.getPrivate(), password, new X509Certificate[] { certificate });

    try (OutputStream output = Files.newOutputStream(keystorePath)) {
      keyStore.store(output, password);
    }
    return keyStore;
  }

  private static X509Certificate selfSignedCertificate(KeyPair keyPair) throws Exception {
    Instant now = Instant.now();

    X500Name name = new X500Name("CN=VertexLink Desktop");

    BigInteger serial = new BigInteger(64, new SecureRandom());

    JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
        name,
        serial,
        Date.from(now.minus(1, ChronoUnit.DAYS)),
        Date.from(now.plus(3650, ChronoUnit.DAYS)),
        name,
        keyPair.getPublic());

    ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());

    return new JcaX509CertificateConverter()
        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
        .getCertificate(builder.build(signer));
  }

}
