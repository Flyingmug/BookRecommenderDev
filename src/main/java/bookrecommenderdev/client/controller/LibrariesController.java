package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
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

/**
 * Controller della pagina "Librerie".
 *
 * <p>Recupera e mostra l’elenco delle librerie dell’utente autenticato, con paginazione.
 * La navigazione e le azioni utente (creazione, ricerca, cambio pagina) vengono gestite tramite {@link Router}.
 */
public class LibrariesController implements Routable {

  @FXML private VBox mainContent;
  @FXML private FlowPane librariesContainer;
  @FXML private Button prevPageButton;
  @FXML private Button nextPageButton;
  @FXML private VBox prevPageControl;
  @FXML private VBox nextPageControl;
  @FXML private SearchbarController searchbarController;
  @FXML private ErrorBannerController errorBannerController;

  private AppContext context;
  private int currentPageIndex = 0;
  private int totalResultCount = 0;


  /**
   * <p>Memorizza il contesto ed effettua la richiesta per ottenere le librerie dell'utente.
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;
    resolve(0);
  }

  /**
   * Reimposta lo stato di paginazione e collega la barra di ricerca alla pagina
   * dedicata alla ricerca nelle librerie.
   */
  @FXML
  private void initialize() {
    currentPageIndex = 0;
    totalResultCount = 0;

    searchbarController.setOnSearch(req -> Router.go("/libraries/search", req));
  }

  /**
   * Recupera una pagina di librerie dal server e aggiorna la UI.
   * <p>
   * Se l’utente non è autenticato non esegue alcuna richiesta.
   * In caso di assenza risultati collassa la lista con relativi controlli di paginazione.
   *
   * @param pageIndex indice pagina richiesta
   */
  private void resolve(int pageIndex) {

    if (!AuthContext.isAuthenticated()) return;
    int userId = AuthContext.getUser().idUtente();

    try {
      PaginaLibrerieRisultati page = context.server().getListLibrerie(userId, pageIndex);

      List<PaginaLibreria> results = page.results();
      totalResultCount = page.totalCount();
      currentPageIndex = Math.max(0, pageIndex);

      if (results == null || results.isEmpty() || totalResultCount == 0) {
        showResults(false);
        load(List.of());
        setControls();
        return;
      }

      showResults(true);
      load(results);
      setControls();

    } catch (RemoteException e) {
      System.out.println("SEARCHERR Error while fetching data");
      setContentVisible(false);
      showError("Errore nel reperimento dei risultati");
    }
  }


  /**
   * Carica la lista grafica delle librerie.
   * <p>
   * Ogni elemento viene creato tramite {@link LibraryItemFactory}.
   *
   * @param libraries lista di risultati
   */
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

  /**
   * Handler UI per la creazione di una nuova libreria.
   *
   * <p>Se l’utente non è autenticato, reindirizza al login; altrimenti apre la pagina di creazione.
   */
  @FXML
  private void onCreate() {
    if (!AuthContext.isAuthenticated()) {
      Router.go("/login");
      return;
    }

    Router.go("/libraries/create");
  }

  /**
   * Aggiorna abilitazione e visibilità dei controlli di paginazione in base allo stato corrente.
   *
   * <p>La possibilità di avanzare dipende dal numero totale risultati e dalla dimensione pagina.
   */
  private void setControls() {
    boolean canPrev = currentPageIndex > 0;
    boolean canNext = (currentPageIndex + 1) * LIBRARIES_PAGE_SIZE < totalResultCount;

    setPrevControlVisibility(canPrev);
    setNextControlVisibility(canNext);

    prevPageButton.setDisable(!canPrev);
    nextPageButton.setDisable(!canNext);
  }

  /**
   * Handler UI per tornare alla pagina precedente dei risultati.
   * Esegue controlli di validità su indice corrente e numero risultati.
   */
  @FXML
  private void onPrev() {
    if (totalResultCount <= 0 || currentPageIndex <= 0) return;

    goToPage(currentPageIndex - 1);
  }

  /**
   * Handler UI per passare alla pagina successiva dei risultati.
   * Esegue controlli di validità su indice corrente e disponibilità di ulteriori risultati.
   */
  @FXML
  private void onNext() {
    if (totalResultCount <= 0 ||
        (currentPageIndex + 1) * LIBRARIES_PAGE_SIZE >= totalResultCount)
      return;

    goToPage(currentPageIndex + 1);
  }

  /**
   * Richiede il caricamento della pagina con indice {@code newIndex}.
   * Durante la richiesta disabilita temporaneamente i controlli di paginazione.
   *
   * @param newIndex nuovo indice pagina (0-based)
   */
  private void goToPage(int newIndex) {
    prevPageButton.setDisable(true);
    nextPageButton.setDisable(true);

    currentPageIndex = newIndex;
    resolve(newIndex);
  }

  /** Controlla la visibilità del pulsante di pagina precedente. */
  private void setPrevControlVisibility(boolean visibility) {
    prevPageControl.setVisible(visibility);
  }

  /** Controlla la visibilità del pulsante di pagina successiva. */
  private void setNextControlVisibility(boolean visibility) {
    nextPageControl.setVisible(visibility);
  }

  /**
   * Mostra o collassa l’area dei risultati.
   *
   * @param resultsPresent {@code false} se non ci sono risultati da mostrare
   */
  private void showResults(boolean resultsPresent) {
    librariesContainer.setVisible(resultsPresent);
    librariesContainer.setManaged(resultsPresent);
  }

  /**
   * Controlla la visibilità del contenuto principale della pagina.
   *
   * @param visible visibilità
   */
  private void setContentVisible(boolean visible) {
   mainContent.setVisible(visible);
   mainContent.setManaged(visible);
  }

  private void showError(String message) {
    errorBannerController.show(message, null, null);
  }

}
