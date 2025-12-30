package bookrecommenderdev.routing.auth;

import bookrecommenderdev.model.Credentials;

import java.util.Optional;
import java.util.prefs.Preferences;


/**
 * todo
 * */
public final class AuthStorage {

  private static final Preferences prefs =
      Preferences.userNodeForPackage(AuthStorage.class);

  private static final String KEY_USERID = "auth.email";
  private static final String KEY_PASSWORD = "auth.password";

  private AuthStorage() {}

  /** Salva le credenziali di accesso. */
  public static void save(String userId, String password) {
    prefs.put(KEY_USERID, userId);
    prefs.put(KEY_PASSWORD, password);
  }

  /** Reperisce le credenziali di accesso memorizzate in un {@link Optional}. */
  public static Optional<Credentials> load() {
    String userId = prefs.get(KEY_USERID, null);
    String password = prefs.get(KEY_PASSWORD, null);

    if (userId == null || password == null) return Optional.empty();
    return Optional.of(new Credentials(userId, password));
  }

  /** Rimuove le credenziali localmente memorizzate. */
  public static void clear() {
    prefs.remove(KEY_USERID);
    prefs.remove(KEY_PASSWORD);
  }
}