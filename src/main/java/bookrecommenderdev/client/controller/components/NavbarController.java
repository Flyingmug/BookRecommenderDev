package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class NavbarController {

  @FXML private VBox searchbarContainer;
  @FXML private SearchbarController searchbarController;

  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private Button librariesButton;
  @FXML private Parent profileButton;

  public void setSearchbarVisible(boolean visible) {
    searchbarContainer.setVisible(visible);
    searchbarContainer.setManaged(visible);
  }

  @FXML
  public void initialize() {
    loginButton.visibleProperty().bind(
        AuthContext.userProperty().isNull()
    );
    loginButton.managedProperty().bind(
        AuthContext.userProperty().isNull()
    );
    loginButton.disableProperty().bind(
        AuthContext.userProperty().isNotNull()
    );

    registerButton.visibleProperty().bind(
        AuthContext.userProperty().isNull()
    );
    registerButton.managedProperty().bind(
        AuthContext.userProperty().isNull()
    );
    registerButton.disableProperty().bind(
        AuthContext.userProperty().isNotNull()
    );

    librariesButton.visibleProperty().bind(
        AuthContext.userProperty().isNotNull()
    );
    librariesButton.managedProperty().bind(
        AuthContext.userProperty().isNotNull()
    );
    librariesButton.disableProperty().bind(
        AuthContext.userProperty().isNull()
    );

    profileButton.visibleProperty().bind(
        AuthContext.userProperty().isNotNull()
    );
    profileButton.managedProperty().bind(
        AuthContext.userProperty().isNotNull()
    );
    profileButton.disableProperty().bind(
        AuthContext.userProperty().isNull()
    );

    searchbarController.setOnSearch(req -> Router.go("/search", req));
  }

  @FXML public void onRegister() { Router.go("/registration"); }

  @FXML public void onLogin() { Router.go("/login"); }

  @FXML public void onLibraries() { Router.go("/libraries"); }

}
