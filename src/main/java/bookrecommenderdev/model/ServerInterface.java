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

  // =====================================================
  // Ricerca
  // =====================================================

  /**
   * Esegue una ricerca paginata di libri secondo i criteri specificati.
   *
   * <p>
   * Se la richiesta è {@code null} o non valida, il risultato è una pagina vuota.
   * </p>
   *
   * @param richiesta    definizione dei criteri di ricerca
   * @param indicePagina indice della pagina richiesta (0-based)
   * @return pagina di risultati contenente libri corrispondenti
   * @throws RemoteException     in caso di errore di comunicazione RMI
   * @throws DataAccessException in caso di errore di accesso ai dati
   */
  PageResult<Libro> cercaLibro(SearchRequest richiesta, int indicePagina)
      throws RemoteException, DataAccessException;

  // =====================================================
  // Libri
  // =====================================================

  /**
   * Restituisce le informazioni di base di un libro.
   *
   * @param idLibro identificativo univoco del libro
   * @return libro corrispondente all’id richiesto
   * @throws NotFoundException   se il libro non esiste
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  Libro getLibro(int idLibro)
      throws RemoteException, NotFoundException, DataAccessException;

  /**
   * Restituisce la pagina completa di dettaglio di un libro.
   *
   * <p>
   * Include le informazioni del libro e le valutazioni aggregate.
   * Le medie delle valutazioni possono essere {@code null} se il libro
   * non ha ancora ricevuto valutazioni.
   * </p>
   *
   * @param idLibro identificativo del libro
   * @return pagina di dettaglio del libro
   * @throws NotFoundException   se il libro non esiste
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  PaginaLibro getLibroCompleto(int idLibro)
      throws RemoteException, NotFoundException, DataAccessException;

  // =====================================================
  // Librerie
  // =====================================================

  /**
   * Restituisce la lista paginata delle librerie di un utente.
   *
   * @param idUtente     id dell’utente
   * @param indicePagina indice pagina (0-based)
   * @return pagina di librerie dell’utente
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  PaginaLibrerieRisultati getListLibrerie(int idUtente, int indicePagina)
      throws RemoteException, DataAccessException;

  /**
   * Cerca libri in tutte le librerie dell’utente.
   *
   * @param idUtente     id utente
   * @param richiesta    criteri di ricerca
   * @param indicePagina indice pagina (0-based)
   * @return pagina di libri trovati
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  PaginaLibriRisultati searchAllLibrerie(int idUtente, SearchRequest richiesta, int indicePagina)
      throws RemoteException, DataAccessException;

  /**
   * Restituisce i libri contenuti in una specifica libreria dell’utente.
   *
   * @param idUtente     id utente
   * @param idLibreria   id libreria
   * @param indicePagina indice pagina (0-based)
   * @return pagina di libri contenuti nella libreria
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  PaginaLibriRisultati searchInLibreria(int idUtente, int idLibreria, int indicePagina)
      throws RemoteException, DataAccessException;

  /**
   * Recupera una libreria verificando che appartenga all’utente.
   *
   * @param idUtente   id utente
   * @param idLibreria id libreria
   * @return libreria richiesta
   * @throws NotFoundException   se la libreria non esiste o non appartiene all’utente
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  Libreria getLibreriaById(int idUtente, int idLibreria)
      throws RemoteException, NotFoundException, DataAccessException;

  /**
   * Crea una nuova libreria per l’utente.
   *
   * @param idUtente     id utente
   * @param nomeLibreria nome della libreria
   * @param idList       lista di id libri iniziali (può essere vuota)
   * @throws AlreadyExistsException se esiste già una libreria con lo stesso nome
   * @throws DataAccessException    in caso di errore DB
   * @throws RemoteException        in caso di errore RMI
   */
  void registraLibreria(int idUtente, String nomeLibreria, List<Integer> idList)
      throws RemoteException, AlreadyExistsException, DataAccessException;

  /**
   * Elimina una libreria dell’utente.
   *
   * @param idUtente   id utente
   * @param idLibreria id libreria
   * @throws NotFoundException   se la libreria non esiste
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  void deleteLibreria(int idUtente, int idLibreria)
      throws RemoteException, NotFoundException, DataAccessException;

  /**
   * Verifica se un libro è presente in almeno una libreria dell’utente.
   *
   * @param idUtente id utente
   * @param idLibro  id libro
   * @return {@code true} se il libro è presente; {@code false} altrimenti
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  boolean isLibroInLibrerieUtente(int idUtente, int idLibro)
      throws RemoteException, DataAccessException;

  // =====================================================
  // Valutazioni
  // =====================================================

  /**
   * Restituisce la valutazione dell’utente per un libro.
   *
   * @param idLibro  id libro
   * @param idUtente id utente
   * @return valutazione oppure {@code null} se non esiste
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  Valutazione getValutazione(int idLibro, int idUtente)
      throws RemoteException, DataAccessException;

  /**
   * Restituisce una pagina di valutazioni associate a un libro.
   *
   * @param idLibro      id libro
   * @param indicePagina indice pagina (0-based)
   * @return pagina di valutazioni
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  PaginaValutazioni cercaValutazioni(int idLibro, int indicePagina)
      throws RemoteException, DataAccessException;

  /**
   * Inserisce o aggiorna la valutazione di un libro.
   *
   * @param valutazione valutazione da salvare
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  void inserisciValutazioneLibro(Valutazione valutazione)
      throws RemoteException, DataAccessException;

  /**
   * Elimina la valutazione dell’utente per un libro.
   *
   * @param idLibro  id libro
   * @param idUtente id utente
   * @throws NotFoundException   se la valutazione non esiste
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  void deleteValutazione(int idLibro, int idUtente)
      throws RemoteException, NotFoundException, DataAccessException;

  // =====================================================
  // Consigli
  // =====================================================

  /**
   * Restituisce i libri consigliati dall’utente per un libro base.
   *
   * @param idUtente id utente
   * @param idLibro  id libro base
   * @return lista di libri consigliati (può essere vuota)
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  List<Libro> getSuggerimentiUtente(int idUtente, int idLibro)
      throws RemoteException, DataAccessException;

  /**
   * Restituisce una pagina di suggerimenti per un libro base.
   *
   * @param idLibro      id libro base
   * @param indicePagina indice pagina (0-based)
   * @return pagina di suggerimenti
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  PaginaConsigliRisultati cercaSuggerimentiLibro(int idLibro, int indicePagina)
      throws RemoteException, DataAccessException;

  /**
   * Inserisce un nuovo suggerimento di lettura.
   *
   * @param idUtente    id utente
   * @param idLibroBase id libro base
   * @param idLibroCons id libro consigliato
   * @throws NotFoundException      se uno dei libri non esiste
   * @throws AlreadyExistsException se il suggerimento è già presente
   * @throws LimitExceededException se è stato superato il limite consentito
   * @throws DataAccessException    in caso di errore DB
   * @throws RemoteException        in caso di errore RMI
   */
  void inserisciSuggerimentoLibro(int idUtente, int idLibroBase, int idLibroCons)
      throws RemoteException, NotFoundException, AlreadyExistsException,
      LimitExceededException, DataAccessException;

  /**
   * Elimina un suggerimento precedentemente inserito.
   *
   * @param idUtente    id utente
   * @param idLibroBase id libro base
   * @param idLibroCons id libro consigliato
   * @throws NotFoundException   se il suggerimento non esiste
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  void deleteSuggerimentoLibro(int idUtente, int idLibroBase, int idLibroCons)
      throws RemoteException, NotFoundException, DataAccessException;

  // =====================================================
  // Autenticazione / Sessioni
  // =====================================================

  /**
   * Registra un nuovo utente.
   *
   * @param u utente da registrare
   * @return dati essenziali dell’utente per la sessione
   * @throws AlreadyExistsException se email, userId o codice fiscale sono già presenti
   * @throws DataAccessException    in caso di errore DB
   * @throws RemoteException        in caso di errore RMI
   */
  UtenteSessione registrazione(Utente u)
      throws RemoteException, AlreadyExistsException, DataAccessException;

  /**
   * Esegue il login e crea una nuova sessione.
   *
   * @param userId   identificatore utente
   * @param password password dell’utente
   * @return token di sessione e dati utente
   * @throws InvalidCredentialsException se le credenziali non sono valide
   * @throws DataAccessException          in caso di errore DB
   * @throws RemoteException              in caso di errore RMI
   */
  TokenSessione loginWithToken(String userId, String password)
      throws RemoteException, InvalidCredentialsException, DataAccessException;

  /**
   * Ripristina una sessione a partire da un token.
   *
   * @param token token di sessione
   * @return dati utente associati alla sessione
   * @throws InvalidCredentialsException se il token non è valido o scaduto
   * @throws DataAccessException          in caso di errore DB
   * @throws RemoteException              in caso di errore RMI
   */
  UtenteSessione resumeSessione(String token)
      throws RemoteException, InvalidCredentialsException, DataAccessException;

  /**
   * Invalida una sessione (logout).
   *
   * @param token token da invalidare
   * @throws DataAccessException in caso di errore DB
   * @throws RemoteException     in caso di errore RMI
   */
  void logout(String token)
      throws RemoteException, DataAccessException;
}