package bookrecommenderdev.routing.auth;

import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * todo
 * */
public final class AuthStorage {

  private static final Preferences prefs =
      Preferences.userNodeForPackage(AuthStorage.class);

  private static final String TOKEN = "auth.token";

  private AuthStorage() {}

  /** Salva le credenziali di accesso. */
  public static void save(String token) {
    prefs.put(TOKEN, token);
  }

  /** Reperisce le credenziali di accesso memorizzate in un {@link Optional}. */
  public static Optional<String> load() {
    String token = prefs.get(TOKEN, null);

    if (token == null) return Optional.empty();
    return Optional.of(token);
  }

  /** Rimuove le credenziali localmente memorizzate. */
  public static void clear() {
    prefs.remove(TOKEN);
  }
}