package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.Libreria;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LibraryItemController {

  @FXML private Button libraryLink;
  @FXML private Label itemTitleLabel;
  @FXML private Label itemCountLabel;

  public void setLibrary(Libreria lib, int totalCount, Runnable onClick) {

    if (lib != null) {

      libraryLink.setOnAction(_ -> onClick.run());
      itemTitleLabel.setText(lib.getNome());
      itemCountLabel.setText(""+totalCount);

    }

  }
}
