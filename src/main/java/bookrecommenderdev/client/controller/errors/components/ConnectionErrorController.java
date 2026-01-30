package bookrecommenderdev.client.controller.errors.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller JavaFX per la gestione degli errori di connessione di sistema con il server.
 * <p>
 * Mostra una schermata bloccante con messaggio di errore e un pulsante
 * per tentare nuovamente l’inizializzazione dell’applicazione.
 */
public class ConnectionErrorController {

  @FXML private VBox root;
  @FXML private Label titleLabel;
  @FXML private Label subtitleLabel;
  @FXML private Button retryButton;

  /**
   * Imposta l’azione da eseguire quando l’utente preme il pulsante di retry.
   *
   * @param onRetry azione di riprova (può essere {@code null})
   */
  public void setRetryAction(Runnable onRetry) {
    if (retryButton == null) return;

    retryButton.setOnAction(_ -> {
      if (onRetry != null) onRetry.run();
    });
  }

  /**
   * Mostra la schermata di errore di connessione con titolo e sottotitolo.
   *
   * @param title    titolo dell’errore
   * @param subtitle dettaglio o descrizione aggiuntiva
   */
  public void showError(String title, String subtitle) {
    titleLabel.setText(title);
    subtitleLabel.setText(subtitle);

    root.setVisible(true);
    root.setManaged(true);
  }

  /**
   * Nasconde la schermata di errore.
   */
  public void hideError() {
    root.setVisible(false);
    root.setManaged(false);
  }
}
