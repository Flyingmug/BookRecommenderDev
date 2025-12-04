package bookrecommenderdev.client.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;

public class LoadingController {

  @FXML public StackPane loadingAnimationPane;

  @FXML
  public void initialize() {

    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();

    // Get URL directly from resources (works in IDE and JAR)
    URL resourceUrl = getClass().getResource("/bookrecommenderdev/assets/BooksLoading_clipped.svg");
    String svgUrl = resourceUrl.toExternalForm();

    webEngine.load(svgUrl);

    // add the WebView to your pane
    loadingAnimationPane.getChildren().add(webView);
  }

}
