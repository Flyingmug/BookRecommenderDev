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
  requires javafx.web;
  requires javafx.graphics;
  requires javafx.base;
  requires java.prefs;
  requires java.xml.crypto;

  exports bookrecommenderdev.client;
  opens bookrecommenderdev.client to javafx.fxml;
  exports bookrecommenderdev.client.controller;
  opens bookrecommenderdev.client.controller to javafx.fxml;
  exports bookrecommenderdev.client.controller.layouts;
  opens bookrecommenderdev.client.controller.layouts to javafx.fxml;
  exports bookrecommenderdev.client.controller.components;
  opens bookrecommenderdev.client.controller.components to javafx.fxml;
  exports bookrecommenderdev.client.controller.errors;
  opens bookrecommenderdev.client.controller.errors to javafx.fxml;
  opens bookrecommenderdev.client.errors.components to javafx.fxml;
  opens bookrecommenderdev.client.errors to javafx.fxml;


  opens bookrecommenderdev.server to javafx.fxml;
  exports bookrecommenderdev.server;
  exports bookrecommenderdev.server.controller;
  exports bookrecommenderdev.model.dto;
  opens bookrecommenderdev.server.controller to javafx.fxml;

  exports bookrecommenderdev.model;
  exports bookrecommenderdev;
  exports bookrecommenderdev.model.data;
  opens bookrecommenderdev to javafx.fxml;
  exports bookrecommenderdev.client.routing;
  opens bookrecommenderdev.client.routing to javafx.fxml;
  exports bookrecommenderdev.client.routing.history;
  opens bookrecommenderdev.client.routing.history to javafx.fxml;
  exports bookrecommenderdev.client.routing.route;
  opens bookrecommenderdev.client.routing.route to javafx.fxml;
  exports bookrecommenderdev.client.routing.layout;
  opens bookrecommenderdev.client.routing.layout to javafx.fxml;
  exports bookrecommenderdev.client.routing.animation;
  opens bookrecommenderdev.client.routing.animation to javafx.fxml;
  exports bookrecommenderdev.client.controller.components.controls;
  opens bookrecommenderdev.client.controller.components.controls to javafx.fxml;
  exports bookrecommenderdev.client.auth;
  exports bookrecommenderdev.client.controller.errors.components;
  opens bookrecommenderdev.client.controller.errors.components to javafx.fxml;
  exports bookrecommenderdev.model.exceptions;
  exports bookrecommenderdev.model.base;
  exports bookrecommenderdev.server.rmi;
  opens bookrecommenderdev.server.rmi to javafx.fxml;
  exports bookrecommenderdev.server.auth;
  opens bookrecommenderdev.server.auth to javafx.fxml;
  opens bookrecommenderdev.model to javafx.fxml;
}