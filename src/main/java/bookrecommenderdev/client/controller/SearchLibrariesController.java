package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;

/**
 * Controller JavaFX della ricerca libri “nelle librerie” dell’utente autenticato.
 * <p>
 * Riceve una {@link SearchRequest} nello stato di navigazione e configura un
 * {@link SearchResultsController} per mostrare i risultati ottenuti dal server.
 */
public class SearchLibrariesController implements Routable {

  @FXML private Label searchedTitle;
  @FXML private SearchResultsController<Libro> resultsController;
  @FXML private SearchbarController searchbarController;

  @FXML private ErrorBannerController errorBannerController;

  /**
   * Collega la searchbar alla rotta corrente e avvia la ricerca in base allo stato ricevuto.
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    searchbarController.setOnSearch(req -> Router.go("/libraries/search", req));
    runSearch(context, state);
  }

  /**
   * Esegue la ricerca in base all’oggetto {@code request} ricevuto dallo stato di navigazione.
   * <p>
   * Richiede autenticazione (controllo secondario) e valida la {@link SearchRequest} prima
   * di configurare la sorgente paginata dei risultati.
   *
   * @param context contesto applicativo client
   * @param request oggetto stato (atteso: {@link SearchRequest})
   */
  private void runSearch(AppContext context, Object request) {
    SearchRequest req = (request instanceof SearchRequest sr) ? sr : null;

    if (!AuthContext.isAuthenticated()) {
      // Controllo secondario, il percorso dovrebbe essere protetto
      Router.go("/login");
      return;
    }

    if (req == null) {
      showError("Inserisci una richiesta");
      return;
    }

    if (!req.isValid()) {
      showError("Richiesta di ricerca non valida");
      return;
    }

    searchedTitle.setText(buildTitle(req));

    int idUtente = AuthContext.getUser().idUtente();

    PageFetcher<Libro> source = indicePagina ->
        context.server().searchAllLibrerie(idUtente, req, indicePagina);

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
   * Costruisce il titolo della pagina in base al tipo di ricerca.
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
