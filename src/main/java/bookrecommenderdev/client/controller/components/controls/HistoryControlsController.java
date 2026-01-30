package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.client.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller JavaFX per i controlli di navigazione nello storico del {@link Router}.
 * <p>
 * Espone due pulsanti (indietro/avanti) e li abilita o disabilita automaticamente
 * in base alla disponibilità di pagine nello storico.
 */
public class HistoryControlsController {

  @FXML private Button historyBackButton;
  @FXML private Button historyForwardButton;

  /**
   * Inizializza i controlli di navigazione legando lo stato dei pulsanti
   * alla disponibilità di pagine precedenti o successive nello storico del {@link Router}.
   */
  @FXML
  private void initialize() {
    historyBackButton.disableProperty().bind(Router.canBack().not());
    historyForwardButton.disableProperty().bind(Router.canForward().not());
  }

  /**
   * Handler FXML: naviga alla pagina precedente nello storico di navigazione.
   */
  @FXML
  public void onPrevPage() {
    Router.goBack();
  }

  /**
   * Handler FXML: naviga alla pagina successiva nello storico di navigazione.
   */
  @FXML
  public void onNextPage() {
    Router.goForward();
  }
}