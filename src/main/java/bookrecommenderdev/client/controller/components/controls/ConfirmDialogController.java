package bookrecommenderdev.client.controller.components.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Controller JavaFX per un dialog di conferma compatto.
 * <p>
 * Il dialog espone due azioni:
 * <ul>
 *   <li>Conferma → esegue la callback {@link #onConfirm}</li>
 *   <li>Annulla → esegue la callback {@link #onCancel}</li>
 * </ul>
 *
 * Comportamenti automatici:
 * <ul>
 *   <li>focus iniziale sul pulsante di conferma;</li>
 *   <li>annullamento automatico alla perdita di focus;</li>
 *   <li>tasto ESC per annullare.</li>
 * </ul>
 *
 * Il controller è pensato per essere usato come componente interno
 * (es. da {@link ConfirmActionDialogController}).
 */
public class ConfirmDialogController {

  @FXML private HBox root;
  @FXML private Button confirmButton;
  private Runnable onConfirm = () -> {};
  private Runnable onCancel = () -> {};

  @FXML
  private void initialize() {

    // Focus automatico quando il dialog diventa visibile
    root.visibleProperty().addListener((obs, oldV, newV) -> {
      if (newV) {
        javafx.application.Platform.runLater(this::requestInitialFocus);
      }
    });

    // Annulla automaticamente alla perdita di focus
    root.focusWithinProperty().addListener((obs, wasFocused, isFocused) -> {
      if (wasFocused && !isFocused) {
        onCancel.run();
      }
    });

    // Tasto ESC per annullare
    root.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
        onCancel.run();
        e.consume();
      }
    });
  }

  /** Handler FXML: conferma l’azione. */
  @FXML
  private void onConfirm() {
    onConfirm.run();
  }

  /** Handler FXML: annulla l’azione. */
  @FXML
  private void onCancel() {
    onCancel.run();
  }

  /* Metodi esposti */

  /**
   * Imposta la callback di conferma.
   *
   * @param onConfirm callback (se {@code null} viene usato un no-op)
   */
  public void setOnConfirm(Runnable onConfirm) {
    this.onConfirm = (onConfirm == null) ? () -> {} : onConfirm;
  }

  /**
   * Imposta la callback di annullamento.
   *
   * @param onCancel callback (se {@code null} viene usato un no-op)
   */
  public void setOnCancel(Runnable onCancel) {
    this.onCancel = (onCancel == null) ? () -> {} : onCancel;
  }

  /**
   * Forza il focus iniziale sul pulsante di conferma.
   */
  public void requestInitialFocus() {
    confirmButton.requestFocus();
  }
}