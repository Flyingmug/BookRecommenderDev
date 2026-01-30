package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.components.controls.ConfirmActionDialogController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.ReviewItemFactory;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.auth.AuthContext;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

/**
 * Controller JavaFX della sezione “recensione dell’utente” nella pagina di dettaglio di un libro.
 * <p>
 * Se l’utente è autenticato, verifica l’eventuale presenza di una valutazione personale per il libro:
 * <ul>
 *   <li>Se presente, la mostra e abilita la rimozione tramite dialog di conferma;</li>
 *   <li>Se assente, mostra l’invito a creare una nuova recensione.</li>
 * </ul>
 * In caso di errore mostra un {@link ErrorBannerController} con opzione di riprova.
 */
public class UserReviewSectionController {

  @FXML private VBox content;
  @FXML private VBox mainArea;
  @FXML private Parent deleteControl;
  @FXML private ConfirmActionDialogController deleteControlController;
  @FXML private VBox createReviewSection;

  @FXML private ErrorBannerController errorBannerController;

  private int idLibro;
  private AppContext context;
  Runnable onLayoutChange;

  /**
   * Imposta il contesto e l’id del libro di riferimento, quindi aggiorna lo stato della sezione.
   *
   * @param context contesto applicativo client
   * @param idLibro id del libro
   */
  public void setContext(AppContext context, int idLibro) {
    this.context = context;
    this.idLibro = idLibro;

    refresh();
  }

  @FXML
  private void initialize() {

    // Configurazione del dialog di conferma
    if (deleteControlController != null) {
      deleteControlController.setOnConfirm(this::deleteReviewConfirmed);
      deleteControlController.setOnCancel(() -> {});
    }
  }

  /**Reindirizza al form di creazione di valutazione {@code /book/:id/review}*/
  @FXML
  private void onReview() {
    Router.go("/book/" + idLibro + "/review");
  }

  /**<p>Gestisce l'aggiornamento dello stato della pagina.
   * <p>Verifica lo stato di autenticazione, per poi eventualmente verificare
   * la presenza di una valutazione dell'utente per il libro corrente.
   * <p>Questa viene mostrata se esiste, altrimenti viene caricato il link al form di recensione.
   * <p>Notifica un cambiamento di layout.
   * <p>In caso di errore viene mostrato un banner con l'opzione di refresh della sezione. */
  private void refresh() {

    if (context == null || !AuthContext.isAuthenticated()) {
      hideContent();
      return;
    }

    try {
      int userId = AuthContext.getUser().idUtente();
      Valutazione v = context.server().getValutazione(idLibro, userId);

      if (v != null) showExistingReview(v);
      else showCreateReview();

      showContent();
      notifyLayoutChange();

    } catch (DataAccessException e) {
      hideContent();
      showError(
          "Errore nel reperimento della valutazione (DB).",
          this::refresh
      );

    } catch (RemoteException e) {
      hideContent();
      showError(
          "Errore di comunicazione con il server.",
          this::refresh
      );

    }
  }

  /**
   * Conferma l’eliminazione della recensione dell’utente e aggiorna la sezione.
   * <p>
   * In caso di errore (recensione non trovata, DB o comunicazione) mostra un banner con retry.
   */
  private void deleteReviewConfirmed() {
    if (deleteControlController != null) deleteControlController.setDisabled(false);

    try {
      if (context == null || !AuthContext.isAuthenticated()) return;

      context.server().deleteValutazione(idLibro, AuthContext.getUser().idUtente());
      refresh();

    } catch (NotFoundException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError(
          "Errore nell'identificare la valutazione.",
          this::refresh
      );

    } catch (DataAccessException | RemoteException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError(
          "Errore nel reperimento della valutazione (DB).",
          this::refresh
      );

    }
  }

  /**
   * Mostra la recensione esistente dell’utente e abilita il controllo di eliminazione.
   *
   * @param v valutazione da visualizzare
   */
  private void showExistingReview(Valutazione v) {

    mainArea.getChildren().setAll(
        ReviewItemFactory.create(v)
    );
    deleteControl.setVisible(true);
    deleteControl.setManaged(true);
  }

  /**
   * Mostra l’introduzione alla creazione recensione e nasconde il controllo di eliminazione.
   */
  private void showCreateReview() {
    showIntro();
    deleteControl.setVisible(false);
    deleteControl.setManaged(false);

    notifyLayoutChange();
  }

  /** Mostra la sezione introduttiva per la creazione della recensione. */
  private void showIntro() {
    mainArea.getChildren().clear();
    mainArea.getChildren().add(createReviewSection);
  }

  /** Rende visibile la sezione di contenuto principale nel layout. */
  private void showContent() {
    content.setVisible(true);
    content.setManaged(true);
  }

  /** Nasconde la sezione di contenuto principale dal layout. */
  private void hideContent() {
    content.setVisible(false);
    content.setManaged(false);
  }

  /** Notifica un cambiamento di layout al callback esterno, se presente. */
  private void notifyLayoutChange() {
    if (onLayoutChange != null) {
      onLayoutChange.run();
    }
  }

  /**
   * Mostra un banner d’errore.
   *
   * @param message messaggio da visualizzare
   * @param retry   azione di riprova (può essere {@code null})
   */
  private void showError(String message, Runnable retry) {
    errorBannerController.show(message, retry, null);
  }

  // Metodi Esposti

  /** Imposta un <i>callback</i> eseguito in seguito al cambiamento di layout.
   * @param r callback */
  public void setOnLayoutChange(Runnable r) {
    this.onLayoutChange = r;
  }
}
