package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.Utente;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;

import java.util.Map;

public class LibraryPageController implements Routable {


  @FXML private Parent resultsSection;
  @FXML private SearchResultsController resultsSectionController;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {

    if (resultsSectionController == null) return;

    String query = params.get("query");

    Utente u = AuthContext.getUser();

    PageFetcher<Libro> source = page ->
        context.server().searchLibreria(u.getId_utente(), query, page);

    resultsSectionController.setSource(source, query);
  }

}
