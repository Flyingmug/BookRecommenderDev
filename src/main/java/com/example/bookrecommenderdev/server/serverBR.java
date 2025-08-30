package com.example.bookrecommenderdev.server;

import com.example.bookrecommenderdev.server.controller.ServerBRController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.*;

public class serverBR extends Application {

  private ServerBRController controller;

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(serverBR.class.getResource("serverBR-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 720, 480);
    controller = fxmlLoader.getController();
    stage.setTitle("BR Server");
    stage.setScene(scene);
    stage.show();



    // test area

    // creazione del server e rebind alla registry
//    try {
//      ServerImplementation server = new ServerImplementation();
//      Registry reg = LocateRegistry.createRegistry(1099);
//      reg.rebind("BRServer", server);
//      System.out.println("Server running");
//    } catch (RemoteException e) {
//
//    }



  }

  public static void main(String[] args) {

    // test area

    // avvio applicazione
    launch();

    // connessione database locale
    String url = "jdbc:postgresql://localhost:5432/bookrecommenderdev";
    String user = "postgres";
    String password = "11111111";

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
      if (conn != null) {
        System.out.println("Connected to PostgreSQL successfully!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } 


  }
}