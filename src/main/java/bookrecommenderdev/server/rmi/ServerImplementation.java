package bookrecommenderdev.server.rmi;

import bookrecommenderdev.model.ServerInterface;
import bookrecommenderdev.model.base.Libreria;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.base.Utente;
import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.model.exceptions.*;
import bookrecommenderdev.model.data.PageResult;
import bookrecommenderdev.server.dao.*;
import bookrecommenderdev.server.db.DatabaseConfig;
import bookrecommenderdev.model.dto.*;

import javax.sql.DataSource;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static bookrecommenderdev.Constants.TOKEN_TTL;

/**
 * Implementazione concreta dell'interfaccia remota del server.
 *
 * <p>Questa classe rappresenta il punto di ingresso principale per tutte
 * le chiamate remote effettuate dai client tramite RMI.</p>
 *
 * <p>L'implementazione espone i servizi applicativi definiti
 * nell'interfaccia {@link ServerInterface}, delegando la logica
 * di gestione e l'accesso ai dati ai componenti interni del server
 * (DAO).</p>
 *
 * <h2>Architettura</h2>
 * <ul>
 *   <li>Servizi utilizzabili tramite Java RMI</li>
 *   <li>Thread-safe grazie alla gestione della concorrenza</li>
 * </ul>
 *
 * <h2>Gestione degli errori</h2>
 * <p>Le eccezioni di tipo {@link RemoteException} indicano problemi
 * di comunicazione remota</p>
 * <p>Le eccezioni di tipo {@link DataAccessException} indicano problemi
 * di comunicazione con il database</p>
 * <p>Eventuali stati vengono comunicati con eccezioni ad hoc (es: {@link AlreadyExistsException})
 * </p>
 */
