package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.routing.AppContext;
import com.example.bookrecommenderdev.routing.Routable;
import com.example.bookrecommenderdev.routing.Router;
import com.example.bookrecommenderdev.model.AuthStatus;
import com.example.bookrecommenderdev.model.Utente;
import com.example.bookrecommenderdev.utils.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.util.Map;

import static com.example.bookrecommenderdev.Constants.LOCAL_CREDENTIALS;
import static com.example.bookrecommenderdev.utils.InputVerifiers.*;

public class LoginController implements Routable {

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

  private AppContext context;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) { this.context = context; }

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
  protected void onLogin() {

    String email = loginEmail.getText();
    String password = loginPassword.getText();

    // validazione + feedback
    if (!validateLoginInput(email, password)) {
      return;
    }

    try {
      Pair<Utente, AuthStatus> res = context.server().login(email, password);
      handleLoginResult(res);

    } catch(RemoteException e) {
      setLoginFeedback("Errore nella connessione al server");
    }

  }

  /**
   * Gestisce il comportamento dell'applicazione a seconda dell'esito della
   * richiesta di autenticazione.
   * @param res Risposta dal server
   */
  private void handleLoginResult(Pair<Utente, AuthStatus> res) {
    switch(res.getValue()) {
      case SUCCESS:
        context.setUser(res.getKey());

        if (loginRicordaCredenziali.isSelected())
          saveCredentials();

        setLoginFeedback("Login avvenuto con successo");
        setUserAccessed();
        Router.go("/");
        break;
      case NO_SUCH_USER:
        setLoginFeedback("Credenziali errate");
        break;
      case DB_ERROR:
        setLoginFeedback("Errore nel reperimento dei dati");
        break;
      default:
        setLoginFeedback("Response parsing error");
        break;
    }
  }

  /**
   * Valuta il rispetto delle condizioni poste sui campi di login.
   * Inoltre utilizza la casella di feedback per mostrare sulla UI eventuali violazioni delle condizioni.
   * @param email Email dell'utente.
   * @param password Password dell'utente.
   * @return Valore booleano indicante il superamento del controllo sugli attributi.
   */
  private boolean validateLoginInput(String email, String password) {
    if (!verifyEmail(email)) {
      setLoginFeedback("Nome utente deve essere tra 1 e 64 caratteri");
      return false;
    }
    if (!verifyPassword(password)) {
      setLoginFeedback("Password deve essere tra 8 e 64 caratteri");
      return false;
    }
    return true;
  }

  /**
   * Utilizza la classe utilitaria {@link FileManager} per salvare la coppia (email, password) su un file locale.
   * Il nome del file è definito in {@link com.example.bookrecommenderdev.Constants}
    */
  private void saveCredentials() {
    FileManager.write(LOCAL_CREDENTIALS, context.user().getEmail() + "," + context.user().getPassword());
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
