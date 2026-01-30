package bookrecommenderdev.model.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Indica che le credenziali fornite non sono valide.
 * <p>
 * Tipicamente usata nei flussi di autenticazione
 * (login, refresh token, validazione sessione).
 */
public class InvalidCredentialsException extends RuntimeException implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  public InvalidCredentialsException(String message) {
    super(message);
  }
}
