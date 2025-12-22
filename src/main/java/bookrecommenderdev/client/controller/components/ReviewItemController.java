package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.Valutazione;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ReviewItemController {

  @FXML
  private Label authorLabel;
  @FXML private Label contentLabel;
  @FXML private HBox starsContainer;

  public void setReview(Valutazione review) {
    authorLabel.setText(review.getRecensioneOriginalita());
    contentLabel.setText(review.getRecensioneContenuto());

//    starsContainer.getChildren().setAll(
//        StarFactory.createStars(review.rating())
//    );
  }
}