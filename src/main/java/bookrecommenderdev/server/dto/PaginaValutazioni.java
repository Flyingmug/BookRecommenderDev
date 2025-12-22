package bookrecommenderdev.server.dto;

import bookrecommenderdev.model.Valutazione;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PaginaValutazioni(List<Valutazione> results, int totalCount)
  implements Serializable {
  @Serial
  private final static long serialVersionUID = 1L;
}
