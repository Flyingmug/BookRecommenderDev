package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.Utente;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.routing.route.Route;
import bookrecommenderdev.routing.route.RouteMatch;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Map;

public class LibraryPageController implements Routable {

  @FXML private Label libraryTitle;
  @FXML private Parent resultsSection;
  @FXML private SearchResultsController resultsSectionController;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {

    if (resultsSectionController == null) return;

    String query = params.get("id");

    Utente u = AuthContext.getUser();

    SearchRequest req = SearchRequest.perTitolo(query);

    PageFetcher<Libro> source = page ->
        context.server().searchAllLibrerie(u.getId_utente(), req, page);

    resultsSectionController.setSource(source, SearchRequest.perTitolo(query));
  }

  @FXML
  private void initialize() {

  }
}
