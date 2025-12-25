package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
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

  PageResult<Libro> searchTitolo(String titolo, int indicePagina) throws RemoteException;
  List<Libro> searchAutore(String autore) throws RemoteException;
  List<Libro> searchAnnoAutore(String annoAutore) throws RemoteException;
  PaginaLibro getPaginaLibro(int idLibro) throws RemoteException;
  LibroPaginaPersonaleDTO getPaginaLibroPersonale() throws RemoteException;
  List<LibraryResult> getListLibrerie(long idUtente) throws RemoteException;
  PaginaLibriRisultati searchFromLibrerie(long idUtente, String query,  int pageNumber) throws RemoteException;
  PaginaLibriRisultati searchLibreria(long idUtente, String nomeLibreria,  int pageNumber) throws RemoteException;
  void createLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(String nome, long idUtente) throws RemoteException;
  Valutazione getValutazione(int idLibro, int userId) throws RemoteException;
  PaginaValutazioni getValutazioni(long idLibro, int indicePagina) throws RemoteException;
  boolean inserisciValutazione(Valutazione valutazione) throws RemoteException;
  boolean deleteValutazione(int idLibro, int id_utente) throws RemoteException;
  List<Libro> getConsigli(long idLibro) throws RemoteException;
  AuthResult login(String nome, String password) throws RemoteException;
  RegisterStatus registrazione(Utente u) throws RemoteException, InsertDBException;
  String ping() throws RemoteException;

}
