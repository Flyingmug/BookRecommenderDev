package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.factory.ReviewItemFactory;
import bookrecommenderdev.model.Valutazione;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.auth.AuthContext;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class UserReviewSectionController {

  @FXML private Parent root;
  @FXML private VBox content;
  @FXML private VBox mainArea;
  @FXML private Button removeButton;
  @FXML private Label titleLabel;
  @FXML private Button reviewButton;

  private int idLibro;
  private AppContext context;
  Runnable onLayoutChange;

  public void setContext(AppContext context, int idLibro) {
    this.context = context;
    this.idLibro = idLibro;

    if (context == null || !AuthContext.isAuthenticated()) {
      System.out.println("context is null or user not authenticated.");
      hideSection();
      return; // todo behaviour needed at all?
    }

    try {
      Valutazione v = context.server()
          .getValutazione(idLibro, AuthContext.getUser().getId_utente());

      if (v != null) {
        // valutazione presente -> mostra valutazione
        // mostra pulsante di eliminazione recensione
        showExistingReview(v);
      } else {
        showCreateReview();
      }

      showSection();

    } catch (RemoteException e) {
      System.out.println("Errore nel reperimento della valutazione.");
      e.printStackTrace();
      hideSection();
    }
  }

  @FXML
  private void onReview() {
    Router.go("/review/" + idLibro);
  }

  @FXML
  private void onRemoveReview() {
    try {
      if (!AuthContext.isAuthenticated() || context == null) {
        System.out.println("ERR2 context is null or user not authenticated.");
        return;
      }

      boolean result = context.server().deleteValutazione(
          idLibro,
          AuthContext.getUser().getId_utente()
      );

      System.out.println(result ?
          "success: review removed" : "failure: db unaffected");

      // Refresh section
      showCreateReview();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }


  private void showExistingReview(Valutazione v) {
    mainArea.getChildren().setAll(
        ReviewItemFactory.createReviewNode(v)
    );

    removeButton.setVisible(true);
    removeButton.setManaged(true);
  }

  private void showCreateReview() {
    showIntro();
    removeButton.setVisible(false);
    removeButton.setManaged(false);

    notifyLayoutChange();
  }

  private void showSection() {
    root.setVisible(true);
    root.setManaged(true);
  }

  private void showIntro() {
    mainArea.getChildren().clear();
    mainArea.getChildren().addAll(
        titleLabel,
        reviewButton
    );
  }

  private void hideSection() {
    root.setVisible(false);
    root.setManaged(false);
  }

  /** Notifica un cambiamento di layout. */
  private void notifyLayoutChange() {
    if (onLayoutChange != null) {
      onLayoutChange.run();
    }
  }

  // Metodi Esposti

  /** Imposta un <i>callback</i> eseguito in seguito al cambiamento di layout.
   * @param r callback */
  public void setOnLayoutChange(Runnable r) {
    this.onLayoutChange = r;
  }


}
