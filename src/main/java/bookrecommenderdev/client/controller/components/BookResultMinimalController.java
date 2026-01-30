package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.base.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

/**
 * Controller JavaFX per una rappresentazione minimale di un libro.
 * <p>
 * Mostra esclusivamente il titolo del libro e un’azione opzionale
 * (tipicamente usata per rimozione/selezione in liste compatte).
 */
public class BookResultMinimalController {

  @FXML private Label title;
  @FXML private Button actionButton;
  @FXML private FontIcon actionIcon;
  private Libro libro;
  private Consumer<Integer> onAction = null;

  /**
   * Imposta il libro mostrato dal componente.
   *
   * @param l libro da visualizzare
   */
  public void setBook(Libro l) {
    this.title.setText(l.getTitolo());
    this.libro = l;
  }

  /**
   * Abilita e configura il pulsante di azione opzionale.
   *
   * @param iconLiteral literal dell’icona (Ikonli); se {@code null} viene usata un’icona di default
   * @param onAction    callback invocata con l’id del libro
   */
  public void enableActionButton(String iconLiteral, Consumer<Integer> onAction) {
    this.onAction = onAction;

    if (actionIcon != null) {
      actionIcon.setIconLiteral(
          iconLiteral == null ? "mdi2p-plus-box-outline" : iconLiteral
      );
      actionIcon.setVisible(iconLiteral != null && !iconLiteral.isBlank());
    }

    actionButton.setVisible(true);
    actionButton.setManaged(true);
    actionButton.setDisable(false);
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