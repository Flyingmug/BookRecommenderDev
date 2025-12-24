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

  private static final String KEY_EMAIL = "auth.email";
  private static final String KEY_PASSWORD = "auth.password";

  private AuthStorage() {}

  /** Salva le credenziali di accesso. */
  public static void save(String email, String password) {
    prefs.put(KEY_EMAIL, email);
    prefs.put(KEY_PASSWORD, password); // see note below
  }

  /** Reperisce le credenziali di accesso memorizzate in un {@link Optional}. */
  public static Optional<Credentials> load() {
    String email = prefs.get(KEY_EMAIL, null);
    String password = prefs.get(KEY_PASSWORD, null);

    if (email == null || password == null) return Optional.empty();
    return Optional.of(new Credentials(email, password));
  }

  /** Rimuove le credenziali localmente memorizzate. */
  public static void clear() {
    prefs.remove(KEY_EMAIL);
    prefs.remove(KEY_PASSWORD);
  }
}