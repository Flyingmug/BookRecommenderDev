package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.LibraryItemController;
import bookrecommenderdev.model.base.Libreria;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Factory responsabile della creazione del componente grafico “Library Item”.
 *
 * <p>Centralizza il caricamento dell’FXML e l’inizializzazione del relativo
 * controller ({@link LibraryItemController}), fornendo un punto unico e consistente
 * per costruire l’elemento.</p>
 */
public class LibraryItemFactory {

  /**
   * Crea un elemento grafico di una libreria.
   *
   * <p>All'elemento viene associato:</p>
   * <ul>
   *  <li>il conteggio dei libri contenuti</li>
   *  <li>l'handler di click</li>
   * </ul>
   *
   * <p><b>Gestione errori:</b> se il caricamento FXML fallisce, viene restituito
   * un nodo di fallback con messaggio.</p>
   *
   * @param lib libreria da rappresentare
   * @param totalCount conteggio totale dei libri in una libreria
   * @param onClick callback eseguita quando l’utente seleziona l’elemento
   * @return nodo radice del componente caricato, oppure un nodo di fallback in caso di errore
   */
  public static Parent create(Libreria lib, int totalCount, Runnable onClick) {

    try {
      FXMLLoader loader = new FXMLLoader(
          ReviewItemFactory.class.getResource("/bookrecommenderdev/client/components/library-item.fxml")
      );
      Parent node = loader.load();
      LibraryItemController controller = loader.getController();

      controller.setLibrary(lib, totalCount, onClick);
      return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Unable to load content", Size.SM, Color.RED)
      );
      return node;
    }

  }

}
