package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Libreria;

import java.io.Serial;
import java.io.Serializable;

public record PaginaLibreria(Libreria library, int bookCount) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
}
