package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Libro;

import java.io.Serial;
import java.io.Serializable;

public record LibroConsigliato(Libro libro, int countConsigliato)
  implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
