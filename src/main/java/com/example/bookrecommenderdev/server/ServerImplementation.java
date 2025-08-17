package com.example.bookrecommenderdev.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

  // campi
  GestoreRaccolta raccolta;

  public ServerImplementation() throws RemoteException {
    super();

  }

  // metodi
  public void cercaLibro(String titolo) throws RemoteException {
    // NOTE: NOT VOID
    raccolta.cercaLibro(titolo);
  }
}
