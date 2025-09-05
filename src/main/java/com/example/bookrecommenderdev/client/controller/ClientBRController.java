package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.client.factory.BookDisplayFactory;
import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.model.Utente;
import com.example.bookrecommenderdev.server.ServerInterface;
import com.example.bookrecommenderdev.server.dto.PaginaLibro;
import com.example.bookrecommenderdev.utils.FileManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

import static com.example.bookrecommenderdev.utils.InputVerifiers.*;
import static com.example.bookrecommenderdev.utils.Tools.setRandomBackgroundColor;

public class ClientBRController {
  final static int PAGE_SIZE = 50;
  final static String LOCAL_CREDENTIALS = "credentials.txt";
  static BookDisplayFactory bookDisplayCreator = new BookDisplayFactory();

  @FXML
  private Label serverErrorTitle;
  @FXML
  private Label serverErrorLabel;
  @FXML
  private VBox serverConnErrorWrapper;

  @FXML
  private Label welcomeText;
  @FXML
  private StackPane centerStackContainer;
  @FXML
  private ScrollPane resultPage;
  @FXML
  private VBox homePage;
  @FXML
  private TextField searchbar;
  @FXML
  private HBox navbar;
  @FXML
  private HBox navbarControls;
  @FXML
  private StackPane searchbarWrapper;
  @FXML
  private Button searchButton;
  @FXML
  private Region mainPageSpacer;

  @FXML
  private Label resultTitle;
  @FXML
  private HBox resultTitleWrapper;
  @FXML
  private VBox booksResultDisplay;
  @FXML
  private VBox booksResultWrapper;

  @FXML
  private HBox noResultsTitleWrapper;
  @FXML
  private Label resultIndexCounter;
  @FXML
  private Button previousPageButton;
  @FXML
  private Button nextPageButton;

  // login
  @FXML
  private VBox loginPage;
  @FXML
  private Label loginFeedback;
  @FXML
  private TextField loginEmail;
  @FXML
  private TextField loginPassword;
  @FXML
  private Button loginButton;
  @FXML
  private Button confirmLoginButton;
  @FXML
  private CheckBox loginRicordaCredenziali;

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

  @FXML
  private Button profileButton;
  @FXML
  private Label profilePicture;
  @FXML
  private Button logoutButton;  // might be unnecessary, only thing needed is the onAction
  @FXML
  private VBox profilePage;


  @FXML
  public VBox bookPage;
  @FXML
  public Label bookPageTitolo;
  @FXML
  public Label bookPageAutori;
  @FXML
  public Label bookPageAnnoPubblicazione;
  @FXML
  public Label bookPageEditore;
  @FXML
  public Label bookPageCategorie;

  @FXML
  public VBox scoresContainer;


  // librerie
  @FXML
  private Button librariesButton;

  private ServerInterface bookRecommender;
  int currentResultPageIndex;
  int bookResultCount;
  String currentSearchInput;

  // utente corrente
  Utente currentUser;


