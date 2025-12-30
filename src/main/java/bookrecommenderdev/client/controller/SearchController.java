package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;

public class SearchController implements Routable {

  @FXML private Label searchedTitle;
  @FXML private Parent resultsSection;
  @FXML private SearchResultsController<Libro> resultsController;

  @FXML private ErrorBannerController errorBannerController;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    SearchRequest req = (state instanceof SearchRequest sr) ? sr : null;
    if (req == null) {
      showError("Inserisci una richiesta", null, null);
      return;
    }

    if (!req.isValid()) {
      showError("Richiesta di ricerca non valida", null, null);
      return;
    }

    searchedTitle.setText(buildTitle(req));

    PageFetcher<Libro> source = indicePagina ->
        context.server().cercaLibro(req, indicePagina);

    resultsController.setItemRenderer(this::renderBookItem);
    resultsController.setSource(source, PAGE_SIZE);
  }

  private Parent renderBookItem(Libro l) {
    return BookResultItemFactory.create(
        l, id -> Router.go("/book/" + id, TransitionAnimation.LEFT_SLIDE)
    );
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