public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

  private final Duration tokenLifetime = Duration.ofDays(TOKEN_TTL);
  private final LibroDao libri;
  private final UtenteDao utenti;
  private final ValutazioneDao valutazioni;
  private final ConsiglioLibroDao consigli;
  private final LibreriaDao librerie;
  private final SessioneDao sessioni;


  /**
   * Costruttore del server remoto.
   *
   * <p>Il costruttore esporta l'oggetto remoto rendendolo disponibile
   * per l'invocazione tramite RMI.</p>
   *
   * <p>Inizializza il server (connessione al database,
   * inizializzazione dei servizi DAO).</p>
   *
   * @throws RemoteException se l'esportazione dell'oggetto remoto fallisce
   */
  public ServerImplementation() throws RemoteException {
    super();
    DataSource datasource = DatabaseConfig.getDataSource();
    libri = new LibroDao(datasource);
    utenti = new UtenteDao(datasource);
    librerie = new LibreriaDao(datasource);
    valutazioni = new ValutazioneDao(datasource);
    consigli = new ConsiglioLibroDao(datasource);
    sessioni = new SessioneDao(datasource);
  }

  //
  // libri
  //

  @Override
  public PageResult<Libro> cercaLibro(SearchRequest richiesta, int indicePagina)
      throws RemoteException, DataAccessException {

    if (richiesta == null || richiesta.getTipo() == null) {
      return new PaginaLibriRisultati(List.of(), 0);
    }

    try {
      return libri.search(richiesta, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) durante la ricerca del libro", e);
    }
  }

  @Override
  public Libro getLibro(int idLibro)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      return libri.getBasic(idLibro)
          .orElseThrow(() -> new NotFoundException("Libro non trovato: " + idLibro));

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) durante getLibro(" + idLibro + ")", e);
    }

  }

  @Override
  public PaginaLibro getLibroCompleto(int idLibro)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      Libro l = libri.getComplete(idLibro)
          .orElseThrow(() -> new NotFoundException("Libro non trovato: " + idLibro));
      double[] v = valutazioni.getAverageScores(idLibro).orElse(null);
      return new PaginaLibro(l, v);

    } catch(SQLException e) {
      throw new DataAccessException("Errore (DB) durante getLibroCompleto(" + idLibro + ")", e);
    }
  }

  //
  // librerie
  //

  @Override
   public PaginaLibrerieRisultati getListLibrerie(int idUtente, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.getPageListLibrerie(idUtente, indicePagina);

    } catch(SQLException e) {
      throw new DataAccessException("Errore (DB) durante la ricerca di librerie", e);
    }
  }

  @Override
  public PaginaLibriRisultati searchAllLibrerie(int idUtente, SearchRequest richiesta, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.searchAll(idUtente, richiesta, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) durante la ricerca nelle librerie", e);
    }
  }

  @Override
  public PaginaLibriRisultati searchInLibreria(int idUtente, int idLibreria, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.searchIn(idUtente, idLibreria, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella ricerca in una libreria", e);
    }
  }

  @Override
  public Libreria getLibreriaById(int idUtente, int idLibreria)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      return librerie.getLibreria(idUtente, idLibreria)
          .orElseThrow(() -> new NotFoundException("Libreria non trovata"));

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'ottenimento della libreria", e);
    }
  }

  @Override
  public void registraLibreria(int idUtente, String nomeLibreria, List<Integer> idList)
      throws RemoteException, AlreadyExistsException, DataAccessException {
    try {
      librerie.creaLibreria(idUtente, nomeLibreria, idList);

    } catch (SQLException e) {
      if ("23505".equals(e.getSQLState())) {
        throw new AlreadyExistsException("Esiste già una libreria con questo nome");
      }
      throw new DataAccessException("Errore (DB) nella creazione della libreria", e);
    }
  }

  @Override
  public void deleteLibreria(int idUtente, int idLibreria)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      if (!librerie.deleteLibreria(idUtente, idLibreria)) {
        throw new NotFoundException("Libreria non trovata");
      }

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nel reperimento datì della libreria", e);
    }
  }

  public boolean isLibroInLibrerieUtente(int idUtente, int idLibro)
      throws RemoteException, DataAccessException {
    try {
      return librerie.verificaLibroInLibrerieUtente(idUtente, idLibro);
    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella verifica del libro", e);
    }
  }

  //
  // Valutazioni
  //

  @Override
  public Valutazione getValutazione(int idLibro, int idUtente)
      throws RemoteException, DataAccessException {
    try {
      return valutazioni.get(idLibro, idUtente).orElse(null);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'ottenimento della valutazione", e);
    }
  }

  @Override
  public PaginaValutazioni cercaValutazioni(int idLibro, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return valutazioni.getPage(indicePagina, idLibro);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella ricerca delle valutazioni", e);
    }
  }

  /**
   * sovrascrive un eventuale review esistente
   * */
  @Override
  public void inserisciValutazioneLibro(Valutazione valutazione)
      throws RemoteException, DataAccessException {
    try {
      valutazioni.save(valutazione);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'inserimento della valutazione", e);
    }
  }

  @Override
  public void deleteValutazione(int idLibro, int idUtente)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      if (!valutazioni.delete(idLibro, idUtente)) {
        throw new NotFoundException("Valutazione non trovata");
      }

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella cancellazione della valutazione", e);
    }
  }


  //
  // Consigli
  //

  public List<Libro> getSuggerimentiUtente(int idUtente, int idLibro)
      throws RemoteException, DataAccessException {
    try {
      return consigli.getConsigliUtente(idUtente, idLibro);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'ottenimento dei consigli dell'utente", e);
    }
  }

  @Override
  public PaginaConsigliRisultati cercaSuggerimentiLibro(int idLibroBase, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return consigli.searchConsigli(idLibroBase, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella ricerca dei consigli", e);
    }
  }

  @Override
  public void inserisciSuggerimentoLibro(int idUtente, int idLibroBase, int idLibroCons)
      throws RemoteException, NotFoundException, AlreadyExistsException, LimitExceededException, DataAccessException {
    try {
      consigli.inserisciConsiglio(idUtente, idLibroBase, idLibroCons);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'inserimento del consiglio", e);
    }
  }

  @Override
  public void deleteSuggerimentoLibro(int idUtente, int idLibroBase, int idLibroCons)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      if (!consigli.deleteConsiglio(idUtente, idLibroBase, idLibroCons))
         throw new NotFoundException("Consiglio non trovato.");

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella ricerca del consiglio", e);
    }
  }


  //
  // Utente
  //

  public UtenteSessione registrazione(Utente u)
      throws RemoteException, AlreadyExistsException, DataAccessException {
    try {
      utenti.save(u);

      return new UtenteSessione(
          u.getId_utente(),
          u.getNome(),
          u.getCognome(),
          u.getEmail(),
          u.getUserId()
      );

    } catch (SQLException e) {
      if ("23505".equals(e.getSQLState())) {
        throw new AlreadyExistsException("Email, UserId o Codice Fiscale già utilizzati.");
      }
      throw new DataAccessException("Errore (DB) nella creazione dell'utente", e);
    }
  }

  @Override
  public TokenSessione loginWithToken(String userId, String password)
      throws RemoteException, InvalidCredentialsException, DataAccessException {

    try {
      Utente u = utenti.findByUserId(userId)
          .orElseThrow(() -> new InvalidCredentialsException("Credenziali non valide."));

      if (!u.getPassword().equals(password)) {
        throw new InvalidCredentialsException("Credenziali non valide.");
      }

      UtenteSessione session = new UtenteSessione(u.getId_utente(), u.getNome(), u.getCognome(), u.getEmail(), u.getUserId());

      String token = sessioni.creaSessione(u.getId_utente(), tokenLifetime);

      return new TokenSessione(token, session);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella ricerca dell'utente", e);
    }
  }

  @Override
  public UtenteSessione resumeSessione(String token)
      throws RemoteException, InvalidCredentialsException, DataAccessException {

    try {
      Integer idUtente = sessioni.resolveToken(token, tokenLifetime).orElse(null);
      if (idUtente == null) throw new InvalidCredentialsException("Sessione non valida.");

      Utente u = utenti.findById(idUtente)
          .orElseThrow(() -> new InvalidCredentialsException("Sessione non valida."));

      return new UtenteSessione(u.getId_utente(), u.getNome(), u.getCognome(), u.getEmail(), u.getUserId());

    } catch (SQLException e) {
      throw new DataAccessException("Errore di database.", e);
    }
  }

  @Override
  public void logout(String token) throws RemoteException, DataAccessException {
    try {
      sessioni.deleteToken(token);
    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'eliminazione del token", e);
    }
  }

  // ping
  public String ping()
      throws RemoteException {
    System.out.println("PING RECEIVED");
    return "pong";
  }

}
