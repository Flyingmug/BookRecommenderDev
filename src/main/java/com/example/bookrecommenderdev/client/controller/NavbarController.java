package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import static com.example.bookrecommenderdev.utils.Tools.setRandomBackgroundColor;

public class NavbarController {

  @FXML private StackPane searchbar;
  @FXML private TextField searchInput;

  @FXML public Button loginButton;
  @FXML public Button registerButton;
  @FXML private Button librariesButton;
  @FXML private Button profileButton;

  @FXML
  public void initialize() {
    setRandomBackgroundColor(profileButton);
  }

  public void setSearchbarVisible(Boolean v) { searchbar.setVisible(v); }
  public void showLoginButton(Boolean v) { loginButton.setManaged(v);  }
  public void showRegisterButton(Boolean v) { registerButton.setManaged(v); }
  public void showLibrariesButton(Boolean v) { librariesButton.setManaged(v);  }
  public void showProfilePicture(Boolean v) { profileButton.setManaged(v); }

  @FXML public void onRegister() {
    Router.go("/registration");
  }

  @FXML public void onHomepage() {
    Router.go("/");
  }

  @FXML public void onLogin() {
    Router.go("/login");
  }

  @FXML public void onLibraries() {  }

  @FXML public void onProfile() { Router.go("/profile");}

  @FXML public void onSearchAction() {
    String input = searchInput.getText();
    Router.go("/");
    System.err.println("Not Implemented.");
  }
}
