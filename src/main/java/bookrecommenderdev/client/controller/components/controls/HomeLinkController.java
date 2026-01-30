package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.client.routing.Router;
import javafx.fxml.FXML;

/**
 * Controller JavaFX per il collegamento rapido alla homepage dell’applicazione.
 * <p>
 * Espone un’azione che reindirizza l’utente alla rotta principale ("/").
 */
public class HomeLinkController {

  /**
   * Handler FXML: naviga alla homepage dell’applicazione.
   */
  @FXML
  public void onHomepage() {
    Router.go("/");
  }
}