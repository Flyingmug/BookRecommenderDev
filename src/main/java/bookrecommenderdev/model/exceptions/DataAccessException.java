package bookrecommenderdev.model.exceptions;

import java.io.Serial;
import java.io.Serializable;
import java.sql.SQLException;

public class DataAccessException extends RuntimeException implements Serializable {
  @Serial private final static long serialVersionUID = 1L;

  public DataAccessException(String message, SQLException e) {
    super(message);
  }

  public SQLException getSQLException() {
    return new SQLException();
  }
}
