package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Valutazione;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Risultato paginato per le valutazioni/recensioni associate a un libro.
 * <p>
 * {@code results} contiene i risultati della pagina corrente; {@code totalCount} è il numero totale
 * di valutazioni disponibili (su tutte le pagine).
 *
 * @param results    lista valutazioni della pagina (non nulla, può essere vuota)
 * @param totalCount numero totale valutazioni disponibili (>= 0)
 */
public record PaginaValutazioni(List<Valutazione> results, int totalCount)
  implements Serializable {
  @Serial
  private final static long serialVersionUID = 1L;
}
