package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.data.PageResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

  public record PaginaConsigliRisultati(List<LibroConsigliato> results, int totalCount)
      implements Serializable, PageResult<LibroConsigliato> {
    @Serial private static final long serialVersionUID = 1L;
  }
