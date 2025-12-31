package bookrecommenderdev.client.controller.components.controls;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class ConfirmActionDialogController {

  @FXML private StackPane root;
  @FXML private Button actionButton;
  @FXML private Parent confirm;
  @FXML private ConfirmDialogController confirmController;

  private Runnable onConfirm = () -> {};
  private Runnable onCancel = () -> {};

  @FXML
  private void initialize() {
    // Start with confirm hidden
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

  @FXML
  private void onAction() {
    showConfirm();
  }

  /* Metodi esposti */

  public void setOnConfirm(Runnable onConfirm) {
    this.onConfirm = (onConfirm == null) ? () -> {} : onConfirm;
  }

  public void setOnCancel(Runnable onCancel) {
    this.onCancel = (onCancel == null) ? () -> {} : onCancel;
  }

  /** Disable/enable the whole control (both button + dialog buttons). */
  public void setDisabled(boolean disabled) {
    if (actionButton != null) actionButton.setDisable(disabled);
    if (confirm != null) confirm.setDisable(disabled);
  }

  /** Return to default state: action button visible, confirm hidden. */
  public void reset() {
    root.requestFocus();

    setConfirmVisible(false);
    setActionVisible(true);
  }

  /** Optional: expose the action button for changing tooltip/icon/text/styles */
  public Button getActionButton() {
    return actionButton;
  }

  /* Internal */

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

  private void setActionVisible(boolean v) {
    actionButton.setVisible(v);
    actionButton.setManaged(v);
  }

  private void setConfirmVisible(boolean v) {
    confirm.setVisible(v);
    confirm.setManaged(v);
  }

}
