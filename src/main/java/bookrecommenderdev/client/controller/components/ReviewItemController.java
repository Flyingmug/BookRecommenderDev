package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.factory.StarIconFactory;
import bookrecommenderdev.model.base.CampoValutazione;
import bookrecommenderdev.model.base.Valutazione;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import static bookrecommenderdev.model.utils.InputVerifiers.notNull;

public class ReviewItemController {

  @FXML private GridPane reviewContainer;
  @FXML private HBox reviewHeaderScoreContainer;
  @FXML private Label reviewGenerale;

  public void setReview(Valutazione review) {
    if (review == null) {
      reviewHeaderScoreContainer.getChildren().clear();
      hide(reviewGenerale);
      return;
    }

    // voto finale
    reviewHeaderScoreContainer.getChildren().setAll(
        new Label(String.format("%.1f", review.getVotoFinale())),
        StarIconFactory.buildStars(review.getVotoFinale())
    );

    // aggiunta recensione generale (se presente)
    String generale = notNull(review.getRecensioneGenerale());
    if(!generale.isEmpty()) {
      show(reviewGenerale);
      reviewGenerale.setText(review.getRecensioneGenerale());
    } else {
      reviewGenerale.setVisible(false);
      reviewGenerale.setManaged(false);
    }

    // aggiunta recensioni dei campi di valutazione
    int row = 1;
    for (CampoValutazione campo : CampoValutazione.values()) {
      if (campo == CampoValutazione.GENERALE) continue;

      String valueText = notNull(getReviewField(campo, review));
      int score = (int) getReviewScore(campo, review);

      addRow(campo.label(), score, valueText, row);
      row++;
    }

  }

  private void addRow(String fieldText, int reviewScore, String valueText, int row) {
    Label field = new Label(fieldText.toUpperCase());
    Label score = new Label(Integer.toString(reviewScore));
    score.getStyleClass().add("review-key-score");

    HBox keyBox = new HBox(field, score);
    keyBox.getStyleClass().add("review-key");

    Label value = new Label(valueText);
    value.getStyleClass().add("review-value");

    reviewContainer.add(keyBox, 0, row);
    reviewContainer.add(value, 1, row);
  }

  private String getReviewField(CampoValutazione campo, Valutazione review) {
    return switch (campo) {
      case STILE -> review.getRecensioneStile();
      case CONTENUTO -> review.getRecensioneContenuto();
      case GRADEVOLEZZA ->  review.getRecensioneGradevolezza();
      case ORIGINALITA ->  review.getRecensioneOriginalita();
      case EDIZIONE -> review.getRecensioneEdizione();
      case GENERALE -> review.getRecensioneGenerale();
    };
  }

  private double getReviewScore(CampoValutazione campo, Valutazione review) {
    return switch (campo) {
      case STILE -> review.getStile();
      case CONTENUTO -> review.getContenuto();
      case GRADEVOLEZZA -> review.getGradevolezza();
      case ORIGINALITA -> review.getOriginalita();
      case EDIZIONE -> review.getEdizione();
      case GENERALE -> review.getVotoFinale();
    };
  }

  private static void hide(Region r) {
    r.setVisible(false);
    r.setManaged(false);
  }

  private static void show(Region r) {
    r.setVisible(true);
    r.setManaged(true);
  }
}