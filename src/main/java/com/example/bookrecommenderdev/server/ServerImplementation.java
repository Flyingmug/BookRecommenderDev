package com.example.bookrecommenderdev.server;

import com.example.bookrecommenderdev.server.dao.LibroDao;
import com.example.bookrecommenderdev.server.db.DatabaseConfig;

import javax.sql.DataSource;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

  // campi
  private final DataSource datasource;
  private final LibroDao libri;


  public ServerImplementation() throws RemoteException {
    super();
    datasource = DatabaseConfig.getDataSource();
    libri = new LibroDao(datasource);
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
