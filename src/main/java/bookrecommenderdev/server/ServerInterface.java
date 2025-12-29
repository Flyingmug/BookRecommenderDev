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
  PaginaLibro getPaginaLibro(int idLibro) throws RemoteException, DataAccessException;

  // librerie
  List<PaginaLibreria> getListLibrerie(int idUtente, int indicePagina) throws RemoteException, DataAccessException;
  PaginaLibriRisultati searchAllLibrerie(int idUtente, SearchRequest richiesta,  int indicePagina) throws RemoteException, DataAccessException;
  PaginaLibriRisultati searchInLibreria(int idUtente, int idLibreria, int indicePagina) throws RemoteException, DataAccessException;
  Libreria getLibreriaById(int idUtente, int idLibreria) throws RemoteException, NotFoundException, DataAccessException;
  int createLibreria(int idUtente, String nomeLibreria, List<Integer> idList) throws RemoteException, DataAccessException;
  void deleteLibreria(int idUtente, int idLibreria) throws RemoteException, NotFoundException, DataAccessException;

  // valutazioni
  Valutazione getValutazione(int idLibro, int userId) throws RemoteException, DataAccessException;
  PaginaValutazioni cercaValutazioni(int idLibro, int indicePagina) throws RemoteException, DataAccessException;
  boolean inserisciValutazione(Valutazione valutazione) throws RemoteException, DataAccessException;
  void deleteValutazione(int idLibro, int id_utente) throws RemoteException, NotFoundException, DataAccessException;

  // consigli
  List<Libro> getConsigli(int idLibro) throws RemoteException;

  // autenticazione e accesso
  AuthResult login(String nome, String password) throws RemoteException;
  RegisterStatus registrazione(Utente u) throws RemoteException, InsertDBException;

  // ping
  String ping() throws RemoteException;

}
