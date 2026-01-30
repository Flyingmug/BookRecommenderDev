package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.controls.ConfirmActionDialogController;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Factory responsabile della creazione del componente grafico "Dialog" generico.
 *
 * <p>Centralizza il caricamento dell’FXML e l’inizializzazione del relativo
 * controller ({@link ConfirmActionDialogController}), fornendo un punto unico e consistente
 * per costruire l’elemento.</p>
 */
public class ConfirmActionDialogFactory {

  /**
   * Crea un dialog di conferma associando le azioni di conferma e annullamento.
   *
   * <p>Il dialog creato invoca:</p>
   * <ul>
   *   <li>{@code onConfirm} quando l’utente conferma l’azione</li>
   *   <li>{@code onCancel} quando l’utente annulla o il dialog perde focus</li>
   * </ul>
   *
   * <p><b>Gestione errori:</b> se il caricamento FXML fallisce, viene restituito
   * un nodo di fallback con messaggio.</p>
   *
   * @param onConfirm azione da eseguire alla conferma
   * @return nodo radice del dialog, oppure un nodo di fallback in caso di errore
   */
  public static Parent create(
      Runnable onConfirm,
      Runnable onCancel
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/controls/confirm-action-dialog.fxml")
      );
      Parent node = loader.load();
      ConfirmActionDialogController controller = loader.getController();

      controller.setOnConfirm(onConfirm);
      controller.setOnCancel(onCancel);
      return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Impossibile creare il dialog", Size.SM, Color.RED)
      );
      return node;
    }
  }

}
