package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static bookrecommenderdev.utils.Tools.setRandomBackgroundColor;

public class NavbarController {


  @FXML private VBox searchbar;

  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private Button librariesButton;
  @FXML private Button profileButton;
  @FXML private Label profileText;

  public void setSearchbarVisible(boolean visible) {
    searchbar.setVisible(visible);
    searchbar.setVisible(visible);
  }

  @FXML
  public void initialize() {
    setRandomBackgroundColor(profileButton);

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
  }

  // fixme unused
  public void setProfileInitials(String s) { profileText.setText(s.substring(0,2)); }

  @FXML public void onRegister() { Router.go("/registration"); }

  @FXML public void onLogin() { Router.go("/login"); }

  @FXML public void onLibraries() { Router.go("/libraries"); }

  @FXML public void onProfile() { Router.go("/profile"); }

}
