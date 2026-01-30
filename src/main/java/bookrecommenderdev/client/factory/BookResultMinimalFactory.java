package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.BookResultMinimalController;
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
 * Factory responsabile della creazione della variante “minimale” del "Libro Risultato.
 *
 * <p>Questa factory incapsula il caricamento dell’FXML e l’inizializzazione del controller
 * ({@link BookResultMinimalController}), fornendo un componente compatto utile in contesti
 * dove serve una rappresentazione ridotta del libro (liste dense, sidebar, ecc.).</p>
 *
 * <p>In caso di errore di caricamento, viene restituito un nodo di fallback contenente
 * un messaggio d’errore.</p>
 */
public class BookResultMinimalFactory {

  /**
   * Carica un nuovo item grafico per rappresentare un libro nei risultati.
   *
   * <p>Il metodo carica l’FXML, recupera il controller e imposta:</p>
   * <ul>
   *   <li>il {@link Libro} da visualizzare</li>
   *   <li>l’handler di click (se previsto)</li>
   *   <li>l'icona del pulsante (se previsto)</li>
   * </ul>
   *
   * <p><b>Gestione errori:</b> se il caricamento FXML fallisce, viene restituito
   * un nodo di fallback con messaggio.</p>
   *
   * @param l oggetto relativo all'elemento grafico
   * @param onAction handler di click
   * @return nodo radice del componente caricato (oppure un fallback in caso di errore)
   */
  public static Parent create(
      Libro l,
      Consumer<Integer> onAction,
      String actionIconLiteral
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/book-result-minimal.fxml")
      );
      Parent node = loader.load();
      BookResultMinimalController controller = loader.getController();

      controller.setBook(l);

      if (onAction != null) {
        controller.enableActionButton(actionIconLiteral, onAction);
      }
      return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Impossibile caricare", Size.SM, Color.RED)
      );
      return node;
    }
  }

}
