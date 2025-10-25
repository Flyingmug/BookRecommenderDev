package bookrecommenderdev.server;

import bookrecommenderdev.model.*;
import bookrecommenderdev.server.dto.PaginaLibro;
import bookrecommenderdev.server.dto.LibroPaginaPersonaleDTO;
import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Elenco delle operazioni rese disponibili ai client attraverso il registry di RMI.
 */
public interface ServerInterface extends Remote {

  Pair<List<Libro>, Integer> searchTitolo(String titolo, int indicePagina) throws RemoteException;
  List<Libro> searchAutore(String autore) throws RemoteException;
  List<Libro> searchAnnoAutore(String annoAutore) throws RemoteException;
  PaginaLibro getPaginaLibro(int idLibro) throws RemoteException;
  LibroPaginaPersonaleDTO getPaginaLibroPersonale() throws RemoteException;
  List<Pair<Libreria, Integer>> getListLibrerie(long idUtente) throws RemoteException;
  List<Libro> getContenutoLibreria(List<Long> idList) throws RemoteException;
  void createLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(String nome, long idUtente) throws RemoteException;
  List<Valutazione> getValutazioni(long idLibro) throws RemoteException;
  List<Libro> getConsigli(long idLibro) throws RemoteException;
  Pair<Utente, AuthStatus> login(String nome, String password) throws RemoteException;
  String registrazione(Utente u) throws RemoteException, InsertDBException;
}
