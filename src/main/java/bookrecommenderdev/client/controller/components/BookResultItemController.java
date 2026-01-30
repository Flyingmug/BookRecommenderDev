package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.base.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

/**
 * Controller JavaFX per un singolo elemento “risultato libro”.
 * <p>
 * Mostra titolo/autore/anno e consente:
 * <ul>
 *   <li>apertura della pagina libro tramite {@link #linkButton};</li>
 *   <li>un’azione opzionale configurabile (es. “Aggiungi”, “Rimuovi”, “Seleziona”).</li>
 * </ul>
 */
public class BookResultItemController {

  @FXML private Button linkButton;
  @FXML private Label titleLabel;
  @FXML private Label authorLabel;
  @FXML private Label yearLabel;
  @FXML private Button actionButton;
  @FXML private FontIcon actionIcon;

  private Libro libro;
  private Consumer<Integer> onOpen = _ -> {};
  private Consumer<Integer> onAction = null;


  /**
   * Imposta il libro mostrato dall’item e l’azione di apertura.
   * <p>
   * Popola i campi testuali e configura il click del link.
   * L’azione opzionale viene nascosta per default.
   *
   * @param l            libro da mostrare
   * @param onClickOpen  callback invocata con l’id del libro (può essere {@code null})
   */
  public void setBook(Libro l, Consumer<Integer> onClickOpen) {
    this.libro = l;
    this.onOpen = (onClickOpen == null) ? (_ -> {}) : onClickOpen;

    titleLabel.setText(l.getTitolo());

    authorLabel.setText(l.getAutori());

    yearLabel.setText(l.getAnnoPubblicazione() > 0 ?
        Integer.toString(l.getAnnoPubblicazione()) : "");

    linkButton.setOnAction(_ ->  this.onOpen.accept(l.getIdLibro()));

    hideActionButton();
  }

  /**
   * Abilita e configura il pulsante di azione opzionale.
   *
   * @param text        testo del pulsante (se {@code null} viene impostato a stringa vuota)
   * @param iconLiteral literal dell’icona (Ikonli), opzionale
   * @param onAction    callback invocata con l’id del libro (può essere {@code null})
   */
  public void enableActionButton(String text, String iconLiteral, Consumer<Integer> onAction) {
    this.onAction = onAction;

    actionButton.setText(text == null ? "" : text);
    if (actionIcon != null) {
      actionIcon.setIconLiteral(iconLiteral == null ? "" : iconLiteral);
      actionIcon.setVisible(iconLiteral != null && !iconLiteral.isBlank());
    }

    actionButton.setVisible(true);
    actionButton.setManaged(true);
    actionButton.setDisable(false);
  }


  /**
   * Nasconde il pulsante di azione ed elimina la callback associata.
   */
  public void hideActionButton() {
    actionButton.setVisible(false);
    actionButton.setManaged(false);
    this.onAction = null;
  }

  /**
   * Handler FXML invocato al click del pulsante di azione.
   * Esegue la callback solo se libro e azione sono stati configurati.
   */
  @FXML
  private void onActionClicked() {
    if (libro == null || onAction == null) return;
    onAction.accept(libro.getIdLibro());
  }

}
