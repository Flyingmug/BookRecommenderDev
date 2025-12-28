package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Map;

public class SearchLibrariesController implements Routable {

  @FXML private Label searchedTitle;
  @FXML private Parent resultsSection;
  @FXML private SearchResultsController resultsSectionController;
  @FXML private SearchbarController searchbarController;

  @FXML private ErrorBannerController errorBannerController;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    searchbarController.setOnSearch(req -> Router.go("/libraries/search", req));
    runSearch(context, state);
  }

  private void runSearch(AppContext context, Object request) {
    SearchRequest req = (request instanceof SearchRequest sr) ? sr : null;

    if (!AuthContext.isAuthenticated()) {
      // Controllo secondario, il percorso dovrebbe essere protetto
      Router.go("/login");
      return;
    }

    if (req == null) {
      // resultsController.showEmptyState("Inserisci una ricerca.");
      return;
    }

    if (!req.isValid()) {
      searchedTitle.setText("Ricerca");
      // resultsController.showErrorState("Richiesta di ricerca non valida.");
      return;
    }

    searchedTitle.setText(buildTitle(req));

    int idUtente = AuthContext.getUser().getId_utente();

    PageFetcher<Libro> source = indicePagina ->
        context.server().searchAllLibrerie(idUtente, req, indicePagina);

    resultsSectionController.setSource(source, req);
  }

  private String buildTitle(SearchRequest req) {
    return switch (req.getTipo()) {
      case TITOLO -> "Titolo: " + req.getTitolo();
      case AUTORE -> "Autore: " + req.getAutore();
      case AUTORE_ANNO -> "Autore: " + req.getAutore() + " (" + req.getAnno() + ")";
    };
  }

  private void showError(String message, Runnable onRetry, Runnable onBack) {
    errorBannerController.show(message, onRetry, onBack);
  }
}
