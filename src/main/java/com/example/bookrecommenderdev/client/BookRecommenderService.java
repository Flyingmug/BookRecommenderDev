package com.example.bookrecommenderdev.client;

import com.example.bookrecommenderdev.server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BookRecommenderService {
  private static ServerInterface bookRecommender;

  public static void init(String host, int port) throws RemoteException, NotBoundException {
    Registry reg = LocateRegistry.getRegistry(host, port);
    bookRecommender = (ServerInterface) reg.lookup("serverBR");
  }

  public static ServerInterface getServer() {
    return bookRecommender;
  }
}
