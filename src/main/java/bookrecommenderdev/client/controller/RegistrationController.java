package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.model.Utente;
import bookrecommenderdev.utils.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.LOCAL_CREDENTIALS;
import static bookrecommenderdev.utils.InputVerifiers.*;
import static bookrecommenderdev.utils.InputVerifiers.verCodiceFiscale;
import static bookrecommenderdev.utils.InputVerifiers.verifyEmail;
import static bookrecommenderdev.utils.InputVerifiers.verifyName;
import static bookrecommenderdev.utils.InputVerifiers.verifyPassword;

public class RegistrationController implements Routable {


  // registrazione
  @FXML
  private VBox registerPage;
  @FXML
  private Label registerFeedback;
  @FXML
  private TextField registerName;
  @FXML
  private TextField registerSurname;
  @FXML
  private TextField registerEmail;
  @FXML
  private TextField registerPassword;
  @FXML
  private TextField registerCodiceFiscale;
  @FXML
  private Button registerButton;
//  @FXML
//  private Button confirmRegistrationButton;
  @FXML
  private CheckBox saveCredentialsCheck;

  private AppContext context;

  @FXML
  public void initialize() {
// registration fields
    preventMultipleSpacesAndLimit(registerName, 64);
    preventMultipleSpacesAndLimit(registerSurname, 64);
    preventMultipleSpacesAndLimit(registerEmail, 255);
    preventMultipleSpacesAndLimit(registerPassword, 64);
    restrictLooseFiscalCodeInput(registerCodiceFiscale);
  }


  @Override
  public void onRoute(Map<String, String> params, AppContext context) { this.context = context; }



  private void resetRegisterPage() {
    registerFeedback.setText("");
    registerName.setText("");
    registerName.setText("");
    registerSurname.setText("");
    registerEmail.setText("");
    registerPassword.setText("");
    registerCodiceFiscale.setText("");
    saveCredentialsCheck.setSelected(false);
  }

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
      String res = context.server().registrazione(u);
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
  private void handleRegistrationResult(String res, Utente u) {
    switch(res) {
      case "success":
        setRegistrationFeedback("Registrazione avvenuta con successo");
          context.setUser(u);
        if (saveCredentialsCheck.isSelected())
          saveCredentials();
        break;
      case "user-exists":
        setRegistrationFeedback("L'utente specificato esiste");
        break;
      case "insert-error":
        setRegistrationFeedback("Errore nell'inserimento dei dati");
        break;
      case "db-error":
        setRegistrationFeedback("Errore nel reperimento dei dati");
        break;
      default:
        setRegistrationFeedback("Response parsing error");
        break;
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

  private void saveCredentials() {
    FileManager.write(LOCAL_CREDENTIALS, context.user().getEmail() + "," + context.user().getPassword());
  }

  private void setRegistrationFeedback(String message) {
    registerFeedback.setText(message);
  }
}
