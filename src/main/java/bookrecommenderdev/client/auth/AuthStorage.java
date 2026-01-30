package bookrecommenderdev.client.auth;

import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * * Utility per la persistenza locale del token di autenticazione.
 * * <p>
 * * Utilizza {@link Preferences} per salvare e recuperare il token di sessione,
 * * consentendo il ripristino automatico del login tra esecuzioni dell’applicazione.
 *
 */
public final class AuthStorage {

  private static final Preferences prefs =
      Preferences.userNodeForPackage(AuthStorage.class);

  /** Chiave utilizzata per memorizzare il token di autenticazione. */
  private static final String TOKEN = "auth.token";

  /** Costruttore privato: classe di utilità statica. */
  private AuthStorage() {}

  /**
   * Salva le credenziali di accesso.
   * @param token token di sessione restituito dal server
   */
  public static void save(String token) {
    prefs.put(TOKEN, token);
  }

  /**
   Reperisce le credenziali di accesso memorizzate in un {@link Optional}.
   *
   * @return {@link Optional} contenente le credenziali, oppure vuoto se assenti
   */
  public static Optional<String> load() {
    String token = prefs.get(TOKEN, null);

    if (token == null) return Optional.empty();
    return Optional.of(token);
  }
  /**
   * Rimuove le credenziali di autenticazione memorizzate localmente.
   * <p>
   * Invocabile per l'operazione di logout o di rimozione di credenziali/token invalidi.
   */
  public static void clear() {
    prefs.remove(TOKEN);
  }
}