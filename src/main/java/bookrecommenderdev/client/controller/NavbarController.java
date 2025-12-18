package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.Router;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import static bookrecommenderdev.utils.Tools.setRandomBackgroundColor;

public class NavbarController {

  @FXML private HBox historyControls;
  @FXML private Button historyBackButton;
  @FXML private Button historyForwardButton;
  @FXML private FontIcon historyBackIcon;
  @FXML private FontIcon historyForwardIcon;

  @FXML private StackPane searchbar;
  @FXML private TextField searchInput;

  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private Button librariesButton;
  @FXML private Button profileButton;
  @FXML private Label profileText;

  @FXML private Button prevPage;
  @FXML private Button nextPage;

  @FXML
  public void initialize() {
    setRandomBackgroundColor(profileButton);

    historyBackButton.disableProperty().bind(
        Router.canBack().not()
    );
    historyForwardButton.disableProperty().bind(
        Router.canForward().not()
    );

    historyBackIcon.fillProperty().bind(
        Bindings.when(Router.canBack())
            .then(Color.valueOf("#272727"))
            .otherwise(Color.valueOf("#c8c8c8"))
    );
    historyForwardIcon.fillProperty().bind(
        Bindings.when(Router.canForward())
            .then(Color.valueOf("#272727"))
            .otherwise(Color.valueOf("#c8c8c8"))
    );

  }

  public void setSearchbarVisible(Boolean v) { searchbar.setVisible(v); }
  public void showLoginButton(Boolean v) { loginButton.setManaged(v); }
  public void showRegisterButton(Boolean v) { registerButton.setManaged(v); }
  public void showLibrariesButton(Boolean v) { librariesButton.setManaged(v);  }
  public void showProfilePicture(Boolean v) { profileButton.setManaged(v); }
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
