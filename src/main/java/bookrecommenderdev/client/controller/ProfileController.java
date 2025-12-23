package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.auth.AuthContext;
import bookrecommenderdev.model.auth.AuthStorage;
import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ProfileController {

  @FXML private Button logoutButton;  // might be unnecessary, only thing needed is the onAction
  @FXML private VBox profilePage;

  @FXML
  public void initialize() {

    logoutButton.visibleProperty().bind(
        AuthContext.userProperty().isNotNull()
    );
    logoutButton.disableProperty().bind(
        AuthContext.userProperty().isNull()
    );
  }

  @FXML
  protected void onLogout() {
    AuthContext.logout();
    AuthStorage.clear();
    Router.go("/");
  }
}
