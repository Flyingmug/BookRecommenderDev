package bookrecommenderdev.model.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Indica che è stato superato un limite applicativo.
 * <p>
 * Esempi:
 * <ul>
 *   <li>numero massimo di consigli per libro</li>
 *   <li>numero massimo di operazioni consentite</li>
 * </ul>
 */
public class LimitExceededException extends RuntimeException implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  public LimitExceededException(String message) {
    super(message);
  }
}
