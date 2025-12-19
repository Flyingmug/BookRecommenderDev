package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.Map;

public class ReviewsController implements Routable {

  @FXML
  public VBox reviewsPage;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    String idLibro = params.get("query");
  }
}
