package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;

/**
 * Controller JavaFX della pagina risultati di ricerca.
 * <p>
 * Riceve una {@link SearchRequest} nello stato di navigazione, imposta il titolo della pagina
 * e configura un {@link SearchResultsController} per recuperare i risultati dal server.
 */
public class SearchController implements Routable {

  @FXML private Label searchedTitle;
  @FXML private SearchResultsController<Libro> resultsController;

  @FXML private ErrorBannerController errorBannerController;

  /**
   * Invocato dal routing all’ingresso nella pagina.
   * <p>
   * Recupera la {@link SearchRequest} dallo stato, valida la richiesta e inizializza
   * la sorgente paginata per i risultati.
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    SearchRequest req = (state instanceof SearchRequest sr) ? sr : null;
    if (req == null) {
      showError("Inserisci una richiesta");
      return;
    }

    if (!req.isValid()) {
      showError("Richiesta di ricerca non valida");
      return;
    }

    searchedTitle.setText(buildTitle(req));

    PageFetcher<Libro> source = indicePagina ->
        context.server().cercaLibro(req, indicePagina);

    resultsController.setItemRenderer(this::renderBookItem);
    resultsController.setSource(source, PAGE_SIZE);
  }

  /**
   * Renderizza un libro come elemento cliccabile che apre la pagina dettaglio del libro.
   *
   * @param l libro da renderizzare
   * @return nodo UI del risultato
   */
  private Parent renderBookItem(Libro l) {
    return BookResultItemFactory.create(
        l, id -> Router.go("/book/" + id, TransitionAnimation.LEFT_SLIDE)
    );
  }

  /**
   * Costruisce il testo del titolo della pagina in base al tipo di ricerca.
   *
   * @param req richiesta di ricerca valida
   * @return stringa descrittiva (es. "Titolo: ...", "Autore: ...")
   */
  private String buildTitle(SearchRequest req) {
    return switch (req.getTipo()) {
      case TITOLO -> "Titolo: " + req.getTitolo();
      case AUTORE -> "Autore: " + req.getAutore();
      case AUTORE_ANNO -> "Autore: " + req.getAutore() + " (" + req.getAnno() + ")";
    };
  }

  /**
   * Mostra un errore tramite {@link ErrorBannerController}.
   *
   * @param message testo dell’errore
   */
  private void showError(String message) {
    errorBannerController.show(message, null, null);
  }
}
