package bookrecommenderdev.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import java.net.URL;

public class serverBR extends Application {

  @Override
  public void start(Stage stage) throws IOException {

    Font.loadFont(
        getClass().getResourceAsStream("/bookrecommenderdev/fonts/Gabriola/Gabriola.ttf"),
        16
    );

    FXMLLoader fxmlLoader =
        new FXMLLoader(serverBR.class.getResource("serverBR-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 720, 480);
    stage.setTitle("BR Server");

    URL stylesurl =
        getClass().getResource("/bookrecommenderdev/styles/server-styles.css");
    if (stylesurl != null)
      scene.getStylesheets().add(stylesurl.toExternalForm());

    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}