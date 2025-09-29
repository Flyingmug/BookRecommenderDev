package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.client.BookRecommenderService;
import com.example.bookrecommenderdev.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.awt.print.Book;
import java.rmi.RemoteException;

import static com.example.bookrecommenderdev.utils.InputVerifiers.*;

public class LoginController {

  // login
  @FXML
  private VBox loginPage;
  @FXML
  private Label loginFeedback;
  @FXML
  private TextField loginEmail;
  @FXML
  private TextField loginPassword;
  @FXML
  private Button loginButton;
  @FXML
  private Button confirmLoginButton;
  @FXML
  private CheckBox loginRicordaCredenziali;

  @FXML
  public void initialize() {
// login fields
    preventMultipleSpacesAndLimit(loginEmail, 255);
    preventMultipleSpacesAndLimit(loginPassword, 64);
  }


  private void resetLoginPage() {
    loginFeedback.setText("");
    loginEmail.setText("");
    loginPassword.setText("");
    loginRicordaCredenziali.setSelected(false);
  }

  @FXML
  protected void onConfirmLogin() {

    String email = loginEmail.getText();
    String password = loginPassword.getText();

    if (!verifyEmail(email)) {
      setLoginFeedback("Nome utente deve essere tra 1 e 64 caratteri");
      return;
    }

    if (!verifyPassword(password)) {
      setLoginFeedback("Password deve essere tra 8 e 64 caratteri");
      return;
    }

    try {
      Pair<Utente, String> res = BookRecommenderService.getServer().login(email, password);

      switch(res.getValue()) {
        case "success":

//          currentUser = res.getKey();
//
//          if (loginRicordaCredenziali.isSelected())
//            saveCredentials();

          setLoginFeedback("Login avvenuto con successo");
          setUserAccessed();
//          onHomepage();
          break;
        case "no-such-user":
          setLoginFeedback("Credenziali errate");
          break;
        case "db-error":
          setLoginFeedback("Errore nel reperimento dei dati");
          break;
        default:
          setLoginFeedback("Response parsing error");
          break;
      }

    } catch(RemoteException e) {
      setLoginFeedback("Errore nella connessione al server");
    }

  }

  private void setUserAccessed() {
//    navbarControls.getChildren().removeAll(loginButton, registerButton);
//    navbarControls.getChildren().addFirst(librariesButton);
//    profilePicture.setText(("" + currentUser.getNome().charAt(0) + currentUser.getCognome().charAt(0)).toUpperCase());
//    navbarControls.getChildren().addLast(profileButton);
  }

  private void setLoginFeedback(String message) {
    loginFeedback.setText(message);
  }


}
