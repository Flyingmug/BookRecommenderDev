package bookrecommenderdev.client.auth;

import bookrecommenderdev.model.dto.UtenteSessione;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Contesto di autenticazione globale del client.
 * <p>
 * Mantiene lo stato dell’utente autenticato corrente e lo espone come
 * {@link javafx.beans.property.Property} per consentire binding reattivi
 * all’interno dell’interfaccia JavaFX.
 */
public class AuthContext {

  /**
   * Proprietà osservabile che rappresenta l’utente attualmente autenticato.
   * {@code null} indica che nessun utente è autenticato all'istanza dell'applicazione.
   */
  private static final ObjectProperty<UtenteSessione> currentUser =
      new SimpleObjectProperty<>(null);

  /** Costruttore privato: classe di utilità statica. */
  private AuthContext() {}

  /**
   * Restituisce la proprietà read-only dell’utente autenticato.
   * <p>
   * Utile per effettuare binding UI (es. mostra/nascondi componenti in base allo stato di login).
   *
   * @return proprietà osservabile dell’utente autenticato
   */
  public static ReadOnlyObjectProperty<UtenteSessione> userProperty() {
    return currentUser;
  }

  /**
   * Restituisce l’utente attualmente autenticato.
   *
   * @return sessione utente corrente, oppure {@code null} se non autenticato
   */
  public static UtenteSessione getUser() {
    return currentUser.get();
  }

  /**
   * Indica se un utente è attualmente autenticato.
   *
   * @return {@code true} se esiste una sessione attiva, {@code false} altrimenti
   */
  public static boolean isAuthenticated() {
    return currentUser.get() != null;
  }

  /**
   * Imposta l’utente autenticato corrente.
   * <p>
   * Invocato dopo un login riuscito o il ripristino automatico della sessione.
   *
   * @param user sessione utente autenticata
   */
  public static void login(UtenteSessione user) {
    currentUser.set(user);
  }

  /** Rimuove le credenziali localmente memorizzate. */
  public static void logout() {
    currentUser.set(null);
  }
}
