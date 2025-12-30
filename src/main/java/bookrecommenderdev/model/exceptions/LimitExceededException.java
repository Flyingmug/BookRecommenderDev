package bookrecommenderdev.model.exceptions;

public class LimitExceededException extends RuntimeException {
  public LimitExceededException(String message) {
    super(message);
  }
}
