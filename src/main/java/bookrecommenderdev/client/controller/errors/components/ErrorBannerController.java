package bookrecommenderdev.client.controller.errors.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ErrorBannerController {

  @FXML private HBox root;
  @FXML private Label messageLabel;
  @FXML private Button retryButton;
  @FXML private Button backButton;

  public void showError(String message) {
    show(message, null, null);
  }

  public void show(String message, Runnable onRetry, Runnable onBack) {
    messageLabel.setText(message);

    if (onRetry != null) {
      retryButton.setVisible(true);
      retryButton.setManaged(true);
      retryButton.setOnAction(e -> onRetry.run());
    } else {
      retryButton.setVisible(false);
      retryButton.setManaged(false);
      retryButton.setOnAction(null);
    }

    if (onBack != null) {
      backButton.setVisible(true);
      backButton.setManaged(true);
      backButton.setOnAction(e -> onBack.run());
    } else {
      backButton.setVisible(false);
      backButton.setManaged(false);
      backButton.setOnAction(null);
    }

    root.setVisible(true);
    root.setManaged(true);
  }

  public void hide() {
    root.setVisible(false);
    root.setManaged(false);
  }
}