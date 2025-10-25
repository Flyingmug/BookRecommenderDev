package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
import bookrecommenderdev.server.dao.*;
import bookrecommenderdev.model.*;
import bookrecommenderdev.server.dao.*;
import bookrecommenderdev.server.db.DatabaseConfig;
import bookrecommenderdev.server.dto.PaginaLibro;
import bookrecommenderdev.server.dto.LibroPaginaPersonaleDTO;
import javafx.util.Pair;

import javax.sql.DataSource;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

import static bookrecommenderdev.model.AuthStatus.*;

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

  public Pair<List<Libro>, Integer> searchTitolo(String titolo, int indicePagina) throws RemoteException {
    Pair<List<Libro>, Integer> elenco = null;
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

//      List<ConsigliLettura> listaConsigli = consigli.getAll(idLibro, true);
//      List<Integer> elencoIdLibri = new LinkedList<>();
//      for (ConsigliLettura c: listaConsigli) {
//        elencoIdLibri.add( c.getIdconsiglio1());
//        elencoIdLibri.add( c.getIdconsiglio2());
//        elencoIdLibri.add( c.getIdconsiglio3());
//        System.out.println(c.getIdconsiglio1());
//        System.out.println(c.getIdconsiglio2());
//        System.out.println(c.getIdconsiglio3());
//      }

      return new PaginaLibro(l, v);

    } catch(SQLException e) {
      System.out.println("test");
      e.printStackTrace();
      System.out.println(e.getMessage());
      return null;
    }

  }

  public LibroPaginaPersonaleDTO getPaginaLibroPersonale() throws RemoteException {

    return null;
  }

  //
  // librerie
  //
  public List<Pair<Libreria, Integer>> getListLibrerie(long idUtente) throws RemoteException {

    try {
      List<Pair<Libreria, Integer>> libs = librerie.getLibrerie(idUtente);
      return libs;
    } catch(SQLException e) {
      System.err.println("Error in list obtaining");
      e.printStackTrace();
      System.out.println(e.getMessage());
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

  public List<Valutazione> getValutazioni(long idLibro) throws RemoteException {

    return List.of();
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
   * Utilizza nome e password per controllare la presenza della coppia nel database
   *
   * @param email    nome utente
   * @param password password utente
   * @return token di sessione............
   */
  public Pair<Utente, AuthStatus> login(String email, String password) throws RemoteException {

    try {
      List<Utente> lista = utenti.get(email, password, false);
      System.out.println("Size: " + lista.size());

      if (lista.isEmpty()) return new Pair<>(null, NO_SUCH_USER);

      return new Pair<>(lista.getFirst(), SUCCESS);
    } catch (SQLException e) {
      return new Pair<>(null, DB_ERROR);
    }
  }

  public String registrazione(Utente u) throws RemoteException, InsertDBException {

    try {
      List<Utente> lista = utenti.get(u.getEmail(), u.getCodiceFiscale(), true);

      System.out.println("utenti: " + lista.size());

      if(lista.contains(u)) return "user-exists";

      u.setUserId();
      boolean outcome = utenti.save(u);

      return outcome ? "success":"insert-error";
    } catch (SQLException e) {
      e.printStackTrace();
      return "db-error";
    }
  }

}
