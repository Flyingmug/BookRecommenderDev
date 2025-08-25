package com.example.bookrecommenderdev.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.*;

public class serverBR extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(serverBR.class.getResource("serverBR-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 720, 480);
    stage.setTitle("BR Server");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {

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


    launch();

  }
}