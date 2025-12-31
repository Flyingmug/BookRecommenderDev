package bookrecommenderdev.server.auth;

import java.security.SecureRandom;

public class Tokenizer {
  private static final SecureRandom RNG = new SecureRandom();

  public static String newToken64Hex() {
    byte[] bytes = new byte[32];
    RNG.nextBytes(bytes);
    StringBuilder sb = new StringBuilder(64);
    for (byte b : bytes) sb.append(String.format("%02x", b));
    return sb.toString();
    }
}
