package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.auth.AuthStorage;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.route.Route;
import bookrecommenderdev.routing.route.RouteMatch;
import bookrecommenderdev.server.dto.AuthResult;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.*;
import static bookrecommenderdev.utils.InputVerifiers.*;

public class LoginController implements Routable {

  @FXML private Label loginFeedback;
  @FXML private TextField loginUserId;
  @FXML private TextField loginPassword;
  @FXML private Button loginButton;
  @FXML private Button confirmLoginButton;
  @FXML private CheckBox loginRicordaCredenziali;

  private AppContext context;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) { this.context = context; }

  @FXML
  public void initialize() {
    // login fields
    preventMultipleSpacesAndLimit(loginUserId, MAX_REVIEW_LENGTH);
    preventMultipleSpacesAndLimit(loginPassword, MAX_PASSWORD_LENGTH);
  }


  /**
   * todo documentation
   * */
  @FXML
  protected void onLogin() {

    String userId = loginUserId.getText();
    String password = loginPassword.getText();

    // validazione + feedback
    if (!validateLoginInput(userId, password))
      return;

    try {
      AuthResult res = context.server().login(userId, password);
      handleLoginResult(res, userId, password);

    } catch(RemoteException e) {
      setLoginFeedback("Errore nella connessione al server");
    }

  }

  /**
   * Gestisce il comportamento dell'applicazione a seconda dell'esito della
   * richiesta di autenticazione.
   * @param res Risposta dal server.
   */
  private void handleLoginResult(AuthResult res, String userId, String password) {

    switch(res.authStatus()) {
      case SUCCESS -> {
        AuthContext.login(res.user());

        if (loginRicordaCredenziali.isSelected())
          AuthStorage.save(userId, password);

        setLoginFeedback("Login avvenuto con successo");

        Router.go("/");
      }
      case NO_SUCH_USER -> setLoginFeedback("Credenziali errate");
      case DB_ERROR -> setLoginFeedback("Errore nel reperimento dei dati");
      case UNKNOWN -> setLoginFeedback("Response parsing error");
    }
  }

  /**
   * Valuta il rispetto delle condizioni poste sui campi di login.
   * Inoltre utilizza la casella di feedback per mostrare sulla UI eventuali violazioni delle condizioni.
   * @param userId Email dell'utente.
   * @param password Password dell'utente.
   * @return {@code true} se il controllo è superato, {@code false} altrimenti.
   */
  private boolean validateLoginInput(String userId, String password) {
    if (!verifyPassword(userId)) {
      setLoginFeedback("User id deve essere tra 8 e 64 caratteri");
      return false;
    }
    if (!verifyPassword(password)) {
      setLoginFeedback("Password deve essere tra 8 e 64 caratteri");
      return false;
    }
    return true;
  }

  private void setLoginFeedback(String message) {
    loginFeedback.setText(message);
  }

}
