package bookrecommenderdev.model;

import bookrecommenderdev.model.base.Libreria;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.base.Utente;
import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.model.exceptions.*;
import bookrecommenderdev.model.data.PageResult;
import bookrecommenderdev.model.dto.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interfaccia remota RMI che definisce i servizi esposti dal server.
 *
 * <p>Rappresenta il contratto pubblico tra applicazione client e server.
 * Tutti i metodi dichiarati sono invocabili da remoto tramite Java RMI e devono quindi
 * rispettare i vincoli tipici della comunicazione distribuita:</p>
 *
 * <ul>
 *   <li><b>Serializzazione:</b> parametri e valori di ritorno devono essere serializzabili.</li>
 *   <li><b>Errori di rete:</b> ogni metodo può sollevare {@link RemoteException} in caso
 *       di problemi di comunicazione.</li>
 *   <li><b>Concorrenza:</b> le chiamate possono essere gestite in parallelo,
 *       quindi l'implementazione server-side deve essere thread-safe.</li>
 * </ul>
 *
 * <h2>Paginazione</h2>
 * <p>Diverse operazioni restituiscono risultati paginati: l'indice pagina ({@code indicePagina})
 * è utilizzato per richiedere porzioni dei risultati. La dimensione pagina e le informazioni
 * sul totale sono incapsulate nei DTO e/o nei wrapper {@link PageResult}.</p>
 *
 * <h2>Gestione errori applicativi</h2>
 * <p>Le eccezioni del package {@code bookrecommenderdev.model.exceptions} rappresentano errori
 * di dominio (es. risorsa non trovata, credenziali invalide, vincoli violati) o errori di accesso
 * ai dati (es. {@link DataAccessException}).</p>
 */
public interface ServerInterface extends Remote {

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Ricerca
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Esegue una ricerca di libri secondo i criteri specificati.
   *
   * @param richiesta definizione di chiave e criterio di ricerca
   * @param indicePagina indice della pagina di risultati richiesta
   * @return risultati paginati contenenti {@link Libro libri} che soddisfano i criteri di ricerca
   * @throws RemoteException in caso di errore di comunicazione RMI
   * @throws DataAccessException in caso di errore durante l'accesso ai dati
   */
  PageResult<Libro> cercaLibro(SearchRequest richiesta, int indicePagina) throws RemoteException, DataAccessException;

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Libri
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Restituisce le informazioni di un libro a partire dal suo identificativo.
   *
   * @param idLibro identificativo univoco del libro
   * @return libro corrispondente all'id richiesto
   * @throws RemoteException in caso di errore di comunicazione RMI
   * @throws NotFoundException se il libro richiesto non esiste
   * @throws DataAccessException in caso di errore durante l'accesso ai dati
   */
  Libro getLibro(int idLibro) throws RemoteException, NotFoundException, DataAccessException;

  /**
   * Restituisce una "pagina" informativa completa relativa a un libro.
   *
   * <p>Il DTO {@link PaginaLibro} può includere informazioni arricchite rispetto a {@link #getLibro(int)},
   * ad esempio dettagli estesi, statistiche, valutazioni aggregate o elementi correlati
   * (in base al modello adottato).</p>
   *
   * @param idLibro identificativo univoco del libro
   * @return DTO contenente i dati della pagina libro
   * @throws RemoteException in caso di errore di comunicazione RMI
   * @throws DataAccessException in caso di errore durante l'accesso ai dati
   */
  PaginaLibro getLibroCompleto(int idLibro) throws RemoteException, DataAccessException;

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Librerie
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  PaginaLibrerieRisultati getListLibrerie(int idUtente, int indicePagina) throws RemoteException, DataAccessException;
  PaginaLibriRisultati searchAllLibrerie(int idUtente, SearchRequest richiesta,  int indicePagina) throws RemoteException, DataAccessException;
  PaginaLibriRisultati searchInLibreria(int idUtente, int idLibreria, int indicePagina) throws RemoteException, DataAccessException;
  Libreria getLibreriaById(int idUtente, int idLibreria) throws RemoteException, NotFoundException, DataAccessException;
  void registraLibreria(int idUtente, String nomeLibreria, List<Integer> idList) throws RemoteException, AlreadyExistsException, DataAccessException;
  void deleteLibreria(int idUtente, int idLibreria) throws RemoteException, NotFoundException, DataAccessException;
  boolean isLibroInLibrerieUtente(int idUtente, int idLibro) throws RemoteException, NotFoundException, DataAccessException;

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Valutazioni
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  Valutazione getValutazione(int idLibro, int userId) throws RemoteException, DataAccessException;
  PaginaValutazioni cercaValutazioni(int idLibro, int indicePagina) throws RemoteException, DataAccessException;
  void inserisciValutazioneLibro(Valutazione valutazione) throws RemoteException, DataAccessException;
  void deleteValutazione(int idLibro, int id_utente) throws RemoteException, NotFoundException, DataAccessException;

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Consigli di lettura
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  List<Libro> getSuggerimentiUtente(int idUtente, int idLibro) throws RemoteException;
  PaginaConsigliRisultati cercaSuggerimentiLibro(int idLibro, int indicePagina) throws RemoteException, DataAccessException;
  void inserisciSuggerimentoLibro(int idUtente, int idLibroBase, int idLibroCons) throws RemoteException, NotFoundException, AlreadyExistsException, LimitExceededException, DataAccessException;
  void deleteSuggerimentoLibro(int idUtente, int idLibroBase, int idLibroCons) throws RemoteException, NotFoundException, DataAccessException;

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Autenticazione
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  UtenteSessione registrazione(Utente u) throws RemoteException, AlreadyExistsException, DataAccessException;
  TokenSessione loginWithToken(String userId, String password) throws RemoteException, InvalidCredentialsException, DataAccessException;
  UtenteSessione resumeSessione(String token) throws RemoteException, InvalidCredentialsException, DataAccessException;
  void logout(String token) throws RemoteException, DataAccessException;

}
