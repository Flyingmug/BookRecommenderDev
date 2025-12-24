package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
import bookrecommenderdev.model.auth.RegisterStatus;
import bookrecommenderdev.server.dao.*;
import bookrecommenderdev.server.db.DatabaseConfig;
import bookrecommenderdev.server.dto.*;
import javafx.util.Pair;

import javax.sql.DataSource;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static bookrecommenderdev.model.auth.AuthStatus.*;
import static bookrecommenderdev.model.auth.RegisterStatus.FISCAL_CODE_ALREADY_USED;

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

  public PaginaLibriRisultati searchTitolo(String titolo, int indicePagina) throws RemoteException {
    PaginaLibriRisultati elenco;
    try {
      elenco = libri.getPage(indicePagina, titolo);
      return elenco;
    } catch (SQLException e) {
      return null;
    }
  }

  public List<Libro> searchAutore(String autore) throws RemoteException {
      return List.of();
  }

  public List<Libro> searchAnnoAutore(String annoAutore) throws RemoteException {
      return List.of();
  }

  public PaginaLibro getPaginaLibro(int idLibro) throws RemoteException {

    try {
      Libro l = libri.get(idLibro);
      double[] v = valutazioni.getAverage(idLibro);

      return new PaginaLibro(l, v);

    } catch(SQLException e) {
      e.printStackTrace();
      return null;
    }

  }

  public LibroPaginaPersonaleDTO getPaginaLibroPersonale() throws RemoteException {

    return null;
  }

  //
  // librerie
  //
  public List<LibraryResult> getListLibrerie(long idUtente) throws RemoteException {

    try {
      return librerie.getLibrerie(idUtente);

    } catch(SQLException e) {
      System.err.println("Error in list obtaining");  // DEBUG
      e.printStackTrace();
      return null;
    }
  }

  public List<Libro> getContenutoLibreria(List<Long> idList) throws RemoteException {

    return List.of();
  }

  public void createLibreria(Libreria lib) throws RemoteException, InsertDBException {

  }

  public void deleteLibreria(Libreria lib) throws RemoteException {

  }

  public void deleteLibreria(String nome, long idUtente) throws RemoteException {

  }

  //
  // Valutazioni
  //

  public PaginaValutazioni getValutazioni(long idLibro, int indicePagina) throws RemoteException {
    PaginaValutazioni elenco;
    try {
      elenco = valutazioni.getPage(indicePagina, idLibro);
      return elenco;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String ping() throws RemoteException {
    System.out.println("PING RECEIVED");
    return "pong";
  }

  //
  // Consigli
  //

  public List<Libro> getConsigli(long idLibro) throws RemoteException {

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
  public RegisterStatus registrazione(Utente u) throws RemoteException {
    try {
//      if (utenti.findByEmail(u.getEmail()).isPresent()) {
//        return EMAIL_ALREADY_USED;
//      }

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

}
