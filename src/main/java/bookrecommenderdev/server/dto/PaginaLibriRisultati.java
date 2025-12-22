package bookrecommenderdev.server.dto;

import bookrecommenderdev.model.Libro;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PaginaLibriRisultati(List<Libro> results, int totalCount)
  implements Serializable {
  @Serial
  private final static long serialVersionUID = 1;
}
