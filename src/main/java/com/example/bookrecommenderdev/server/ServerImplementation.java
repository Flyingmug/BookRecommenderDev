package com.example.bookrecommenderdev.server;

import com.example.bookrecommenderdev.server.dao.LibroDao;
import com.example.bookrecommenderdev.server.db.DatabaseConfig;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

  // campi
  GestoreRaccolta raccolta;
  private final static LibroDao libri = new LibroDao();


  public ServerImplementation() throws RemoteException {
    super();
  }


  // metodi
  //
  //
  //
  //
  public void cercaLibro(String titolo) throws RemoteException {
    // NOTE: NOT VOID
    libri.get(123);
  }


}
