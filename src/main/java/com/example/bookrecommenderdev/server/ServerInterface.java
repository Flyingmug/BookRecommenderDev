package com.example.bookrecommenderdev.server;

import com.example.bookrecommenderdev.model.*;
import com.example.bookrecommenderdev.server.dto.LibroPaginaDTO;
import com.example.bookrecommenderdev.server.dto.LibroPaginaPersonaleDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Elenco delle operazioni rese disponibili ai client attraverso il registry di RMI.
 */
public interface ServerInterface extends Remote {

  List<Libro> searchTitolo(String titolo) throws RemoteException;
  List<Libro> searchAutore(String autore) throws RemoteException;
  List<Libro> searchAnnoAutore(String annoAutore) throws RemoteException;
  LibroPaginaDTO getPaginaLibro(long idLibro) throws RemoteException;
  LibroPaginaPersonaleDTO getPaginaLibroPersonale() throws RemoteException;
  List<Libreria> getListLibrerie(long idUtente) throws RemoteException;
  List<Libro> getContenutoLibreria(List<Long> idList) throws RemoteException;
  void createLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(Libreria lib) throws RemoteException;
  void deleteLibreria(String nome, long idUtente) throws RemoteException;
  List<Valutazione> getValutazioni(long idLibro) throws RemoteException;
  List<Libro> getConsigli(long idLibro) throws RemoteException;
  String login(String nome, String password) throws RemoteException;
  void registrazione(Utente u) throws RemoteException, InsertDBException;
}
