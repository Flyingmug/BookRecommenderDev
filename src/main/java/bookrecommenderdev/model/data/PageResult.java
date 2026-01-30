package bookrecommenderdev.model.data;

import java.util.List;

/**
 * Risultato di una richiesta paginata.
 * <p>
 * Incapsula:
 * <ul>
 *   <li>gli elementi della pagina corrente;</li>
 *   <li>il numero totale di elementi disponibili (indipendente dalla pagina).</li>
 * </ul>
 * <p>
 * L’interfaccia è volutamente minimale per consentire implementazioni
 * semplici (record, DTO).
 *
 * @param <T> tipo degli elementi contenuti nei risultati
 */
public interface PageResult<T> {

  /**
   * Restituisce gli elementi della pagina corrente.
   * <p>
   * Se non esistono risultati per la pagina richiesta,
   * la lista dovrebbe essere vuota (non {@code null}).
   *
   * @return lista degli elementi della pagina corrente
   */
  List<T> results();

  /**
   * Restituisce il numero totale di elementi disponibili.
   * <p>
   * Questo valore rappresenta il totale globale dei risultati
   * (non la dimensione della pagina) ed è usato per calcolare
   * il numero complessivo di pagine.
   *
   * @return numero totale di elementi disponibili
   */
  int totalCount();
}