package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.routing.AppContext;
import com.example.bookrecommenderdev.routing.Route;
import com.example.bookrecommenderdev.routing.RouteGroup;
import com.example.bookrecommenderdev.routing.Router;
import com.example.bookrecommenderdev.model.Utente;
import com.example.bookrecommenderdev.server.ServerInterface;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class LayoutController {

  @FXML private Label serverErrorTitle;
  @FXML private Label serverErrorLabel;
  @FXML private VBox serverConnErrorWrapper;

  @FXML private StackPane centerStackContainer;

  @FXML private HBox navbar;
  @FXML private NavbarController navbarController;
  @FXML private HBox navbarControls;


  @FXML
  private StackPane searchbarWrapper;

  // utente corrente
  Utente currentUser;

  ServerInterface bookRecommender;
  AppContext context;


  @FXML
  public void initialize() {
    initPriorityLayout();


    Map<String, Route> routes = new HashMap<>();

    // Registrazione delle pagine
    routes.put("/", new Route("home-view.fxml"));
    routes.put("/search/:query", new Route("searchResults-view.fxml", RouteGroup.WITH_SEARCH));
    routes.put("/book/:query", new Route("book-view.fxml", RouteGroup.WITH_SEARCH));
    routes.put("/login", new Route("login-view.fxml", RouteGroup.AUTH));
    routes.put("/registration", new Route("registration-view.fxml", RouteGroup.AUTH));
    routes.put("/profile", new Route("profile-view.fxml"));
    routes.put("/libraries", new Route("libraries-view.fxml"));
    routes.put("/libraries/:query", new Route("library-view.fxml"));

    context = new AppContext(bookRecommender, currentUser, navbarController);
    Router.init(centerStackContainer, context, routes);

    initRegistry();
    initSetupLayout();
    //
    //
    // TEST
    // testBookSearchPage();
  }

  /**
   * Inizializza gli elementi grafici privati visualizzabili solo dopo certe azioni.
   */
  private void initSetupLayout() {

    // background
    URL imageUrl = getClass().getResource("/com/example/bookrecommenderdev/assets/library-background2.jpg");
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
  private void initPriorityLayout() {
    Label serverErrorIcon = new Label();
    serverErrorIcon.setGraphic(new FontIcon("mdi2a-alert-outline"));
    serverErrorIcon.setFont(new Font(75));
    serverErrorTitle = new Label();
    serverErrorTitle.getStyleClass().addAll("error-label", "connection-error-title");
    serverErrorLabel = new Label();
    serverErrorLabel.getStyleClass().addAll("error-label", "connection-error-label");
    serverConnErrorWrapper = new VBox();
    serverConnErrorWrapper.setAlignment(Pos.CENTER);
    serverConnErrorWrapper.getChildren().addAll(serverErrorIcon, serverErrorTitle, serverErrorLabel);

  }
  /**
   * Inizializza l'oggetto remoto del server dal repository.
   */
  private void initRegistry() {
    try {
      Registry reg = LocateRegistry.getRegistry("localhost", 1099);
      bookRecommender = (ServerInterface) reg.lookup("serverBR");

    } catch(RemoteException e) {
      // error display on main page
      notifyServerError(e.getMessage(), "Server connection failed!\n (Server might not be online or address is wrong)");
    } catch(NotBoundException e) {
      // error display on main page
      notifyServerError(e.getMessage(), "Server not found!\n");
    } finally {
      if (bookRecommender != null) {
        System.out.println("TMP message: local info verification");
        Router.go("/"); // apertura della pagina iniziale all'esecuzione
//        initVerifyLocalUserCredentials();
      }
    }
  }


//  /**
//   * Nasconde tutti i contenitori rappresentanti le pagine dell'applicazione
//  */
//  private void hideAllPages() {
//    centerStackContainer.getChildren().forEach(child -> child.setVisible(false));
//  }


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
    navbar.getChildren().clear();
    centerStackContainer.getChildren().clear();
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    centerStackContainer.getChildren().add(serverConnErrorWrapper);
  }



  public void onSearchAction(MouseEvent mouseEvent) {

  }
}