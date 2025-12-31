package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Valutazione;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PaginaValutazioni(List<Valutazione> results, int totalCount)
  implements Serializable {
  @Serial
  private final static long serialVersionUID = 1L;
}
