package bookrecommenderdev.client.controller.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.kordamp.ikonli.javafx.FontIcon;

import static bookrecommenderdev.Constants.MAX_REVIEW_LENGTH;
import static bookrecommenderdev.utils.InputVerifiers.preventMultipleSpacesAndLimit;

public class RatingFieldController {

  @FXML private Label titleLabel;
  @FXML private ComboBox<Integer> scoreBox;
  @FXML private Button toggleButton;
  @FXML private FontIcon toggleIcon;
  @FXML private TextArea textReviewArea;
  @FXML private Label charCountLabel;

  @FXML
  private void initialize() {
    scoreBox.getItems().addAll(1, 2, 3, 4, 5);

    toggleButton.setOnAction(_ -> toggleComment());

    preventMultipleSpacesAndLimit(textReviewArea, MAX_REVIEW_LENGTH);

    setCharCountListener();
  }

  private void setCharCountListener() {
    textReviewArea.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        charCountLabel.setText(newValue.length() + " / " + MAX_REVIEW_LENGTH);
      }
    });
  }

  private void toggleComment() {
    boolean show = !textReviewArea.isVisible();
    textReviewArea.setVisible(show);
    textReviewArea.setManaged(show);
    charCountLabel.setVisible(show);
    charCountLabel.setManaged(show);

    if (!show) {
      textReviewArea.setText(null);
    }

    toggleIcon.setIconLiteral(show ? "mdi2m-minus" : "mdi2p-plus");
  }

  /* API exposed to parent */

  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  public int getScore() {
    return scoreBox.getValue() == null ? -1 : scoreBox.getValue();
  }

  public String getTextReview() {
    return textReviewArea.getText();
  }

  public void setScoreBoxVisible(boolean visible) {
    scoreBox.setVisible(visible);
    scoreBox.setManaged(visible);
  }
}
