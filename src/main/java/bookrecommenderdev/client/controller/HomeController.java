package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;


/**
 * Controller JavaFX della pagina Home dell’applicazione.
 *
 * <p>Gestisce la barra di ricerca principale e l'organizzazione degli asset utilizzati nella pagina.
 */
public class HomeController {

  @FXML private StackPane welcomePane;
  @FXML private Pane logoPane;
  @FXML private SearchbarController searchbarController;

  /**
   * Imposta lo sfondo della pagina, lo sfondo della sezione logo, e imposta l'handler di ricerca.
   */
  @FXML
  private void initialize() {
    setTitleBackground();
    setLogo();
    searchbarController.setOnSearch(req -> Router.go("/search", req));
  }

  /**
   * Imposta lo sfondo dell’area di benvenuto usando l’immagine del titolo (modificata da vari effetti).
   */
  private void setTitleBackground() {
    URL imageUrl = getClass().getResource("/bookrecommenderdev/assets/book-recommender-title.png");
    if (imageUrl != null) {
      Image originalImage = new Image(imageUrl.toExternalForm());

      ImageView tempView = new ImageView(originalImage);
      tempView.setOpacity(0.6);

      SnapshotParameters params = new SnapshotParameters();
      params.setFill(Color.TRANSPARENT);
      Image transparentImage = tempView.snapshot(params, null);

      BackgroundImage backgroundImage = new BackgroundImage(
          transparentImage,
          BackgroundRepeat.NO_REPEAT,
          BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(1077, 123,
              false, false, true, false
          )
      );

      if (welcomePane != null) {
        welcomePane.setBackground(new Background(backgroundImage));
      }
    }
  }

  /**
   * Imposta lo sfondo del pane del logo usando l'immagine del logo dell'istituto (modificata da vari effetti).
   */
  private void setLogo() {
    URL imageUrl = getClass().getResource("/bookrecommenderdev/assets/small-insubriae-logo.png");
    if (imageUrl != null) {
      Image originalImage = new Image(imageUrl.toExternalForm());

      ImageView tempView = new ImageView(originalImage);
      tempView.setOpacity(0.4);

      SnapshotParameters params = new SnapshotParameters();
      params.setFill(Color.TRANSPARENT);
      Image transparentImage = tempView.snapshot(params, null);

      BackgroundImage backgroundImage = new BackgroundImage(
          transparentImage,
          BackgroundRepeat.NO_REPEAT,
          BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(200, 200,
              false, false, true, false
          )
      );

      if (logoPane != null) {
        logoPane.setBackground(new Background(backgroundImage));
      }
    }
  }
}


