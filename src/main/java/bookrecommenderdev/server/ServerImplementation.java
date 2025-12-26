package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
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
    // campi
    DataSource datasource = DatabaseConfig.getDataSource();
    libri = new LibroDao(datasource);
    utenti = new UtenteDao(datasource);
    valutazioni = new ValutazioneDao(datasource);
    consigli = new ConsiglioLibroDao(datasource);
    librerie = new LibreriaDao(datasource);
    // TESTING DATABASE PURPOSES
//    this.searchTitolo("a");
  }

  //
  // metodi
  //

  //
  // libri
  //

  public PageResult<Libro> searchTitolo(String titolo, int pageNumber)
      throws RemoteException, DataAccessException {
    try {
      return libri.searchTitolo(pageNumber, titolo); // returns PageResult/your DTO
    } catch (SQLException e) {
      throw new DataAccessException("Errore DB durante searchTitolo", e);
    }
  }

  public List<Libro> searchAutore(String autore) throws RemoteException {
      return List.of();
  }

  public List<Libro> searchAnnoAutore(String annoAutore) throws RemoteException {
      return List.of();
  }

  /**
   *
   * */
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
   *
   * */
  public PaginaLibro getPaginaLibro(int idLibro)
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      Libro l = libri.getComplete(idLibro)
          .orElseThrow(() -> new NotFoundException("Libro non trovato: " + idLibro));
      double[] v = valutazioni.getAverage(idLibro);
      return new PaginaLibro(l, v);

    } catch(SQLException e) {
      throw new DataAccessException("Errore DB durante getPaginaLibro(" + idLibro + ")", e);
    }
  }


  //
  // librerie
  //
   /**
    *
    * */
  public List<LibraryResult> getListLibrerie(long idUtente)
      throws RemoteException, DataAccessException {
    try {
      return librerie.getLibrerie(idUtente);
    } catch(SQLException e) {
      throw new DataAccessException("DB error", e);
    }
  }

  public PaginaLibriRisultati searchAllLibrerie(long idUtente, String query, int pageNumber)
      throws RemoteException, DataAccessException {
    return null;
  }

  public PaginaLibriRisultati searchLibreria(long idUtente, String nomeLibreria,  int pageNumber)
      // todo eneds to throw notfound aswell
      throws RemoteException, NotFoundException, DataAccessException {
    try {
      return librerie.getLibraryPage(idUtente, nomeLibreria, pageNumber);
    } catch (SQLException e) {
      throw new DataAccessException("DB error", e);
    }
  }

  public void createLibreria(Libreria lib)
      throws RemoteException, InsertDBException {

  }

  public void deleteLibreria(Libreria lib)
      throws RemoteException {

  }

  public void deleteLibreria(String nome, long idUtente)
      throws RemoteException {

  }


  //
  // Valutazioni
  //

  @Override
  public Valutazione getValutazione(int idLibro, int idUtente)
      throws RemoteException {
    try {
      return valutazioni.get(idLibro, idUtente);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
      // throw new RemoteException("Error in list obtaining"); // Replace with better error
    }
  }

  public PaginaValutazioni getValutazioni(long idLibro, int indicePagina)
      throws RemoteException {
    PaginaValutazioni elenco;
    try {
      elenco = valutazioni.getPage(indicePagina, idLibro);
      return elenco;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean inserisciValutazione(Valutazione valutazione)
      throws RemoteException {
    try {
      return valutazioni.save(valutazione);
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean deleteValutazione(int idLibro, int idUtente)
      throws RemoteException {
    try {
      return valutazioni.delete(idLibro, idUtente);
      // todo gestione true/false per valutazione inesistente o rimossa
      // todo POSSIBILE SOLUZIONE: usare false come "database intoccato"
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }


  //
  // Consigli
  //

  public List<Libro> getConsigli(long idLibro)
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
      e.printStackTrace();
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
