package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.base.Libreria;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller JavaFX per la visualizzazione di una libreria in lista.
 * <p>
 * Mostra il nome della libreria, il numero totale di libri contenuti
 * e consente la navigazione alla pagina di dettaglio.
 */
public class LibraryItemController {

  @FXML private Button libraryLink;
  @FXML private Label itemTitleLabel;
  @FXML private Label itemCountLabel;

  /**
   * Imposta i dati della libreria visualizzata e l’azione di apertura.
   *
   * @param lib        libreria da mostrare
   * @param totalCount numero totale di libri contenuti
   * @param onClick    azione da eseguire al click della libreria
   */
  public void setLibrary(Libreria lib, int totalCount, Runnable onClick) {

    if (lib != null) {
      libraryLink.setOnAction(_ -> onClick.run());
      itemTitleLabel.setText(lib.getNome());
      itemCountLabel.setText(Integer.toString(totalCount));
    }
  }
}