  @FXML
  public void initialize() {
    initPriorityLayout();
    initRegistry();
    initSetupLayout();

    currentResultPageIndex = 0;
    bookResultCount = 0;
    currentSearchInput = "";


    //
    //
    // TEST
    testBooksearchpage();
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

    // icona di ricerca
    FontIcon noBooksIcon = new FontIcon("mdi2b-book-alert-outline");
    noBooksIcon.setIconSize(38);
    // titolo ricerca fallita
    Label noResultsTitle = new Label("Nessun risultato trovato");
    noResultsTitle.setFont(new Font("Arial", 30));
    noResultsTitle.setPadding(new Insets(5, 10, 5, 10));
    noResultsTitleWrapper = new HBox(noResultsTitle, noBooksIcon);
    noResultsTitleWrapper.setAlignment(Pos.CENTER);

    HBox.setMargin(noResultsTitleWrapper, new Insets(100, 0, 0, 0));



    // login fields
    preventMultipleSpacesAndLimit(loginEmail, 255);
    preventMultipleSpacesAndLimit(loginPassword, 64);
    // registration fields
    preventMultipleSpacesAndLimit(registerName, 64);
    preventMultipleSpacesAndLimit(registerSurname, 64);
    preventMultipleSpacesAndLimit(registerEmail, 255);
    preventMultipleSpacesAndLimit(registerPassword, 64);
    restrictLooseFiscalCodeInput(registerCodiceFiscale);
  }
  private void initPriorityLayout() {
    serverErrorTitle = new Label();
    serverErrorTitle.getStyleClass().addAll("error-label", "connection-error-title");
    serverErrorLabel = new Label();
    serverErrorLabel.getStyleClass().addAll("error-label", "connection-error-label");
    serverConnErrorWrapper = new VBox();
    serverConnErrorWrapper.setAlignment(Pos.CENTER);
    serverConnErrorWrapper.getChildren().addAll(serverErrorTitle, serverErrorLabel);


    librariesButton = new Button("Librerie");
    librariesButton.setFont(new Font("Arial", 15));
    librariesButton.getStyleClass().addAll("libraries-button", "navbar-button");
    librariesButton.setOnAction(_ -> onLibraryList());

    profileButton = new Button();
    profileButton.setText("");
    profileButton.tooltipProperty().set(new Tooltip("Pagina profilo"));
    setRandomBackgroundColor(profileButton);
    profileButton.getStyleClass().add("profile-picture-button");
    profileButton.setOnAction(_ -> onProfilePage());
    profilePicture = new Label();
    profilePicture.getStyleClass().add("profile-picture-text");
    profileButton.setGraphic(profilePicture);
  }
  /**
   * Inizializza l'oggetto remoto del server dal repository.
   */
  private void initRegistry() {
    try {
      Registry reg = LocateRegistry.getRegistry("localhost", 1099);
      bookRecommender = (ServerInterface) reg.lookup("serverBR");
    } catch(RemoteException e) {
      notifyServerError(e.getMessage(), "Server connection failed!\n (Server might not be online or address is wrong)");
      // error display on main page
    } catch(NotBoundException e) {
      notifyServerError(e.getMessage(), "Server not found!\n");

      // error display on main page
    } finally {
      if (bookRecommender != null)
        initVerifyLocalUserCredentials();
    }
  }

  private void initVerifyLocalUserCredentials() {
    String str = FileManager.read(LOCAL_CREDENTIALS);
    if (str != null && !str.isEmpty()) {
      String[] split = str.split(",");
      String email = split[0];
      String password = split[1];
      loginEmail.setText(email);
      loginPassword.setText(password);
      onConfirmLogin();
    }
  }
  /**
   * Caricamento pagina iniziale
   */
  @FXML
  protected void onHomepage() {
    hideAllPages();
    resetHomepage();
    currentResultPageIndex=0;
    currentSearchInput="";
    homePage.setVisible(true);
  }
  /**
   * Ripristina gli elementi della pagina iniziale nelle loro posizioni originali.
   */
  private void resetHomepage() {

    if (!homePage.getChildren().contains(searchbarWrapper)) {
      searchbarWrapper.getStyleClass().remove("searchbar-navbar");
      searchbarWrapper.getStyleClass().add("searchbar-center");
      searchbar.setText("");
      homePage.getChildren().addAll(welcomeText, searchbarWrapper, mainPageSpacer);
    }
  }
  private void resetLoginPage() {
    loginFeedback.setText("");
    loginEmail.setText("");
    loginPassword.setText("");
    loginRicordaCredenziali.setSelected(false);
  }
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
  private void resetBookPage() {
    scoresContainer.getChildren().clear();
  }
  
  /**
   * Nasconde tutti i contenitori rappresentanti le pagine dell'applicazione
  */
  private void hideAllPages() {
    centerStackContainer.getChildren().forEach(child -> child.setVisible(false));
  }


  @FXML
  protected void onSearchAction() {

    // get input
    String newInput = searchbar.getText();
    System.out.println("Searched: " + newInput);  // DEBUG
    if (newInput == null || newInput.isEmpty()) return;

    topSearchbar();
    hideAllPages();
    resultPage.setVisible(true);

    // if there is a new input, set it as the current search value
    boolean newSearch = !newInput.equals(currentSearchInput);
    if (newSearch) {
      setPrevControlVisibility(false);
      currentResultPageIndex = 0;
      currentSearchInput = newInput;
    }

    try {
      Pair<List<Libro>, Integer> data = bookRecommender.searchTitolo(newInput, currentResultPageIndex);
      List<Libro> books = data.getKey();
      int totalResults = data.getValue();

      if (books.isEmpty() || totalResults == 0) {
        System.out.println("Empty result set."); // DEBUG
        showNoResults();
        return;
      }

      System.out.println("Numero di risultati: " + totalResults); // DEBUG

      if (newSearch) setNextControlVisibility(totalResults > PAGE_SIZE);

      setResultsFoundTitle(true);
      booksResultWrapper.setVisible(true);
      loadResults(data);

    } catch(RemoteException e) {
      System.out.println("Error while fetching data");
      e.printStackTrace();
    }

  }
  private void showNoResults() {
    setResultsFoundTitle(false);
    booksResultWrapper.setVisible(false);
  }
  private void topSearchbar() {
    if (!navbar.getChildren().contains(searchbarWrapper)) {
      homePage.getChildren().clear();
      searchbarWrapper.getStyleClass().remove("searchbar-center");
      searchbarWrapper.getStyleClass().add("searchbar-navbar");
      navbar.getChildren().addFirst(searchbarWrapper);
    }
  }
  private void setPrevControlVisibility(boolean visibility) {
    previousPageButton.setVisible(visibility);
  }
  private void setNextControlVisibility(boolean visibility) {
    nextPageButton.setVisible(visibility);
  }

