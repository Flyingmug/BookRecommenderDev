package bookrecommenderdev.client.routing.layout;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;



/**
 * Controller logico di un layout dell'applicazione.
 *
 * <p>Un {@code LayoutController} è responsabile della gestione di pagine.</p>
 *
 * <p>Il router interagisce con il controller del layout per effettuare
 * il cambio di pagina.</p>
 */
public interface LayoutController {

  /**
   * Ottiene il riferimento alla pagina contenuta.
   * @return riferimento
   */
  Pane getContent();

  /**
   * Inserisce il contenuto come pagina corrente.
   * @param page pagina ottenuta
   */
  void setContent(Parent page);
}
