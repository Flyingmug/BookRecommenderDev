package bookrecommenderdev.server.dto;

import bookrecommenderdev.model.Libro;

import java.io.Serial;
import java.io.Serializable;

public record LibroConsigliato(Libro libro, int countConsigliato)
  implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