  /**
   * Rimpiazza i children del contenitore del titolo a seconda del risultato della ricerca.
   * @param success risultati trovati o meno
   */
  private void setResultsFoundTitle(boolean success) {
    resultTitleWrapper.getChildren().clear();
    if (success) {
      resultTitleWrapper.getChildren().add(resultTitle);
    } else {
      resultTitleWrapper.getChildren().addAll(noResultsTitleWrapper);
    }
  }

  /**
   * Attraverso data costruisce degli oggetti di tipo VBox per mostrare i dati di ciascun Libro
   * @param data dati ricevuti
   */
  private void loadResults(Pair<List<Libro>, Integer> data) {

    // elimina eventuali elementi precedenti
    booksResultDisplay.getChildren().clear();

    List<Libro> results = data.getKey();

    bookResultCount = data.getValue();
    resultIndexCounter.setText(formatIndexCounter());

    for (Libro l: results) {
      VBox row = bookDisplayCreator.createVBox(l, this::onPublicBookPage);
      booksResultDisplay.getChildren().add(row);
    }

  }
  private String formatIndexCounter() {
    return Math.min(PAGE_SIZE*currentResultPageIndex+1, bookResultCount) +
        "-" + Math.min(PAGE_SIZE*(1+currentResultPageIndex), bookResultCount) +
        " di " + bookResultCount + " risultati";
  }

  @FXML
  protected void onNextResults() {
    goToPage(currentResultPageIndex + 1);
    resultPage.setVvalue(0);
  }
  @FXML
  protected void onPreviousResults() {
    goToPage(currentResultPageIndex - 1);
    resultPage.setVvalue(1);
  }
  private void goToPage(int newIndex) {
    if (newIndex < 0 || newIndex * PAGE_SIZE >= bookResultCount) return;

    currentResultPageIndex = newIndex;
    onSearchAction();

    setPrevControlVisibility(currentResultPageIndex > 0);
    setNextControlVisibility((currentResultPageIndex + 1) * PAGE_SIZE < bookResultCount);
  }



  @FXML
  protected void onLogin() {
    hideAllPages();
    resetLoginPage();
    loginPage.setVisible(true);
  }
  @FXML
  protected void onConfirmLogin() {

    String email = loginEmail.getText();
    String password = loginPassword.getText();

    if (!verifyEmail(email)) {
      setLoginFeedback("Nome utente deve essere tra 1 e 64 caratteri");
      return;
    }

    if (!verifyPassword(password)) {
      setLoginFeedback("Password deve essere tra 8 e 64 caratteri");
      return;
    }

    try {
      Pair<Utente, String> res = bookRecommender.login(email, password);

      switch(res.getValue()) {
        case "success":

          currentUser = res.getKey();

          if (loginRicordaCredenziali.isSelected())
            saveCredentials();

          setLoginFeedback("Login avvenuto con successo");
          setUserAccessed();
          onHomepage();
          break;
        case "no-such-user":
          setLoginFeedback("Credenziali errate");
          break;
        case "db-error":
          setLoginFeedback("Errore nel reperimento dei dati");
          break;
        default:
          setLoginFeedback("Response parsing error");
          break;
      }

    } catch(RemoteException e) {
      setLoginFeedback("Errore nella connessione al server");
    }

  }

