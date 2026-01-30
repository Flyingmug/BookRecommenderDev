package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.auth.AuthStorage;
import bookrecommenderdev.model.dto.UtenteSessione;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;

import java.rmi.RemoteException;


/**
 * Controller JavaFX per il controllo profilo utente nella UI.
 * <p>
 * Mostra le iniziali dell’utente autenticato e fornisce un menu contestuale
 * con azioni relative alla sessione (es. logout).
 */
public class ProfileControlController {

  @FXML private Label initials;
  @FXML private Button menuToggleButton;
  @FXML private ContextMenu profileMenu;
  @FXML private Button logoutButton;

  AppContext context;

  /**
   * Inizializza il controllo profilo.
   * <p>
   * Collega le iniziali dell’utente allo stato di autenticazione corrente
   * e costruisce il menu contestuale con le azioni disponibili.
   */
  @FXML
  private void initialize() {

    initials.textProperty().bind(
        AuthContext.userProperty().map(user -> {
          if (user == null) return "";
          return getInitials(user);
        })
    );

    profileMenu = new ContextMenu();

    MenuItem logout = new MenuItem("Logout");
    logout.setOnAction(_ -> handleLogout());

    profileMenu.getItems().add(logout);
  }

  /**
   * Gestisce il logout dell’utente.
   * <p>
   * Invia la richiesta di logout al server (se disponibile),
   * cancella i dati locali di autenticazione e reindirizza alla homepage.
   */
  private void handleLogout() {
    AppContext context = Router.context();
    if (context == null) return;

    try {
      context.server().logout(AuthStorage.load().orElse(""));
    } catch (RemoteException | DataAccessException _) {
      // errore ignorato: il logout locale viene comunque completato
    }

    AuthStorage.clear();
    AuthContext.logout();
    Router.go("/");
  }

  /**
   * Handler FXML: mostra o nasconde il menu profilo.
   */
  @FXML
  private void toggleMenu() {
    if (profileMenu.isShowing()) {
      profileMenu.hide();
    } else {
      profileMenu.show(menuToggleButton, Side.BOTTOM, 0, 5);
    }
  }

  /**
   * Calcola le iniziali dell’utente a partire da nome e cognome.
   *
   * @param user utente autenticato
   * @return stringa con le iniziali in maiuscolo
   */
  private String getInitials(UtenteSessione user) {
    String first = (user.nome() != null && !user.nome().isEmpty())
        ? user.nome().substring(0, 1) : "";
    String last = (user.cognome() != null && !user.cognome().isEmpty())
        ? user.cognome().substring(0, 1) : "";
    return (first + last).toUpperCase();
  }
}