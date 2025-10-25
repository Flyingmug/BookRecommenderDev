module com.example.bookrecommenderdev {
    requires javafx.controls;
    requires javafx.fxml;
  requires java.rmi;
  requires java.sql;
  requires com.zaxxer.hikari;
  requires org.postgresql.jdbc;
  requires org.kordamp.ikonli.javafx;
  requires java.desktop;
  requires org.slf4j;


  opens bookrecommenderdev.client to javafx.fxml;
  exports bookrecommenderdev.client;
  exports bookrecommenderdev.client.controller;
  opens bookrecommenderdev.client.controller to javafx.fxml;

  opens bookrecommenderdev.server to javafx.fxml;
  exports bookrecommenderdev.server;
  exports bookrecommenderdev.server.controller;
  exports bookrecommenderdev.server.dto;
  opens bookrecommenderdev.server.controller to javafx.fxml;

  exports bookrecommenderdev.model;
  exports bookrecommenderdev;
  opens bookrecommenderdev to javafx.fxml;
  exports bookrecommenderdev.routing;
  opens bookrecommenderdev.routing to javafx.fxml;
}