package bookrecommenderdev.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class clientBR extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    Font.loadFont(getClass().getResourceAsStream("/bookrecommenderdev/fonts/Montserrat/static/Montserrat-Light.ttf"), 16);
    Font.loadFont(getClass().getResourceAsStream("/bookrecommenderdev/fonts/Montserrat/static/Montserrat-Regular.ttf"), 16);
    Font.loadFont(getClass().getResourceAsStream("/bookrecommenderdev/fonts/Montserrat/static/Montserrat-Medium.ttf"), 16);
    Font.loadFont(getClass().getResourceAsStream("/bookrecommenderdev/fonts/Montserrat/static/Montserrat-SemiBold.ttf"), 16);
    Font.loadFont(getClass().getResourceAsStream("/bookrecommenderdev/fonts/Montserrat/static/Montserrat-Bold.ttf"), 16);
    Font.loadFont(getClass().getResourceAsStream("/bookrecommenderdev/fonts/Montserrat/static/Montserrat-Black.ttf"), 16);

    FXMLLoader fxmlLoader = new FXMLLoader(clientBR.class.getResource("root.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1100, 680);
    stage.setTitle("BookRecommender");

    URL stylesurl = getClass().getResource("/bookrecommenderdev/styles/styles.css");
    if (stylesurl != null)
      scene.getStylesheets().add(stylesurl.toExternalForm());

    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}