  @FXML
  protected void onRegister() {
    hideAllPages();
    resetRegisterPage();
    registerPage.setVisible(true);
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
      String res = bookRecommender.registrazione(u);

      switch(res) {
        case "success":
          setRegistrationFeedback("Registrazione avvenuta con successo");
          currentUser = u;
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
    FileManager.write(LOCAL_CREDENTIALS, currentUser.getEmail() + "," + currentUser.getPassword());
  }
  private void setUserAccessed() {
    navbarControls.getChildren().removeAll(loginButton, registerButton);
    navbarControls.getChildren().addFirst(librariesButton);
    profilePicture.setText(("" + currentUser.getNome().charAt(0) + currentUser.getCognome().charAt(0)).toUpperCase());
    navbarControls.getChildren().addLast(profileButton);
  }

  @FXML
  protected void onLogout() {
    // delete file
    currentUser = null;
    navbarControls.getChildren().removeAll(librariesButton, profileButton);
    navbarControls.getChildren().addFirst(loginButton);
    navbarControls.getChildren().addFirst(registerButton);
    FileManager.delete(LOCAL_CREDENTIALS);
    onHomepage();
  }


  /**
   * Mostra il contenitore della pagina di un libro, nel quale vengono caricati i dati del libro selezionato.
   * @param idLibro id del libro selezionato
   */
  @FXML
  protected void onPublicBookPage(int idLibro) {
    System.out.println("id: " + idLibro); // DEBUG

    resetBookPage();
    hideAllPages();
    bookPage.setVisible(true);

    try {

      PaginaLibro pagina = bookRecommender.getPaginaLibro(idLibro);


      if (pagina != null) {

        if (pagina.getLibro() != null) {
          Libro l = pagina.getLibro();

          bookPageTitolo.setText(l.getTitolo());
          bookPageAutori.setText(l.getAutori());
          bookPageAnnoPubblicazione.setText(Integer.toString(l.getAnnoPubblicazione()));
          bookPageCategorie.setText(l.getCategorie());
          bookPageEditore.setText(l.getEditore());
        }

        if (pagina.getValutazioniAggregate() != null) {
          double[] scores = pagina.getValutazioniAggregate();
          showScores(scores);
        }

      }

    } catch (RemoteException e) {
      e.printStackTrace();

      //
      //
      //
      // Add error display
      //
      //
      //
    }
  }

  @FXML
  protected void onLibraryOpen() {

  }

  @FXML
  protected void onLibraryList() {
    try {
      bookRecommender.getListLibrerie(1);

    } catch (RemoteException e) {

    }
  }

  @FXML
  protected void onLibraryDelete() {

  }

  @FXML
  protected void onLibraryCreate() {

  }

  @FXML
  protected void onLibraryInsert() {

  }

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
  protected void onProfilePage() {
    hideAllPages();
    profilePage.setVisible(true);
  }

  @FXML
  protected void onAction() {

  }


  /**
   * Crea la rappresentazione dei punteggi e della media di essi
   * @param valutazioni array contenente almeno 5 valutazioni
   */
  private void showScores(double[] valutazioni) {

    double totalScore = Arrays.stream(valutazioni)
        .filter(v -> v > 0)
        .average()
        .orElse(0.0);
    scoresContainer.getChildren().add(buildStars(totalScore, 24));

    for (double score: valutazioni) {
      scoresContainer.getChildren().add(buildStars(score, 20));
    }
  }
  /**
   * Restituisce un contenitore di icone di stelle rappresentanti il valore dato come parametro
   * @param score valore rappresentato
   * @return contenitore di icone
   */
  private HBox buildStars(double score, int size) {
    HBox stars = new HBox(2);

    int fullStars = (int) score;                 // whole number part
    boolean hasHalf = (score - fullStars) >= 0.5; // half star if remainder is greater than 0.5

    for (int i = 1; i <= 5; i++) {
      FontIcon star;

      if (i <= fullStars) {
        star = new FontIcon("mdi2s-star"); // full
        star.setIconColor(Paint.valueOf("gold"));
      } else if (i == fullStars + 1 && hasHalf) {
        star = new FontIcon("mdi2s-star-half-full"); // half
        star.setIconColor(Paint.valueOf("gold"));
      } else {
        star = new FontIcon("mdi2s-star-outline"); // empty
        star.setIconColor(Paint.valueOf("gray"));
      }

      star.setIconSize(size);
      stars.getChildren().add(star);
    }

    return stars;
  }


  //
  //
  //
  //
  // test methods

  private void testBooksearchpage() {
    // simulate input insertion
    searchbar.setText("Goat Brothers");
    // simulate search icon click
    onSearchAction();
  }

  private void notifyServerError(String originalMessage, String titleMessage) {
    navbar.getChildren().clear();
    centerStackContainer.getChildren().clear();
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    centerStackContainer.getChildren().add(serverConnErrorWrapper);
  }

  private void setLoginFeedback(String message) {
    loginFeedback.setText(message);
  }
  private void setRegistrationFeedback(String message) {
    registerFeedback.setText(message);
  }

}