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

public class ProfileControlController {

  @FXML private Label initials;
  @FXML private Button menuToggleButton;
  @FXML private ContextMenu profileMenu;
  @FXML private Button logoutButton;

  AppContext context;

  @FXML
  private void initialize() {

    // ottieni iniziali dell'utente
    initials.textProperty().bind(
        AuthContext.userProperty().map(user -> {
          if (user == null) return "";
          return getInitials(user);
        })
    );

    profileMenu = new ContextMenu();

    MenuItem logout = new MenuItem("Logout");
    logout.setOnAction(_ -> handleLogout());

    profileMenu.getItems().addAll(logout);
  }

  private void handleLogout() {
    AppContext context = Router.context();
    if (context == null) return;
    System.out.println("yo");

    try {
      context.server().logout(AuthStorage.load().orElse(""));
    } catch (RemoteException | DataAccessException _) {}

    // elimina token e utente locali
    AuthStorage.clear();
    AuthContext.logout();
    Router.go("/");
  }

  @FXML
  private void toggleMenu() {
    if (profileMenu.isShowing()) {
      profileMenu.hide();
    } else {
      profileMenu.show(menuToggleButton, Side.BOTTOM, 0, 5);
    }
  }

  private String getInitials(UtenteSessione user) {
    String first = (user.nome() != null && !user.nome().isEmpty())
        ? user.nome().substring(0, 1) : "";
    String last = (user.cognome() != null && !user.cognome().isEmpty())
        ? user.cognome().substring(0, 1) : "";
    return (first + last).toUpperCase();
  }
}
