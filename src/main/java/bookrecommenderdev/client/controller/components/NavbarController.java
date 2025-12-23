package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.auth.AuthContext;
import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import static bookrecommenderdev.utils.Tools.setRandomBackgroundColor;

public class NavbarController {

  @FXML private Button historyBackButton;
  @FXML private Button historyForwardButton;

  @FXML private StackPane searchbar;
  @FXML private TextField searchInput;

  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private Button librariesButton;
  @FXML private Button profileButton;
  @FXML private Label profileText;

  @FXML
  public void initialize() {
    setRandomBackgroundColor(profileButton);

    historyBackButton.disableProperty().bind(
        Router.canBack().not()
    );
    historyForwardButton.disableProperty().bind(
        Router.canForward().not()
    );

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

  public void setSearchbarVisible(Boolean v) { searchbar.setVisible(v); }
  public void setProfileInitials(String s) { profileText.setText(s.substring(0,2)); }

  @FXML public void onRegister() { Router.go("/registration"); }

  @FXML public void onHomepage() { Router.go("/"); }

  @FXML public void onLogin() { Router.go("/login"); }

  @FXML public void onLibraries() {  }

  @FXML public void onProfile() { Router.go("/profile"); }

  @FXML public void onPrevPage() {
    Router.goBack();
  }

  @FXML public void onNextPage() {
    Router.goForward();
  }

  @FXML public void onSearchAction() {
    String input = searchInput.getText();
    Router.go("/search/:" + input); // Parametro passato nel percorso
  }

  @FXML void testMethod() { System.out.println("TEST: Click detected"); }

}
