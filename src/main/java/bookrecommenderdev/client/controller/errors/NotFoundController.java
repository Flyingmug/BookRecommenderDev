package bookrecommenderdev.client.controller.errors;

import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

/**
 * Controller JavaFX della pagina di errore "Not Found".
 *
 * <p>Consente di impostare un messaggio personalizzato, passato tramite stato di navigazione.
 */
public class NotFoundController implements Routable {

  @FXML Label title;

  /**
   * Imposta il messaggio visualizzato sulla pagina.
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    try {
      String message = (String) state;

      if (message != null && !message.isBlank()) {
        title.setText(message);
      }

    } catch (Exception _) {}
  }
}
