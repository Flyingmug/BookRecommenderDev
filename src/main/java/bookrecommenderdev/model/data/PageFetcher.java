package bookrecommenderdev.model.data;

import bookrecommenderdev.model.exceptions.DataAccessException;

import java.rmi.RemoteException;

/**
 * Funzione astratta per il recupero paginato di risultati.
 * <p>
 * Rappresenta una "sorgente di pagine di dati" che, dato un indice di pagina,
 * restituisce un {@link PageResult} contenente:
 * <ul>
 *   <li>la lista degli elementi della pagina;</li>
 *   <li>il numero totale di elementi disponibili.</li>
 * </ul>
 * <p>
 * L’interfaccia è pensata per essere usata in contesti client (es. UI)
 * che gestiscono paginazione in modo lazy, indipendentemente dalla sorgente
 * dei dati (database locale, servizio remoto, RMI).
 *
 * @param <T> tipo degli elementi restituiti nella pagina
 */
@FunctionalInterface
public interface PageFetcher<T> {

  /**
   * Recupera una pagina di risultati.
   * <p>
   * Semantica:
   * <ul>
   *   <li>{@code pageIndex} è 0-based;</li>
   *   <li>l’implementazione decide come gestire indici negativi o fuori range
   *       (tipicamente trattati come pagina vuota);</li>
   *   <li>il {@link PageResult#totalCount()} deve rappresentare il totale globale
   *       degli elementi, non solo quelli della pagina.</li>
   * </ul>
   * <p>
   * Il metodo propaga eccezioni di accesso remoto e di accesso ai dati,
   * lasciando la gestione dell’errore al chiamante.
   *
   * @param pageIndex indice della pagina (0-based)
   * @return risultato paginato (mai {@code null})
   * @throws RemoteException in caso di errore di comunicazione remota
   * @throws DataAccessException in caso di errore applicativo di accesso ai dati
   */
  PageResult<T> fetch(int pageIndex) throws RemoteException, DataAccessException;
}
