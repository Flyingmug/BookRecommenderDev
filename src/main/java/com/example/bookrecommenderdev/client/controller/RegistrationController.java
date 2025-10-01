package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.client.AppContext;
import com.example.bookrecommenderdev.client.Routable;
import com.example.bookrecommenderdev.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.Map;

import static com.example.bookrecommenderdev.utils.InputVerifiers.*;
import static com.example.bookrecommenderdev.utils.InputVerifiers.verCodiceFiscale;
import static com.example.bookrecommenderdev.utils.InputVerifiers.verifyEmail;
import static com.example.bookrecommenderdev.utils.InputVerifiers.verifyName;
import static com.example.bookrecommenderdev.utils.InputVerifiers.verifyPassword;

public class RegistrationController implements Routable {

  final static String LOCAL_CREDENTIALS = "credentials.txt";

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
  @FXML
  private Button confirmRegistrationButton;
  @FXML
  private CheckBox registerRicordaCredenziali;

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
  public void onRoute(Map<String, String> params, AppContext context) {
    this.context = context;
  }

//  private void initVerifyLocalUserCredentials() {
//    String str = FileManager.read(LOCAL_CREDENTIALS);
//    if (str != null && !str.isEmpty()) {
//      String[] split = str.split(",");
//      String email = split[0];
//      String password = split[1];
//      loginEmail.setText(email);
//      loginPassword.setText(password);
//      onConfirmLogin();
//    }
//  }

  private void resetRegisterPage() {
    registerFeedback.setText("");
    registerName.setText("");
    registerName.setText("");
    registerSurname.setText("");
    registerEmail.setText("");
    registerPassword.setText("");
    registerCodiceFiscale.setText("");
    registerRicordaCredenziali.setSelected(false);
  }

  @FXML
  protected void onConfirmRegistration() {
    String name = registerName.getText();
    String surname = registerSurname.getText();
    String email = registerEmail.getText();
    String password = registerPassword.getText();
    String codiceFiscale = registerCodiceFiscale.getText();

    if (!verifyName(name)) {
      setRegistrationFeedback("Nome deve essere tra 1 e 64 caratteri");
      return;
    }

    if (!verifyName(surname)) {
      setRegistrationFeedback("Cognome deve essere tra 1 e 64 caratteri");
      return;
    }

    if (!verifyEmail(email)) {
      setRegistrationFeedback("Password deve essere tra 1 e 255 caratteri");
      return;
    }

    if (!verifyPassword(password)) {
      setRegistrationFeedback("Password deve essere tra 8 e 64 caratteri");
      return;
    }

    if (!verCodiceFiscale(codiceFiscale)) {
      setRegistrationFeedback("Sintassi codice fiscale errata");
      return;
    }


    try {
      Utente u = new Utente(
          name,
          surname,
          email,
          codiceFiscale,
          password
      );
      String res = context.server().registrazione(u);

      switch(res) {
        case "success":
          setRegistrationFeedback("Registrazione avvenuta con successo");
//          currentUser = u;
          if (registerRicordaCredenziali.isSelected())
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

    } catch(RemoteException e) {
      setRegistrationFeedback("Errore nella connessione al server");
    }
  }

  private void saveCredentials() {
//    FileManager.write(LOCAL_CREDENTIALS, currentUser.getEmail() + "," + currentUser.getPassword());
  }

  private void setRegistrationFeedback(String message) {
    registerFeedback.setText(message);
  }
}
