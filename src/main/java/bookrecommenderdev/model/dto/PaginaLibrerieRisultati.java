package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.data.PageResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PaginaLibrerieRisultati(List<PaginaLibreria> results, int totalCount)
  implements PageResult<PaginaLibreria>, Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
