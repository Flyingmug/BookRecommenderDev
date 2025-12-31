package bookrecommenderdev.client.controller.errors.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ConnectionErrorController {

  @FXML private VBox root;
  @FXML private Label titleLabel;
  @FXML private Label subtitleLabel;
  @FXML private Button retryButton;

  public void setRetryAction(Runnable onRetry) {
    if (retryButton == null) return;

    retryButton.setOnAction(_ -> {
      if (onRetry != null) onRetry.run();
    });
  }

  public void showError(String title, String subtitle) {
    titleLabel.setText(title);
    subtitleLabel.setText(subtitle);

    root.setVisible(true);
    root.setManaged(true);
  }

  public void hideError() {
    root.setVisible(false);
    root.setManaged(false);
  }
}
