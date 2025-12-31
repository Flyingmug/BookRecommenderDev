package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.InvalidCredentialsException;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.auth.AuthStorage;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.server.dto.TokenSessione;
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
  @FXML private Button confirmLoginButton;
  @FXML private CheckBox loginRicordaCredenziali;

  private AppContext context;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) { this.context = context; }

  @FXML
  public void initialize() {
    // login fields
    preventMultipleSpacesAndLimit(loginUserId, MAX_REVIEW_LENGTH);
    ensureLimit(loginPassword, MAX_PASSWORD_LENGTH);
  }


  /**
   * todo documentation
   * */
  @FXML
  protected void onLogin() {

    if (context == null) return;

    String userId = loginUserId.getText();
    String password = loginPassword.getText();

    if (!validateLoginInput(userId, password)) return;

    // optional UX: disable button while request
    confirmLoginButton.setDisable(true);

    try {
      TokenSessione res = context.server().loginWithToken(userId, password);
      AuthContext.login(res.user());

      if (loginRicordaCredenziali.isSelected()) AuthStorage.save(res.token());
      else AuthStorage.clear();

      setLoginFeedback("Login avvenuto con successo");
      Router.go("/");

    } catch (InvalidCredentialsException e) {
      setLoginFeedback("Credenziali errate");

    } catch (DataAccessException e) {
      setLoginFeedback(e.getMessage() != null ? e.getMessage() : "Errore nel reperimento dei dati");

    } catch (RemoteException e) {
      setLoginFeedback("Errore nella connessione al server");

    } finally {
      confirmLoginButton.setDisable(false);
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
    if (!verifyUserId(userId)) {
      setLoginFeedback("User id deve essere tra " + MIN_USERID_LENGTH + " e " + MAX_USERID_LENGTH + " caratteri");
      return false;
    }
    if (!verifyPassword(password)) {
      setLoginFeedback("Password deve essere tra " + MIN_PASSWORD_LENGTH + " e " + MAX_PASSWORD_LENGTH + " caratteri");
      return false;
    }
    return true;
  }

  private void setLoginFeedback(String message) {
    loginFeedback.setText(message);
  }

}
