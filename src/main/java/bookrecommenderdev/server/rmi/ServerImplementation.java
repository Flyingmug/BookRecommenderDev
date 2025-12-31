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
import java.util.List;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

  private static final java.time.Duration TOKEN_TTL = java.time.Duration.ofDays(30);
  private final LibroDao libri;
  private final UtenteDao utenti;
  private final ValutazioneDao valutazioni;
  private final ConsiglioLibroDao consigli;
  private final LibreriaDao librerie;
  private final SessioneDao sessioni;

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

  /**
   * todo documentation
  * */
  @Override
  public PageResult<Libro> cercaLibro(SearchRequest richiesta, int indicePagina)
      throws RemoteException, DataAccessException {

    if (richiesta == null || richiesta.getTipo() == null) {
      return new PaginaLibriRisultati(List.of(), 0);
    }

    try {
      return libri.search(richiesta, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("Errore DB durante searchTitolo", e);
    }
  }


  /**
   * todo doc
   * */
  @Override
  public Libro getLibro(int idLibro)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      return libri.getBasic(idLibro)
          .orElseThrow(() -> new NotFoundException("Libro non trovato: " + idLibro));

    } catch (SQLException e) {
      throw new DataAccessException("Errore DB durante getLibro(" + idLibro + ")", e);
    }

  }

  /**
   * todo documentation
   * */
  @Override
  public PaginaLibro getPaginaLibro(int idLibro)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      Libro l = libri.getComplete(idLibro)
          .orElseThrow(() -> new NotFoundException("Libro non trovato: " + idLibro));
      double[] v = valutazioni.getAverageScores(idLibro).orElse(null);
      return new PaginaLibro(l, v);

    } catch(SQLException e) {
      throw new DataAccessException("Errore DB durante getPaginaLibro(" + idLibro + ")", e);
    }
  }


  //
  // librerie
  //

   /**
    * todo doc
    * */
  @Override
   public PaginaLibrerieRisultati getListLibrerie(int idUtente, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.getPageListLibrerie(idUtente, indicePagina);

    } catch(SQLException e) {
      throw new DataAccessException("DB error", e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public PaginaLibriRisultati searchAllLibrerie(int idUtente, SearchRequest richiesta, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.searchAll(idUtente, richiesta, indicePagina);

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public PaginaLibriRisultati searchInLibreria(int idUtente, int idLibreria, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.searchIn(idUtente, idLibreria, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("DB error", e);
    }
  }

  @Override
  public Libreria getLibreriaById(int idUtente, int idLibreria)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      return librerie.getLibreria(idUtente, idLibreria)
          .orElseThrow(() -> new NotFoundException("Libreria non trovata"));

    } catch (SQLException e) {
      throw new DataAccessException("Errore nell'ottenimento delle libreria", e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public void createLibreria(int idUtente, String nomeLibreria, List<Integer> idList)
      throws RemoteException, AlreadyExistsException, DataAccessException {
    try {
      librerie.creaLibreria(idUtente, nomeLibreria, idList);

    } catch (SQLException e) {
      if ("23505".equals(e.getSQLState())) {
        throw new AlreadyExistsException("Esiste già una libreria con questo nome.");
      }
      throw new DataAccessException("Errore di database", e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public void deleteLibreria(int idUtente, int idLibreria)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      if (!librerie.deleteLibreria(idUtente, idLibreria)) {
        throw new NotFoundException("Libreria non trovata");
      }

    } catch (SQLException e) {
      throw new DataAccessException("Errore il reperimento dati (DB).", e);
    }
  }

  /**
   * todo doc
   */
  public boolean isLibroInLibrerieUtente(int idUtente, int idLibro)
      throws RemoteException, DataAccessException {
    try {
      return librerie.verificaLibroInLibrerieUtente(idUtente, idLibro);
    } catch (SQLException e) {
      throw new DataAccessException("Errore di database", e);
    }
  }

  //
  // Valutazioni
  //

  /**
   * todo doc
   * */
  @Override
  public Valutazione getValutazione(int idLibro, int idUtente)
      throws RemoteException, DataAccessException {
    try {
      return valutazioni.get(idLibro, idUtente).orElse(null);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'ottenimento della valutazione.", e);
    }
  }

  /**
   * todo doc
   * */
  @Override
  public PaginaValutazioni cercaValutazioni(int idLibro, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return valutazioni.getPage(indicePagina, idLibro);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella ricerca delle valutazioni.", e);
    }
  }

  /**
   * sovrascrive un eventuale review esistente
   * todo doc
   * */
  @Override
  public void inserisciValutazione(Valutazione valutazione)
      throws RemoteException, DataAccessException {
    try {
      valutazioni.save(valutazione);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'inserimento della valutazione.", e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public void deleteValutazione(int idLibro, int idUtente)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      if (!valutazioni.delete(idLibro, idUtente)) {
        throw new NotFoundException("Valutazione non trovata");
      }

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella cancellazione della valutazione.", e);
    }
  }


  //
  // Consigli
  //

  public List<Libro> getConsigliUtente(int idUtente, int idLibro)
      throws RemoteException, DataAccessException {
    try {
      return consigli.getConsigliUtente(idUtente, idLibro);

    } catch (SQLException e) {
      throw new DataAccessException("Errore nell'ottenimento delle consigli.", e);
    }
  }

  @Override
  public PaginaConsigliRisultati cercaConsigli(int idLibroBase, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return consigli.searchConsigli(idLibroBase, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("Errore di database", e);
    }
  }

  @Override
  public void inserisciConsiglio(int idUtente, int idLibroBase, int idLibroCons)
      throws RemoteException, NotFoundException, AlreadyExistsException, LimitExceededException, DataAccessException {
    try {
      consigli.inserisciConsiglio(idUtente, idLibroBase, idLibroCons);

    } catch (SQLException e) {
      throw new DataAccessException("Errore di database", e);
    }
  }

  @Override
  public void deleteConsiglio(int idUtente, int idLibroBase, int idLibroCons)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      if (!consigli.deleteConsiglio(idUtente, idLibroBase, idLibroCons))
         throw new NotFoundException("Consiglio non trovato.");

    } catch (SQLException e) {
      throw new DataAccessException("Errore di database", e);
    }
  }


  //
  // Utente
  //

  /**
   * Utilizza nome e password per verificare la presenza della coppia nel database.
   * @param userId Nome utente.
   * @param password Password utente.
   * @return Risultato dell'operazione
   */
  public UtenteSessione login(String userId, String password)
      throws RemoteException, InvalidCredentialsException, DataAccessException {
    try {
      Utente u = utenti.findByUserId(userId)
          .orElseThrow(() -> new InvalidCredentialsException("Credenziali non valide."));

      // demo/plaintext compare (replace with hash verify later)
      if (!u.getPassword().equals(password)) {
        throw new InvalidCredentialsException("Credenziali non valide.");
      }

      return new UtenteSessione(
          u.getId_utente(),
          u.getNome(),
          u.getCognome(),
          u.getEmail(),
          u.getUserId()
      );

    } catch (SQLException e) {
      throw new DataAccessException("Errore di database.", e);
    }
  }

  /**
   * todo documentation
   * */
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
      throw new DataAccessException("Errore di database.", e);
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

      String token = sessioni.creaSessione(u.getId_utente(), TOKEN_TTL);

      return new TokenSessione(token, session);

    } catch (SQLException e) {
      throw new DataAccessException("Errore di database.", e);
    }
  }

  @Override
  public UtenteSessione resumeSessione(String token)
      throws RemoteException, InvalidCredentialsException, DataAccessException {

    try {
      Integer idUtente = sessioni.resolveToken(token, TOKEN_TTL).orElse(null);
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
      throw new DataAccessException("Errore di database.", e);
    }
  }

  // ping
  public String ping()
      throws RemoteException {
    System.out.println("PING RECEIVED");
    return "pong";
  }

}
