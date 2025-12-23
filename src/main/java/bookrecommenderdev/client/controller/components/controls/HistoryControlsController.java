package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HistoryControlsController {

  @FXML private Button historyBackButton;
  @FXML private Button historyForwardButton;

  @FXML
  private void initialize() {

    historyBackButton.disableProperty().bind(
        Router.canBack().not()
    );
    historyForwardButton.disableProperty().bind(
        Router.canForward().not()
    );

  }

  @FXML public void onPrevPage() {
    Router.goBack();
  }

  @FXML public void onNextPage() {
    Router.goForward();
  }
}
