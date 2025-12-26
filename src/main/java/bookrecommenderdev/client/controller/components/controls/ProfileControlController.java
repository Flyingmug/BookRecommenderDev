package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.model.Utente;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.auth.AuthStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class ProfileControlController {

  @FXML private Label initials;
  @FXML private Button menuToggleButton;
  @FXML private ContextMenu profileMenu;
  @FXML private Button logoutButton;

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

//    MenuItem profile = new MenuItem("Profilo");
//    profile.setOnAction(e -> Router.go("/profile"));
//
//    MenuItem settings = new MenuItem("Impostazioni");
//    settings.setOnAction(e -> Router.go("/settings"));

    MenuItem logout = new MenuItem("Logout");
    logout.setOnAction(_ -> handleLogout());

    profileMenu.getItems().addAll(logout);
  }

  private void handleLogout() {
    AuthContext.logout();
    AuthStorage.clear();
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

  private String getInitials(Utente user) {
    String first = (user.getNome() != null && !user.getNome().isEmpty())
        ? user.getNome().substring(0, 1) : "";
    String last = (user.getCognome() != null && !user.getCognome().isEmpty())
        ? user.getCognome().substring(0, 1) : "";
    return (first + last).toUpperCase();
  }
}
