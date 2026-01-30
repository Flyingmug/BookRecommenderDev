package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.data.PageResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Risultato paginato per la lista delle librerie di un utente.
 * <p>
 * {@code results} contiene i risultati della pagina corrente; {@code totalCount} è il numero totale
 * di librerie disponibili (su tutte le pagine).
 *
 * @param results    lista risultati della pagina (non nulla, può essere vuota)
 * @param totalCount numero totale risultati disponibili (>= 0)
 */
public record PaginaLibrerieRisultati(List<PaginaLibreria> results, int totalCount)
  implements PageResult<PaginaLibreria>, Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
