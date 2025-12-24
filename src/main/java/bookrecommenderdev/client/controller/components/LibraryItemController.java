package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.Libreria;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LibraryItemController {

  @FXML private Label itemTitleLabel;
  @FXML private Label itemCountLabel;

  public void setLibrary(Libreria lib, int totalCount) {

    System.out.println("Building library item...");

    if (lib != null) {

      itemTitleLabel.setText(lib.getNome());
      itemCountLabel.setText(""+totalCount);

    }

  }
}
