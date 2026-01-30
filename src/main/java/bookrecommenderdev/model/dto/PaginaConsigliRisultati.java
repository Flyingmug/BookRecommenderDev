package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.data.PageResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Risultato paginato per la ricerca dei libri consigliati.
 * <p>
 * {@code results} contiene i risultati della pagina corrente; {@code totalCount} è il numero totale
 * di risultati disponibili (su tutte le pagine).
 *
 * @param results    lista risultati della pagina (non nulla, può essere vuota)
 * @param totalCount numero totale risultati disponibili (>= 0)
 */
public record PaginaConsigliRisultati(List<LibroConsigliato> results, int totalCount)
    implements Serializable, PageResult<LibroConsigliato> {
  @Serial private static final long serialVersionUID = 1L;
}
