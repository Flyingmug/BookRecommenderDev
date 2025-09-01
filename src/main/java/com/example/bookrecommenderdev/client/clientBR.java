package com.example.bookrecommenderdev.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class clientBR extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(clientBR.class.getResource("clientBR-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1100, 680);
    stage.setTitle("BookRecommender");

    URL stylesurl = getClass().getResource("/com/example/bookrecommenderdev/styles/styles.css");
    if (stylesurl != null)
      scene.getStylesheets().add(stylesurl.toExternalForm());

    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}