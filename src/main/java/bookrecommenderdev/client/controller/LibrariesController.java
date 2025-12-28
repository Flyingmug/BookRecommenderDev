package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.factory.LibraryItemFactory;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.routing.route.Route;
import bookrecommenderdev.routing.route.RouteMatch;
import bookrecommenderdev.server.dto.LibraryResult;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static bookrecommenderdev.Constants.LIBRARIES_PAGE_SIZE;

public class LibrariesController implements Routable {

  @FXML private FlowPane librariesContainer;
  @FXML private Button prevPageButton;
  @FXML private Button nextPageButton;
  @FXML private VBox prevPageControl;
  @FXML private VBox nextPageControl;
  @FXML private SearchbarController searchbarController;

  AppContext context;
  int currentPageIndex;
  int totalResultCount;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;
    resolve(0);
  }

  @FXML
  private void initialize() {
    currentPageIndex = 0;
    totalResultCount = 0;

    searchbarController.setOnSearch(req -> Router.go("/libraries/search", req));
  }

  private void resolve(int pageIndex) {

    if (!AuthContext.isAuthenticated()) return;   // router shouldn't allow to be here regardless
    int userId = AuthContext.getUser().getId_utente();

    try {

      List<LibraryResult> results = context.server().getListLibrerie(userId, currentPageIndex);

      if (results == null) {
        System.out.println("UI error retrieving libraries");
        return;
      }

      if (results.isEmpty()) {
        System.out.println("Empty result set."); // DEBUG
        showNoResults();
        return;
      }

      totalResultCount = results.size();

      load(results, pageIndex);

    } catch (RemoteException e) {
      System.out.println("SEARCHERR Error while fetching data");
      e.printStackTrace();
    }

  }

  private void load(List<LibraryResult> libraries, int pageIndex) {
    int fromIndex = pageIndex * LIBRARIES_PAGE_SIZE;

    if (fromIndex >= totalResultCount) {
      // Handle empty page (e.g., clear the container and return)
      librariesContainer.getChildren().clear();
      return;
    }

    int toIndex = Math.min(fromIndex + LIBRARIES_PAGE_SIZE, totalResultCount);

    List<LibraryResult> pagedResults = libraries.subList(fromIndex, toIndex);

    librariesContainer.getChildren().clear();

    for (LibraryResult lib : pagedResults) {
      librariesContainer.getChildren().add(
          LibraryItemFactory.createLibraryItem(
              lib.library(),
              lib.bookCount(),
              () -> {
                Router.go("/libraries/" + lib.library().getNome(), TransitionAnimation.LEFT_SLIDE);
              }
          )
      );
    }
    setControls();
  }

  /** Imposta l'utilizzo dei pulsanti di controllo logicamente rispetto ai valori dei risultati di ricerca. */
  private void setControls() {
    setPrevControlVisibility(currentPageIndex > 0);
    setNextControlVisibility((currentPageIndex + 1) * LIBRARIES_PAGE_SIZE < totalResultCount);
    setResultsControlsDisabled(false);
    showDisabled(prevPageButton, false);
    showDisabled(nextPageButton, false);
  }
  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPrev() {
    if (totalResultCount <= 0 || currentPageIndex <= 0) return;

    showDisabled(prevPageButton, true);

    goToPage(currentPageIndex - 1);
  }
  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNext() {
    if (totalResultCount <= 0 ||
        (currentPageIndex + 1) * LIBRARIES_PAGE_SIZE > totalResultCount)
      return;

    showDisabled(nextPageButton, true);

    goToPage(currentPageIndex + 1);
  }

  /** Effettua una nuova richiesta per i risultati alla pagina logica di indice {@code newIndex}. */
  private void goToPage(int newIndex) {
    setResultsControlsDisabled(true);

    currentPageIndex = newIndex;
    resolve(newIndex);
  }

  /** Disabilita i comandi di controlli dei risultati. */
  private void setResultsControlsDisabled(boolean disable) {
    prevPageButton.setDisable(disable);
    nextPageButton.setDisable(disable);
  }
  /** Controlla la visibilità del pulsante di pagina precedente. */
  private void setPrevControlVisibility(boolean visibility) {
    prevPageControl.setVisible(visibility);
  }

  /** Controlla la visibilità del pulsante di pagina successiva. */
  private void setNextControlVisibility(boolean visibility) {
    nextPageControl.setVisible(visibility);
  }

  /** Collassa la pagina e imposta la visibilità a {@code false}. */
  private void showNoResults() {
    librariesContainer.setVisible(false);
  }

  /** Mostra la selezione del pulsante sulla grafica, aggiungendovi la classe rispettiva. */
  private void showDisabled(Button controlButton, boolean b) {
    if (b) {
      controlButton.getStyleClass().add("control-button-customdisabled");
    } else {
      controlButton.getStyleClass().remove("control-button-customdisabled");
    }
  }

  @FXML
  protected void onLibraryOpen() {

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

}
