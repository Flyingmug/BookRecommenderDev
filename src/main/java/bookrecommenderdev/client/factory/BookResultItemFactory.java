package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.BookResultItemController;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Factory responsabile della creazione del componente grafico "Libro Risultato".
 *
 * <p>Centralizza il caricamento dell’FXML e l’inizializzazione del relativo
 * controller ({@link BookResultItemController}), fornendo un punto unico e consistente
 * per costruire l’item da inserire in liste o risultati di ricerca.</p>
 */
public class BookResultItemFactory {

  /**
   * Carica un nuovo item grafico per rappresentare un libro nei risultati.
   *
   * <p>Il metodo carica l’FXML, recupera il controller e imposta:</p>
   * <ul>
   *   <li>il {@link Libro} da visualizzare</li>
   *   <li>l’handler di click (se previsto)</li>
   * </ul>
   *
   * <p><b>Gestione errori:</b> se il caricamento FXML fallisce, viene restituito
   * un nodo di fallback con messaggio.</p>
   *
   * @param l oggetto relativo all' elemento grafico
   * @param onClick handler di click
   * @return nodo radice del componente caricato (oppure un fallback in caso di errore)
   */
  public static Parent create(Libro l, Consumer<Integer> onClick) {
    return create(l, onClick, null, null, null);
  }

  /**
   * Carica un nuovo item grafico per rappresentare un libro nei risultati.
   *
   * <p>Il metodo carica l’FXML, recupera il controller e imposta:</p>
   * <ul>
   *   <li>il {@link Libro} da visualizzare</li>
   *   <li>l’handler di click (se previsto)</li>
   *   <li>l’handler di cancellazione/chiusura (se previsto)</li>
   *   <li>il testo del pulsante di azione (se previsto)</li>
   *   <li>l'icona del pulsante di azione (se previsto)</li>
   * </ul>
   *
   * <p><b>Gestione errori:</b> se il caricamento FXML fallisce, viene restituito
   * un nodo di fallback con messaggio.</p>
   *
   * @param l oggetto relativo all' elemento grafico
   * @param onClick handler di click
   * @param onAction handler di azione (un handler generico personalizzabile)
   * @param actionText testo visualizzato nell' handler di azione
   * @param actionIconLiteral letterale dell'icona di azione
   * @return nodo radice del componente caricato (oppure un fallback in caso di errore)
   */
  public static Parent create(
      Libro l,
      Consumer<Integer> onClick,
      Consumer<Integer> onAction,
      String actionText,
      String actionIconLiteral
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/book-result-item.fxml")
      );
      Parent node = loader.load();
      BookResultItemController controller = loader.getController();

      controller.setBook(l, onClick);

      if (onAction != null) {
        controller.enableActionButton(actionText, actionIconLiteral, onAction);
      }
      return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Impossibile caricare il contenuto", Size.SM, Color.RED)
      );
      return node;
    }
  }

}
