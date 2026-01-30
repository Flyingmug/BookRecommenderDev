package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.dto.LibroConsigliato;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

/**
 * Controller JavaFX per la visualizzazione di un libro consigliato.
 * <p>
 * Mostra le informazioni principali del libro e il numero di volte
 * in cui è stato consigliato dagli utenti.
 */
public class RecommendedItemController {

  @FXML private Button linkButton;
  @FXML private Label recommendCountLabel;
  @FXML private Label titleLabel;
  @FXML private Label authorLabel;
  @FXML private Label yearLabel;

  /**
   * Imposta i dati del libro consigliato e l’azione di apertura.
   *
   * @param cons        DTO contenente il libro e il conteggio dei consigli
   * @param onClickOpen callback invocata con l’id del libro (può essere {@code null})
   */
  public void setItem(LibroConsigliato cons, Consumer<Integer> onClickOpen) {

    Libro l = cons.libro();
    int count = cons.countConsigliato();

    titleLabel.setText(l.getTitolo());
    authorLabel.setText(l.getAutori());

    yearLabel.setText(l.getAnnoPubblicazione() > 0
        ? Integer.toString(l.getAnnoPubblicazione())
        : "");

    recommendCountLabel.setText(Integer.toString(count));

    linkButton.setOnMouseClicked(
        onClickOpen == null
            ? _ -> {}
            : _ -> onClickOpen.accept(l.getIdLibro())
    );
  }
}
