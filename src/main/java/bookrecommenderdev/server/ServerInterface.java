package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.model.exceptions.*;
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
  PaginaLibrerieRisultati getListLibrerie(int idUtente, int indicePagina) throws RemoteException, DataAccessException;
  PaginaLibriRisultati searchAllLibrerie(int idUtente, SearchRequest richiesta,  int indicePagina) throws RemoteException, DataAccessException;
  PaginaLibriRisultati searchInLibreria(int idUtente, int idLibreria, int indicePagina) throws RemoteException, DataAccessException;
  Libreria getLibreriaById(int idUtente, int idLibreria) throws RemoteException, NotFoundException, DataAccessException;
  void createLibreria(int idUtente, String nomeLibreria, List<Integer> idList) throws RemoteException, AlreadyExistsException, DataAccessException;
  void deleteLibreria(int idUtente, int idLibreria) throws RemoteException, NotFoundException, DataAccessException;
  boolean isLibroInLibrerieUtente(int idUtente, int idLibro) throws RemoteException, NotFoundException, DataAccessException;

  // valutazioni
  Valutazione getValutazione(int idLibro, int userId) throws RemoteException, DataAccessException;
  PaginaValutazioni cercaValutazioni(int idLibro, int indicePagina) throws RemoteException, DataAccessException;
  void inserisciValutazione(Valutazione valutazione) throws RemoteException, DataAccessException;
  void deleteValutazione(int idLibro, int id_utente) throws RemoteException, NotFoundException, DataAccessException;

  // consigli
  List<Libro> getConsigliUtente(int idUtente, int idLibro) throws RemoteException;
  PaginaConsigliRisultati cercaConsigli(int idLibro, int indicePagina) throws RemoteException, DataAccessException;
  void inserisciConsiglio(int idUtente, int idLibroBase, int idLibroCons) throws RemoteException, NotFoundException, AlreadyExistsException, LimitExceededException, DataAccessException;
  void deleteConsiglio(int idUtente, int idLibroBase, int idLibroCons) throws RemoteException, NotFoundException, DataAccessException;

  // autenticazione e accesso
  UtenteSessione login(String userId, String password) throws RemoteException, InvalidCredentialsException, DataAccessException;
  UtenteSessione registrazione(Utente u) throws RemoteException, AlreadyExistsException, DataAccessException;

  // sessione
  TokenSessione loginWithToken(String userId, String password) throws RemoteException, InvalidCredentialsException, DataAccessException;
  UtenteSessione resumeSessione(String token) throws RemoteException, InvalidCredentialsException, DataAccessException;
  void logout(String token) throws RemoteException, DataAccessException;

}
