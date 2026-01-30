package bookrecommenderdev.client.controller.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.kordamp.ikonli.javafx.FontIcon;

import static bookrecommenderdev.Constants.MAX_REVIEW_LENGTH;
import static bookrecommenderdev.model.utils.InputVerifiers.preventMultipleSpacesAndLimit;

/**
 * Controller JavaFX per un campo di valutazione di una recensione.
 * <p>
 * Combina un punteggio numerico (1–5) con un commento testuale opzionale,
 * gestendo validazione, limiti di lunghezza e visibilità del testo.
 */
public class RatingFieldController {

  @FXML private Label titleLabel;
  @FXML private ComboBox<Integer> scoreBox;
  @FXML private Button toggleButton;
  @FXML private FontIcon toggleIcon;
  @FXML private TextArea textReviewArea;
  @FXML private Label charCountLabel;

  @FXML
  private void initialize() {
    scoreBox.getItems().addAll(1, 2, 3, 4, 5);

    toggleButton.setOnAction(_ -> toggleComment());

    preventMultipleSpacesAndLimit(textReviewArea, MAX_REVIEW_LENGTH);

    setCharCountListener();
  }

  /**
   * Aggiorna dinamicamente il contatore dei caratteri del commento.
   */
  private void setCharCountListener() {
    textReviewArea.textProperty().addListener((_, _, newValue) -> {
      if (newValue != null) {
        charCountLabel.setText(newValue.length() + " / " + MAX_REVIEW_LENGTH);
      }
    });
  }

  /**
   * Mostra o nasconde l’area di commento testuale.
   * <p>
   * Quando il commento viene nascosto, il contenuto viene azzerato.
   */
  private void toggleComment() {
    boolean show = !textReviewArea.isVisible();
    textReviewArea.setVisible(show);
    textReviewArea.setManaged(show);
    charCountLabel.setVisible(show);
    charCountLabel.setManaged(show);

    if (!show) {
      textReviewArea.setText(null);
    }

    toggleIcon.setIconLiteral(show ? "mdi2m-minus" : "mdi2p-plus");
  }

  /* Metodi esposti */

  /**
   * Imposta il titolo del criterio di valutazione.
   *
   * @param title nome del criterio
   */
  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  /**
   * Restituisce il punteggio selezionato.
   *
   * @return valore tra 1 e 5, oppure {@code null} se non selezionato
   */
  public Integer getScore() {
    return scoreBox.getValue();
  }

  /**
   * Restituisce il commento testuale, senza spazi agli estremi.
   *
   * @return testo del commento, oppure {@code null} se vuoto o composto solo da spazi
   */
  public String getTextReview() {
    if (textReviewArea.getText() == null) return null;
    String t = textReviewArea.getText().trim();
    return t.isBlank() ? null : t;
  }

  /**
   * Imposta la visibilità del selettore di punteggio.
   *
   * @param visible {@code true} per mostrare il punteggio, {@code false} per nasconderlo
   */
  public void setScoreBoxVisible(boolean visible) {
    scoreBox.setVisible(visible);
    scoreBox.setManaged(visible);
  }
}