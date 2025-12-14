package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Route;
import bookrecommenderdev.routing.RouteGroup;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.model.Utente;
import bookrecommenderdev.server.ServerInterface;
import bookrecommenderdev.utils.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import static bookrecommenderdev.Constants.LOCAL_CREDENTIALS;

public class LayoutController {

  @FXML private VBox serverConnErrorWrapper;
  @FXML private Label serverErrorTitle;
  @FXML private Label serverErrorLabel;

  @FXML private StackPane centerStackContainer;

  @FXML private HBox navbar;
  @FXML private NavbarController navbarController;
  @FXML private HBox navbarControls;

  // utente corrente
  Utente currentUser;

  ServerInterface bookRecommender;
  AppContext context;


  @FXML
  public void initialize() {

    Map<String, Route> routes = new HashMap<>();

    // Registrazione delle pagine
    routes.put("/loading", new Route("loading-view.fxml"));
    routes.put("/", new Route("home-view.fxml"));
    routes.put("/search/:query", new Route("searchResults-view.fxml", RouteGroup.WITH_SEARCH));
    routes.put("/book/:query", new Route("book-view.fxml", RouteGroup.WITH_SEARCH));
    routes.put("/login", new Route("login-view.fxml", RouteGroup.AUTH));
    routes.put("/registration", new Route("registration-view.fxml", RouteGroup.AUTH));
    routes.put("/profile", new Route("profile-view.fxml"));
    routes.put("/libraries", new Route("libraries-view.fxml"));
    routes.put("/libraries/:query", new Route("library-view.fxml"));

    initRegistry();
    if (bookRecommender != null) {
      context = new AppContext(bookRecommender, currentUser, navbarController);
      Router.init(centerStackContainer, context, routes);

      Router.go("/loading"); // Go to loading page on initialization
      initVerifyLocalUserCredentials();
    }

    setCenterBackground();
    //
    //
    // TEST
    // testBookSearchPage();
  }

  /**
   * Carica lo sfondo della pagina centrale.
   */
  private void setCenterBackground() {
    // background
    URL imageUrl = getClass().getResource("/bookrecommenderdev/assets/library-background2.jpg");
    if (imageUrl != null) {
      Image image = new Image(imageUrl.toExternalForm());

      BackgroundImage backgroundImage = new BackgroundImage(
          image,
          BackgroundRepeat.NO_REPEAT,
          BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(100, 100, true, true, false, true)
      );

      centerStackContainer.setBackground(new Background(backgroundImage));
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

    try {
      // todo implementation
    } catch (Error e) {

    }
  }

  @FXML
  protected void onLibraryList() {

    Router.go("/libraries");
//    try {
//
//      List<Pair<Libreria, Integer>> librerie = bookRecommender.getListLibrerie(1);
//
//
//
//    } catch (RemoteException e) {
//
//    }
  }


//  private void topSearchbar() {
//    if (!navbar.getChildren().contains(searchbarWrapper)) {
//      homePage.getChildren().clear();
//      searchbarWrapper.getStyleClass().remove("searchbar-center");
//      searchbarWrapper.getStyleClass().add("searchbar-navbar");
//      navbar.getChildren().addFirst(searchbarWrapper);
//    }
//
//  }


  @FXML
  protected void onSearchCriteriaDisplay() {
  }
  @FXML
  protected void onCriteriaSelection() {
  }
  @FXML
  protected void onCreateUser() {
  }
  @FXML
  protected void onDeleteUser() {
  }
  @FXML
  protected void onAction() {
  }

  private void notifyServerError(String originalMessage, String titleMessage) {
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    serverConnErrorWrapper.setManaged(true);
  }



  public void onSearchAction(MouseEvent mouseEvent) {

  }
}