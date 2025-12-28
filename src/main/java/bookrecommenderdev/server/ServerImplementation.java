package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.auth.RegisterStatus;
import bookrecommenderdev.model.data.PageResult;
import bookrecommenderdev.server.dao.*;
import bookrecommenderdev.server.db.DatabaseConfig;
import bookrecommenderdev.server.dto.*;

import javax.sql.DataSource;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static bookrecommenderdev.routing.auth.AuthStatus.*;
import static bookrecommenderdev.routing.auth.RegisterStatus.FISCAL_CODE_ALREADY_USED;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

  private final LibroDao libri;
  private final UtenteDao utenti;
  private final ValutazioneDao valutazioni;
  private final ConsiglioLibroDao consigli;
  private final LibreriaDao librerie;

  public ServerImplementation() throws RemoteException {
    super();
    DataSource datasource = DatabaseConfig.getDataSource();
    libri = new LibroDao(datasource);
    utenti = new UtenteDao(datasource);
    valutazioni = new ValutazioneDao(datasource);
    consigli = new ConsiglioLibroDao(datasource);
    librerie = new LibreriaDao(datasource);
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
   public List<LibraryResult> getListLibrerie(int idUtente, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.getPageLibrerie(idUtente, indicePagina);

    } catch(SQLException e) {
      e.printStackTrace();
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
  public PaginaLibriRisultati searchInLibreria(int idLibreria, int indicePagina)
      throws RemoteException, DataAccessException {
    try {
      return librerie.searchIn(idLibreria, indicePagina);

    } catch (SQLException e) {
      throw new DataAccessException("DB error", e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public int createLibreria(int idUtente, String nomeLibreria, List<Integer> idList)
      throws RemoteException, InsertDBException {
    try {
      return librerie.creaLibreria(idUtente, nomeLibreria, idList);

    } catch (SQLException e) {
      throw new DataAccessException("Database Error", e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public boolean deleteLibreria(int id_libreria)
      throws RemoteException, DataAccessException {
    try {
      return librerie.deleteLibreria(id_libreria);

    } catch (SQLException e) {
      throw new DataAccessException("Errore durante la comunicazione con il database.", e);
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
   * todo doc
   * */
  @Override
  public boolean inserisciValutazione(Valutazione valutazione)
      throws RemoteException, DataAccessException {
    try {
      return valutazioni.save(valutazione);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nell'inserimento della valutazione.", e);
    }
  }

  /**
   * todo doc
   */
  @Override
  public boolean deleteValutazione(int idLibro, int idUtente)
      throws RemoteException, DataAccessException {
    try {
      return valutazioni.delete(idLibro, idUtente);

    } catch (SQLException e) {
      throw new DataAccessException("Errore (DB) nella cancellazione della valutazione.", e);
    }
  }


  //
  // Consigli
  //

  public List<Libro> getConsigli(int idLibro)
      throws RemoteException {

    return List.of();
  }

  //
  // Utente
  //

  /**
   * Utilizza nome e password per verificare la presenza della coppia nel database.
   * @param email Nome utente.
   * @param password Password utente.
   * @return Risultato dell'operazione
   */
  public AuthResult login(String email, String password) throws RemoteException {
    try {
      System.out.println("SERVER login request.");
      Optional<Utente> user = utenti.findByEmailAndPassword(email, password);

      return user
          .map(u -> new AuthResult(u, SUCCESS))      // If user exists, wrap in AuthResult
          .orElseGet(() -> new AuthResult(null, NO_SUCH_USER));

    } catch (SQLException e) {
      e.printStackTrace();
      return new AuthResult(null, DB_ERROR);
    }
  }

  /**
   * todo documentation
   * */
  public RegisterStatus registrazione(Utente u)
      throws RemoteException, DataAccessException {
    try {

      if (utenti.findByFiscalCode(u.getCodiceFiscale()).isPresent()) {
        return FISCAL_CODE_ALREADY_USED;
      }

      u.setUserId();
      utenti.save(u);

      return RegisterStatus.SUCCESS;

    } catch (SQLException e) {
      return RegisterStatus.DB_ERROR;
    }
  }



  // ping
  public String ping()
      throws RemoteException {
    System.out.println("PING RECEIVED");
    return "pong";
  }

}
