module com.example.bookrecommenderdev {
    requires javafx.controls;
    requires javafx.fxml;
  requires java.rmi;
  requires java.sql;
  requires com.zaxxer.hikari;
  requires org.postgresql.jdbc;
  requires org.kordamp.ikonli.javafx;


  opens com.example.bookrecommenderdev.client to javafx.fxml;
  exports com.example.bookrecommenderdev.client;
  exports com.example.bookrecommenderdev.client.controller;
  opens com.example.bookrecommenderdev.client.controller to javafx.fxml;

  opens com.example.bookrecommenderdev.server to javafx.fxml;
  exports com.example.bookrecommenderdev.server;
  exports com.example.bookrecommenderdev.server.controller;
  exports com.example.bookrecommenderdev.server.dto;
  opens com.example.bookrecommenderdev.server.controller to javafx.fxml;

  exports com.example.bookrecommenderdev.model;
}