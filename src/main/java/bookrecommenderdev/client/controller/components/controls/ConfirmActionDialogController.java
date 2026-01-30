package bookrecommenderdev.client.controller.components.controls;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Controller JavaFX per un controllo “azione con conferma”.
 * <p>
 * Il componente espone un pulsante principale (azione) che, quando premuto, sostituisce
 * il pulsante con un dialog di conferma (tipicamente “Conferma / Annulla”).
 * Alla conferma o annullamento, il controllo torna allo stato iniziale.
 * <p>
 * Le azioni esterne vengono fornite tramite callback {@link Runnable}.
 */
public class ConfirmActionDialogController {

  @FXML private StackPane root;
  @FXML private Button actionButton;
  @FXML private Parent confirm;
  @FXML private ConfirmDialogController confirmController;
  private Runnable onConfirm = () -> {};
  private Runnable onCancel = () -> {};

  @FXML
  private void initialize() {
    // Stato iniziale: dialog nascosto
    setConfirmVisible(false);

    if (confirmController != null) {
      confirmController.setOnConfirm(() -> {
        onConfirm.run();
        reset();
      });

      confirmController.setOnCancel(() -> {
        onCancel.run();
        reset();
      });
    }
  }

  /**
   * Handler FXML: attiva la modalità “conferma” mostrando il dialog.
   */
  @FXML
  private void onAction() {
    showConfirm();
  }

  /* Metodi esposti */

  /**
   * Imposta la callback da eseguire quando l’utente conferma l’azione.
   *
   * @param onConfirm callback (se {@code null} viene usato un no-op)
   */
  public void setOnConfirm(Runnable onConfirm) {
    this.onConfirm = (onConfirm == null) ? () -> {} : onConfirm;
  }

  /**
   * Imposta la callback da eseguire quando l’utente annulla l’azione.
   *
   * @param onCancel callback (se {@code null} viene usato un no-op)
   */
  public void setOnCancel(Runnable onCancel) {
    this.onCancel = (onCancel == null) ? () -> {} : onCancel;
  }

  /**
   * Disabilita/abilita l’intero controllo (pulsante principale e dialog).
   *
   * @param disabled {@code true} per disabilitare, {@code false} per abilitare
   */
  public void setDisabled(boolean disabled) {
    if (actionButton != null) actionButton.setDisable(disabled);
    if (confirm != null) confirm.setDisable(disabled);
  }

  /**
   * Ripristina lo stato iniziale: pulsante azione visibile, dialog nascosto.
   */
  public void reset() {
    root.requestFocus();

    setConfirmVisible(false);
    setActionVisible(true);
  }

  /* Internal */

  /**
   * Mostra il dialog di conferma, nascondendo il pulsante principale e assegnando il focus.
   */
  private void showConfirm() {
    setActionVisible(false);
    setConfirmVisible(true);

    if (confirmController != null) {
      confirmController.requestInitialFocus();
    } else {
      // fallback
      javafx.application.Platform.runLater(confirm::requestFocus);
    }
  }

  /** Mostra/nasconde il pulsante di azione principale. */
  private void setActionVisible(boolean v) {
    actionButton.setVisible(v);
    actionButton.setManaged(v);
  }

  /** Mostra/nasconde il dialog di conferma. */
  private void setConfirmVisible(boolean v) {
    confirm.setVisible(v);
    confirm.setManaged(v);
  }

}