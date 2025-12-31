package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.data.PageResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PaginaLibriRisultati(List<Libro> results, int totalCount)
  implements Serializable, PageResult<Libro> {
  @Serial private final static long serialVersionUID = 1;
}
