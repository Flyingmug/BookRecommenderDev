package bookrecommenderdev.model.exceptions;

import java.io.Serial;
import java.io.Serializable;
import java.sql.SQLException;

/**
 * Eccezione wrapper per errori di accesso ai dati.
 * <p>
 * Incapsula una {@link SQLException} convertendola in un’eccezione unchecked,
 * preservando la causa originale per logging e debug.
 * <p>
 * Usata per isolare il resto dell’applicazione dai dettagli JDBC.
 */
public class DataAccessException extends RuntimeException implements Serializable {
  @Serial private final static long serialVersionUID = 1L;

  public DataAccessException(String message) {
    super(message);
  }
}
