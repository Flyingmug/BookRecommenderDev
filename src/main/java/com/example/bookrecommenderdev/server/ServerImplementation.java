package com.example.bookrecommenderdev.server;

import com.example.bookrecommenderdev.model.*;
import com.example.bookrecommenderdev.server.dao.LibroDao;
import com.example.bookrecommenderdev.server.dao.UtenteDao;
import com.example.bookrecommenderdev.server.db.DatabaseConfig;
import com.example.bookrecommenderdev.server.dto.LibroPaginaDTO;
import com.example.bookrecommenderdev.server.dto.LibroPaginaPersonaleDTO;
import javafx.util.Pair;

import javax.sql.DataSource;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

  // campi
  private final DataSource datasource;
  private final LibroDao libri;
  private final UtenteDao utenti;

  public ServerImplementation() throws RemoteException {
    super();
    datasource = DatabaseConfig.getDataSource();
    libri = new LibroDao(datasource);
    utenti = new UtenteDao(datasource);
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
    Pair<List<Libro>, Integer> elenco = libri.getPage(indicePagina, titolo);
    System.out.println("Numero risultati SERVER: " + elenco.getKey().size());
    return elenco;
  }
  public List<Libro> searchAutore(String autore) throws RemoteException {
      return List.of();
  }

  public List<Libro> searchAnnoAutore(String annoAutore) throws RemoteException {
      return List.of();
  }

  public LibroPaginaDTO getPaginaLibro(long idLibro) throws RemoteException {

    return null;
  }

  public LibroPaginaPersonaleDTO getPaginaLibroPersonale() throws RemoteException {

    return null;
  }

  //
  // librerie
  //
  public List<Libreria> getListLibrerie(long idUtente) throws RemoteException {

    return List.of();
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
   * @param email nome utente
   * @param password password utente
   * @return token di sessione............
   */
  public String login(String email, String password) throws RemoteException {

    try {
      List<Utente> lista = utenti.get(email, password, false);
      System.out.println("Size: " + lista.size());

      if (lista.isEmpty()) return "no-such-user";

      return "success";
    } catch (SQLException e) {
      return "db-error";
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
