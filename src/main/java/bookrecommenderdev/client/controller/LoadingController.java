package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.TransitionAnimation;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.net.URL;

public class LoadingController {

  @FXML public StackPane loadingAnimationPane;

  @FXML
  public void initialize() {

    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();

    // Get URL directly from resources (works in IDE and JAR)
    URL resourceUrl = getClass().getResource("/bookrecommenderdev/assets/BooksLoading_clipped.svg");
    if (resourceUrl != null) {
      String svgUrl = resourceUrl.toExternalForm();
      webEngine.load(svgUrl);
      loadingAnimationPane.getChildren().add(webView);

      // 2. Set the known duration of your SVG animation (e.g., 3.1 seconds)
      Duration animDuration = Duration.seconds(3.3);

      PauseTransition delay = new PauseTransition(animDuration);
      delay.setOnFinished(_ ->
          Router.go("/", TransitionAnimation.FADE_INTO)
      );
      delay.play();


//      // fade animation
//      FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.0), webView);
//
//      fadeOut.setFromValue(1.0);
//      fadeOut.setToValue(0.0);
//
//      fadeOut.play();
    }

  }

}
