package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.data.PageResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Risultato paginato per la ricerca/visualizzazione di libri.
 * <p>
 * {@code results} contiene i risultati della pagina corrente; {@code totalCount} è il numero totale
 * di libri che soddisfano la ricerca (su tutte le pagine).
 *
 * @param results    lista risultati della pagina (non nulla, può essere vuota)
 * @param totalCount numero totale risultati disponibili (>= 0)
 */
public record PaginaLibriRisultati(List<Libro> results, int totalCount)
  implements Serializable, PageResult<Libro> {
  @Serial private final static long serialVersionUID = 1;
}
