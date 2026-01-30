package bookrecommenderdev.model.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Indica che una risorsa richiesta non è stata trovata.
 * <p>
 * Usata quando l’assenza è considerata un errore logico
 * (non semplicemente una ricerca vuota).
 */
public class NotFoundException extends RuntimeException implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public NotFoundException(String message) {
    super(message);
  }
}
