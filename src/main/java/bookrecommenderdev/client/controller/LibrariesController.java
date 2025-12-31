package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.factory.LibraryItemFactory;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.client.routing.route.Routable;
import bookrecommenderdev.model.dto.PaginaLibreria;
import bookrecommenderdev.model.dto.PaginaLibrerieRisultati;
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

  private AppContext context;
  private int currentPageIndex = 0;
  private int totalResultCount = 0;

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

    if (!AuthContext.isAuthenticated()) return;
    int userId = AuthContext.getUser().idUtente();

    try {
      PaginaLibrerieRisultati page = context.server().getListLibrerie(userId, pageIndex);

      List<PaginaLibreria> results = page.results();
      totalResultCount = page.totalCount();
      currentPageIndex = Math.max(0, pageIndex);

      if (results == null || results.isEmpty() || totalResultCount == 0) {
        showNoResults(true);
        load(List.of());
        setControls();
        return;
      }

      showNoResults(false);
      load(results);
      setControls();

    } catch (RemoteException e) {
      System.out.println("SEARCHERR Error while fetching data");
      e.printStackTrace();
      // TODO show banner
    }
  }

  private void load(List<PaginaLibreria> libraries) {
    librariesContainer.getChildren().clear();

    for (PaginaLibreria lib : libraries) {
      librariesContainer.getChildren().add(
          LibraryItemFactory.create(
              lib.library(),
              lib.bookCount(),
              () -> Router.go("/libraries/" + lib.library().getIdLibreria(), TransitionAnimation.LEFT_SLIDE)
          )
      );
    }
  }

  @FXML
  private void onCreate() {
    if (!AuthContext.isAuthenticated()) {
      Router.go("/login");
      return;
    }

    Router.go("/libraries/create");
  }

  /** Imposta l'utilizzo dei pulsanti di controllo logicamente rispetto ai valori dei risultati di ricerca. */
  private void setControls() {
    boolean canPrev = currentPageIndex > 0;
    boolean canNext = (currentPageIndex + 1) * LIBRARIES_PAGE_SIZE < totalResultCount;

    setPrevControlVisibility(canPrev);
    setNextControlVisibility(canNext);

    prevPageButton.setDisable(!canPrev);
    nextPageButton.setDisable(!canNext);
  }
  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPrev() {
    if (totalResultCount <= 0 || currentPageIndex <= 0) return;

    goToPage(currentPageIndex - 1);
  }
  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNext() {
    if (totalResultCount <= 0 ||
        (currentPageIndex + 1) * LIBRARIES_PAGE_SIZE >= totalResultCount)
      return;

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
  private void showNoResults(boolean noResults) {
    librariesContainer.setVisible(!noResults);
    librariesContainer.setManaged(!noResults);
  }

}
