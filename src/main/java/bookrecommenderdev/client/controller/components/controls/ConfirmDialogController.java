package bookrecommenderdev.client.controller.components.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ConfirmDialogController {

  @FXML private HBox root;
  @FXML private Button confirmButton;

  private Runnable onConfirm = () -> {};
  private Runnable onCancel = () -> {};

  @FXML
  private void onConfirm() {
    onConfirm.run();
  }

  @FXML
  private void onCancel() {
    onCancel.run();
  }

  @FXML
  private void initialize() {

    // Focus automatico
    root.visibleProperty().addListener((obs, oldV, newV) -> {
      if (newV) {
        javafx.application.Platform.runLater(this::requestInitialFocus);
      }
    });

    // Annulla in automatico alla perdita di focus
    root.focusWithinProperty().addListener((obs, wasFocused, isFocused) -> {
      if (wasFocused && !isFocused) {
        onCancel.run();
      }
    });

    // ESC per annullare
    root.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
        onCancel.run(); e.consume();
      }
    });
  }

  /* Metodi esposti */

  public void setOnConfirm(Runnable onConfirm) {
    this.onConfirm = (onConfirm == null) ? () -> {} : onConfirm;
  }

  public void setOnCancel(Runnable onCancel) {
    this.onCancel = (onCancel == null) ? () -> {} : onCancel;
  }

  public void requestInitialFocus() {
    confirmButton.requestFocus(); // Forza il focus sul nodo
  }

}
