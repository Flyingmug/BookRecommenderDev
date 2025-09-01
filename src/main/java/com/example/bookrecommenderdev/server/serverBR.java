package com.example.bookrecommenderdev.server;

import com.example.bookrecommenderdev.server.controller.ServerBRController;
import com.example.bookrecommenderdev.server.db.DatabaseConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class serverBR extends Application {

  ServerImplementation server;

  /**
   * Inizializza il server
   */
  @Override
  public void init() throws Exception {

    server = new ServerImplementation();

    try {
      // registrazione del server nel registry
      Registry reg = LocateRegistry.createRegistry(1099);
      reg.rebind("serverBR", server);

    } catch (RemoteException e) {
      // gestione errore e display nella UI
    }
  }

  /**
   * Terminazione del programma
   */
  @Override
  public void stop() {
  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(serverBR.class.getResource("serverBR-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 720, 480);
    stage.setTitle("BR Server");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {

    // test area

    // avvio applicazione
    launch();

  }
}