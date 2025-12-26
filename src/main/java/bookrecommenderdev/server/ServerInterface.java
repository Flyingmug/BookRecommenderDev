package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.auth.RegisterStatus;
import bookrecommenderdev.model.data.PageResult;
import bookrecommenderdev.server.dto.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Elenco delle operazioni rese disponibili ai client attraverso il registry di RMI.
 */
public interface ServerInterface extends Remote {

  // ricerca
  PageResult<Libro> cercaLibro(SearchRequest richiesta, int indicePagina) throws RemoteException, DataAccessException;

  // libri
  Libro getLibro(int idLibro) throws RemoteException, NotFoundException, DataAccessException;
  PaginaLibro getPaginaLibro(int idLibro) throws RemoteException;

  // librerie
  PaginaLibriRisultati searchAllLibrerie(long idUtente, String query,  int pageNumber) throws RemoteException;
  PaginaLibriRisultati searchLibreria(long idUtente, String nomeLibreria,  int pageNumber) throws RemoteException;
  List<LibraryResult> getListLibrerie(long idUtente) throws RemoteException;
  void createLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(String nome, long idUtente) throws RemoteException;

  // valutazioni
  Valutazione getValutazione(int idLibro, int userId) throws RemoteException;
  PaginaValutazioni getValutazioni(long idLibro, int indicePagina) throws RemoteException;
  boolean inserisciValutazione(Valutazione valutazione) throws RemoteException;
  boolean deleteValutazione(int idLibro, int id_utente) throws RemoteException;

  // consigli
  List<Libro> getConsigli(long idLibro) throws RemoteException;

  // autenticazione e accesso
  AuthResult login(String nome, String password) throws RemoteException;
  RegisterStatus registrazione(Utente u) throws RemoteException, InsertDBException;

  // ping
  String ping() throws RemoteException;

}
