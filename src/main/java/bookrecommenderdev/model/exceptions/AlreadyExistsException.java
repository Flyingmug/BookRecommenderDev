package bookrecommenderdev.model.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Indica che una risorsa esiste già e non può essere creata nuovamente.
 * <p>
 * Tipicamente usata per violazioni di unicità logica o di database
 * (es. duplicazione di record, consigli già presenti, userId già esistente).
 */
public class AlreadyExistsException extends RuntimeException implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  public AlreadyExistsException(String message) {
    super(message);
  }
}
