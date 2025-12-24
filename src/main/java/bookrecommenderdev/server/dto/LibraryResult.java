package bookrecommenderdev.server.dto;

import bookrecommenderdev.model.Libreria;

import java.io.Serial;
import java.io.Serializable;

public record LibraryResult(Libreria library, int bookCount) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
}
