package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.auth.AuthStorage;
import bookrecommenderdev.routing.auth.RegisterStatus;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.*;
import static bookrecommenderdev.utils.InputVerifiers.*;
import static bookrecommenderdev.utils.InputVerifiers.verCodiceFiscale;
import static bookrecommenderdev.utils.InputVerifiers.verifyEmail;
import static bookrecommenderdev.utils.InputVerifiers.verifyName;
import static bookrecommenderdev.utils.InputVerifiers.verifyPassword;

public class RegistrationController implements Routable {

  @FXML private VBox registerPage;
  @FXML private Label registerFeedback;
  @FXML private TextField registerName;
  @FXML private TextField registerSurname;
  @FXML private TextField registerEmail;
  @FXML private TextField registerPassword;
  @FXML private TextField registerCodiceFiscale;
  @FXML private Button registerButton;
//  @FXML
//  private Button confirmRegistrationButton;
  @FXML private CheckBox saveCredentialsCheck;

  private AppContext context;


  @Override
  public void onRoute(Map<String, String> params, AppContext context) { this.context = context; }

  @FXML
  public void initialize() {
    // registration fields
    preventMultipleSpacesAndLimit(registerName, MAX_NAME_LENGTH);
    preventMultipleSpacesAndLimit(registerSurname, MAX_NAME_LENGTH);
    preventMultipleSpacesAndLimit(registerEmail, MAX_REVIEW_LENGTH);
    preventMultipleSpacesAndLimit(registerPassword, MAX_PASSWORD_LENGTH);
    restrictLooseFiscalCodeInput(registerCodiceFiscale);
  }


  /**
   * todo documentation
   * */
  @FXML
  protected void onRegister() {
    String name = registerName.getText();
    String surname = registerSurname.getText();
    String email = registerEmail.getText();
    String password = registerPassword.getText();
    String codiceFiscale = registerCodiceFiscale.getText();

    // validazione + feedback
    if (!validateRegistrationInput(name, surname, email, password, codiceFiscale))
      return;

    try {
      Utente u = new Utente(
          name,
          surname,
          email,
          codiceFiscale,
          password
      );
      RegisterStatus res = context.server().registrazione(u);
      handleRegistrationResult(res, u);

    } catch(RemoteException e) {
      setRegistrationFeedback("Errore nella connessione al server");
    }
  }

  /**
   * Gestisce il comportamento dell'applicazione a seconda dell'esito della
   * richiesta di registrazione.
   * @param res Risposta dal server.
   * @param u Utente creato.
   */
  private void handleRegistrationResult(RegisterStatus res, Utente u) {
    switch(res) {
      case SUCCESS -> {
        AuthContext.login(u);

        setRegistrationFeedback("Registrazione avvenuta con successo");

        if (saveCredentialsCheck.isSelected())
          AuthStorage.save(u.getEmail(), u.getPassword());

        Router.go("/");
      }
      case FISCAL_CODE_ALREADY_USED -> setRegistrationFeedback("L'utente specificato esiste");
      case DB_ERROR -> setRegistrationFeedback("Errore nel reperimento dei dati");
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
  private boolean validateRegistrationInput(String name, String surname, String email, String password, String codiceFiscale) {
    if (!verifyName(name)) {
      setRegistrationFeedback("Nome deve essere tra 1 e 64 caratteri");
      return false;
    }

    if (!verifyName(surname)) {
      setRegistrationFeedback("Cognome deve essere tra 1 e 64 caratteri");
      return false;
    }

    if (!verifyEmail(email)) {
      setRegistrationFeedback("Password deve essere tra 1 e 255 caratteri");
      return false;
    }

    if (!verifyPassword(password)) {
      setRegistrationFeedback("Password deve essere tra 8 e 64 caratteri");
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
