package bookrecommenderdev.client.controller.errors;

import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

public class NotFoundController implements Routable {

  @FXML Label title;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    try {
      String message = (String) state;

      if (message != null && !message.isBlank()) {
        title.setText(message);
      }

    } catch (Exception _) {}
  }
}
