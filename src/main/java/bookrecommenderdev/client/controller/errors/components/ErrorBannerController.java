package bookrecommenderdev.client.controller.errors.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Controller JavaFX di un banner di errore riusabile.
 * <p>
 * Mostra un messaggio di errore e, opzionalmente, pulsanti di azione
 * per riprovare un’operazione o tornare indietro.
 */
public class ErrorBannerController {

  @FXML private HBox root;
  @FXML private Label messageLabel;
  @FXML private Button retryButton;
  @FXML private Button backButton;

  /**
   * Mostra il banner con un semplice messaggio, senza azioni associate.
   *
   * @param message testo dell’errore da visualizzare
   */
  public void showError(String message) {
    show(message, null, null);
  }

  /**
   * Mostra il banner di errore configurando opzionalmente le azioni disponibili.
   * <p>
   * I pulsanti vengono mostrati o nascosti dinamicamente in base
   * alla presenza delle azioni passate come parametro.
   *
   * @param message testo dell’errore
   * @param onRetry azione da eseguire alla pressione di “retry” (può essere {@code null})
   * @param onBack  azione da eseguire alla pressione di “back” (può essere {@code null})
   */
  public void show(String message, Runnable onRetry, Runnable onBack) {
    messageLabel.setText(message);

    if (onRetry != null) {
      retryButton.setVisible(true);
      retryButton.setManaged(true);
      retryButton.setOnAction(_ -> onRetry.run());
    } else {
      retryButton.setVisible(false);
      retryButton.setManaged(false);
      retryButton.setOnAction(null);
    }

    if (onBack != null) {
      backButton.setVisible(true);
      backButton.setManaged(true);
      backButton.setOnAction(_ -> onBack.run());
    } else {
      backButton.setVisible(false);
      backButton.setManaged(false);
      backButton.setOnAction(null);
    }

    root.setVisible(true);
    root.setManaged(true);
  }

  /**
   * Nasconde il banner di errore ed esclude il nodo dal layout.
   */
  public void hide() {
    root.setVisible(false);
    root.setManaged(false);
  }
}