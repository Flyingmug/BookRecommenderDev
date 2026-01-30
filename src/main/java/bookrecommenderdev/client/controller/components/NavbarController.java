package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Controller JavaFX della barra di navigazione principale.
 * <p>
 * Gestisce la visibilità delle azioni in base allo stato di autenticazione
 * e inoltra le interazioni di navigazione al {@link Router}.
 */
public class NavbarController {

  @FXML private VBox searchbarContainer;
  @FXML private SearchbarController searchbarController;
  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private Button librariesButton;
  @FXML private Parent profileButton;

  /**
   * Imposta la visibilità della barra di ricerca.
   *
   * @param visible {@code true} per mostrare la searchbar, {@code false} per nasconderla
   */
  public void setSearchbarVisible(boolean visible) {
    searchbarContainer.setVisible(visible);
    searchbarContainer.setManaged(visible);
  }

  @FXML
  public void initialize() {
    // Login / registrazione visibili solo se non autenticato
    loginButton.visibleProperty().bind(AuthContext.userProperty().isNull());
    loginButton.managedProperty().bind(AuthContext.userProperty().isNull());
    loginButton.disableProperty().bind(AuthContext.userProperty().isNotNull());

    registerButton.visibleProperty().bind(AuthContext.userProperty().isNull());
    registerButton.managedProperty().bind(AuthContext.userProperty().isNull());
    registerButton.disableProperty().bind(AuthContext.userProperty().isNotNull());

    // Librerie e profilo visibili solo se autenticato
    librariesButton.visibleProperty().bind(AuthContext.userProperty().isNotNull());
    librariesButton.managedProperty().bind(AuthContext.userProperty().isNotNull());
    librariesButton.disableProperty().bind(AuthContext.userProperty().isNull());

    profileButton.visibleProperty().bind(AuthContext.userProperty().isNotNull());
    profileButton.managedProperty().bind(AuthContext.userProperty().isNotNull());
    profileButton.disableProperty().bind(AuthContext.userProperty().isNull());

    // Inoltra la ricerca globale alla pagina di risultati
    searchbarController.setOnSearch(req -> Router.go("/search", req));
  }

  /** Naviga alla pagina di registrazione. */
  @FXML public void onRegister() {
    Router.go("/registration");
  }

  /** Naviga alla pagina di login. */
  @FXML public void onLogin() {
    Router.go("/login");
  }

  /** Naviga alla pagina delle librerie dell’utente. */
  @FXML public void onLibraries() {
    Router.go("/libraries");
  }

}