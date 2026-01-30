package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.exceptions.AlreadyExistsException;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.auth.AuthStorage;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.route.Routable;
import bookrecommenderdev.model.base.Utente;
import bookrecommenderdev.model.dto.TokenSessione;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.*;
import static bookrecommenderdev.model.utils.InputVerifiers.*;
import static bookrecommenderdev.model.utils.InputVerifiers.verCodiceFiscale;
import static bookrecommenderdev.model.utils.InputVerifiers.verifyEmail;
import static bookrecommenderdev.model.utils.InputVerifiers.verifyName;
import static bookrecommenderdev.model.utils.InputVerifiers.verifyPassword;

/**
 * Controller JavaFX della schermata di registrazione utente.
 *
 * <p>Valida i campi, invia la richiesta di registrazione al server e,
 * in caso di successo, effettua automaticamente il login salvando
 * opzionalmente il token di sessione.
 */
public class RegistrationController implements Routable {

  @FXML private Label registerFeedback;
  @FXML private TextField registerName;
  @FXML private TextField registerSurname;
  @FXML private TextField registerUserId;
  @FXML private TextField registerEmail;
  @FXML private TextField registerPassword;
  @FXML private TextField registerCodiceFiscale;

  @FXML private CheckBox saveCredentialsCheck;

  private AppContext context;

  /**
   * Memorizza il contesto dell'applicazione.
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) { this.context = context; }

  /**
   * Applica vincoli di input (lunghezze, spazi multipli e formato codice fiscale).
   */
  @FXML
  public void initialize() {
    // registration fields
    preventMultipleSpacesAndLimit(registerName, MAX_NAME_LENGTH);
    preventMultipleSpacesAndLimit(registerSurname, MAX_NAME_LENGTH);
    preventMultipleSpacesAndLimit(registerUserId, MAX_NAME_LENGTH);
    preventMultipleSpacesAndLimit(registerEmail, MAX_EMAIL_LENGTH);
    ensureLimit(registerPassword, MAX_PASSWORD_LENGTH);
    restrictLooseFiscalCodeInput(registerCodiceFiscale);
  }

  /**
   * Gestisce l’azione di registrazione.
   *
   * <p>Operazioni:
   * <ol>
   *   <li>Valida i campi</li>
   *   <li>Crea l’istanza {@link Utente} e invia la richiesta al server</li>
   *   <li>In caso di successo effettua anche un login automatico</li>
   *   <li>Salva (opzionalmente) il token tramite {@link AuthStorage}</li>
   *   <li>Reindirizza alla home</li>
   * </ol>
   *
   * <p>In caso di errore aggiorna la label di feedback con un messaggio coerente con l’eccezione.
   */
  @FXML
  protected void onRegister() {
    if (context == null) return;

    String name = registerName.getText();
    String surname = registerSurname.getText();
    String userId = registerUserId.getText();
    String email = registerEmail.getText();
    String password = registerPassword.getText();
    String codiceFiscale = registerCodiceFiscale.getText();

    if (!validateRegistrationInput(name, surname, userId, email, password, codiceFiscale)) {
      return;
    }

    try {
      Utente u = new Utente(
          name.trim(),
          surname.trim(),
          email.trim(),
          codiceFiscale.trim(),
          password,
          userId.trim()
      );

      context.server().registrazione(u);

      TokenSessione token = context.server().loginWithToken(userId, password);
      AuthContext.login(token.user());

      if (saveCredentialsCheck.isSelected()) AuthStorage.save(token.token());
      else AuthStorage.clear();

      setRegistrationFeedback("Registrazione avvenuta con successo");
      Router.go("/");

    } catch (AlreadyExistsException e) {
      setRegistrationFeedback(e.getMessage() != null ? e.getMessage() : "Utente già registrato.");

    } catch (DataAccessException e) {
      setRegistrationFeedback(e.getMessage() != null ? e.getMessage() : "Errore nel reperimento dei dati");

    } catch (RemoteException e) {
      setRegistrationFeedback("Errore nella connessione al server");
    }
  }



  /**
   * Valuta il rispetto delle condizioni poste sui campi di login. Inoltre
   * utilizza la casella di feedback per mostrare sulla UI eventuali violazioni delle condizioni.
   * @param name Nome dell'utente.
   * @param surname Cognome dell'utente.
   * @param email Email dell'utente.
   * @param password Password scelta.
   * @param codiceFiscale Codice fiscale personale.
   * @return {@code true} se il controllo è superato, {@code false} altrimenti.
   */
  private boolean validateRegistrationInput(String name, String surname, String userId, String email, String password, String codiceFiscale) {
    if (!verifyName(name)) {
      setRegistrationFeedback("Nome deve essere tra 1 e " + MAX_NAME_LENGTH + " caratteri");
      return false;
    }

    if (!verifyName(surname)) {
      setRegistrationFeedback("Cognome deve essere tra 1 e " + MAX_NAME_LENGTH + " caratteri");
      return false;
    }

    if (!verifyUserId(userId)) {
      setRegistrationFeedback("UserId deve essere tra " + MIN_USERID_LENGTH + " e " + MAX_USERID_LENGTH + " caratteri");
      return false;
    }

    if (!verifyEmail(email)) {
      setRegistrationFeedback("Email deve essere tra 1 e " + MAX_EMAIL_LENGTH + " caratteri");
      return false;
    }

    if (!verifyPassword(password)) {
      setRegistrationFeedback("Password deve essere tra " + MIN_PASSWORD_LENGTH + " e " + MAX_PASSWORD_LENGTH + " caratteri");
      return false;
    }

    if (!verCodiceFiscale(codiceFiscale)) {
      setRegistrationFeedback("Sintassi codice fiscale errata");
      return false;
    }

    return true;
  }

  private void setRegistrationFeedback(String message) {
    registerFeedback.setText(message);
  }
}
