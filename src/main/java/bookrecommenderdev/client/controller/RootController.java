package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.Utente;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Route;
import bookrecommenderdev.routing.LayoutType;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.server.ServerInterface;
import bookrecommenderdev.utils.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import static bookrecommenderdev.Constants.LOCAL_CREDENTIALS;

public class RootController {

  @FXML public StackPane content;
  @FXML private VBox serverConnErrorWrapper;
  @FXML private Label serverErrorTitle;
  @FXML private Label serverErrorLabel;

  // utente corrente
  Utente currentUser;

  ServerInterface bookRecommender;
  AppContext context;

  @FXML
  public void initialize() {
    Map<String, Route> routes = new HashMap<>();

    // Registrazione delle pagine
//    routes.put("/loading", new Route("loading-view.fxml"));
    routes.put("/", new Route("home-view.fxml"));
    routes.put("/search/:query", new Route("searchResults-view.fxml", LayoutType.INTEGRATED));
    routes.put("/book/:query", new Route("book-view.fxml", LayoutType.INTEGRATED));
    routes.put("/book/:query/reviews", new Route("book-view.fxml", LayoutType.INTEGRATED));
    routes.put("/login", new Route("login-view.fxml", LayoutType.EMPTY));
    routes.put("/registration", new Route("registration-view.fxml", LayoutType.EMPTY));
    routes.put("/profile", new Route("profile-view.fxml"));
    routes.put("/libraries", new Route("libraries-view.fxml"));
    routes.put("/libraries/:query", new Route("library-view.fxml"));

    initRegistry();
    if (bookRecommender != null) {
      context = new AppContext(bookRecommender, currentUser);
      Router.init(content, context, routes);  // fixme not completed

      Router.go("/");
      initVerifyLocalUserCredentials();
    }

  }

  /**
   * Inizializza l'oggetto remoto RMI server dal repository.
   */
  private void initRegistry() {
    try {
      Registry reg = LocateRegistry.getRegistry("localhost", 1099);
      bookRecommender = (ServerInterface) reg.lookup("serverBR");
    } catch(RemoteException e) {
      notifyServerError(e.getMessage(), "Server connection failed!\n (Server might not be online or address is wrong)");
    } catch(NotBoundException e) {
      notifyServerError(e.getMessage(), "Server not found!\n");
    }
  }

  /**
   * DDD
   */
  private void initVerifyLocalUserCredentials() {
//    System.out.println("TMP message: local info verification"); // debug

    String str = FileManager.read(LOCAL_CREDENTIALS);
    if (str != null && !str.isEmpty()) {
      String[] split = str.split(",");
      String email = split[0];
      String password = split[1];
//      loginEmailloginEmail.setText(email);
//      loginPassword.setText(password);
//      onConfirmLogin();
    }

    // todo separate string manipulation from logic

//    try {
//      // todo implementation
//    } catch (Error e) {
//
//    }
  }

  private void notifyServerError(String originalMessage, String titleMessage) {
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    serverConnErrorWrapper.setManaged(true);
  }

}
