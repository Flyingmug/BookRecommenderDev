package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.RatingFieldController;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * Controller JavaFX del form di inserimento recensione/valutazione per un libro.
 * <p>
 * Operazioni:
 * <ul>
 *   <li>Recupera il libro da recensire;</li>
 *   <li>Inizializza i campi dei punteggi;</li>
 *   <li>Invia la valutazione al server.</li>
 * </ul>
 */
public class ReviewFormController implements Routable {

  @FXML private Label titleLabel;
  @FXML private RatingFieldController generaleController;
  @FXML private RatingFieldController stileController;
  @FXML private RatingFieldController contenutoController;
  @FXML private RatingFieldController originalitaController;
  @FXML private RatingFieldController gradevolezzaController;
  @FXML private RatingFieldController edizioneController;
  @FXML private Label feedbackLabel;

  @FXML private ErrorBannerController errorBannerController;

  Libro libro;
  AppContext context;

  /**
   * Richiede autenticazione e carica il libro indicato dai parametri di percorso.
   * <p>
   * Se l'utente non è autenticato, viene reindirizzato alla pagina dedicata all'accesso.
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;

    String idLibro = params.get("id");

    if (!AuthContext.isAuthenticated()) {
      Platform.runLater(() -> Router.go("/login"));
      return;
    }

    fetchBook(idLibro);
  }

  /**
   * Ottiene il libro corrispondente all'id dato e aggiorna il titolo del form.
   *
   * @param idLibro Id libro
   */
  private void fetchBook(String idLibro) {
    try {
      libro = context.server().getLibro(Integer.parseInt(idLibro));
      setTitle(libro.getTitolo());
    } catch (NotFoundException e) {

      Platform.runLater(() -> Router.go("/not-found"));
    } catch (DataAccessException e) {

      showError("Errore database nel reperimento del titolo.");
    } catch (RemoteException e) {

      showError("Server non raggiungibile.");
    }

  }

  /**
   * Configura i campi dei punteggi.
   */
  @FXML
  private void initialize() {
    generaleController.setScoreBoxVisible(false);

    generaleController.setTitle("Generale");
    stileController.setTitle("Stile");
    contenutoController.setTitle("Contenuto");
    gradevolezzaController.setTitle("Gradevolezza");
    originalitaController.setTitle("Originalità");
    edizioneController.setTitle("Edizione");
  }

  /**
   * Handler UI per confermare e inviare la recensione.
   * <p>
   * Operazioni:
   * <ol>
   *   <li>Valida i campi;</li>
   *   <li>Invia la {@link Valutazione} al server;</li>
   *   <li>Reindirizza alla pagina del libro.</li>
   * </ol>
   */
  @FXML
  private void conferma() {
    errorBannerController.hide();

    Valutazione v = buildOrShowError();
    if (v == null) return;

    try {
      context.server().inserisciValutazioneLibro(v);
      javafx.application.Platform.runLater(() ->
          Router.go("/book/" + libro.getIdLibro())
      );

    } catch (DataAccessException e) {
      showError("Errore database. Riprova.");

    } catch (java.rmi.RemoteException e) {
      showError("Server non raggiungibile. Riprova.");

    }

  }

  /**
   * Costruisce l’oggetto {@link Valutazione} a partire dai campi compilati.
   * <p>
   * Se mancano voti obbligatori (1–5) o l'utente non è autenticato, mostra un feedback di errore
   * e ritorna {@code null}.
   *
   * @return valutazione costruita sui dati, oppure {@code null} in caso di errore/validazione fallita
   */
  private Valutazione buildOrShowError() {
    Integer stile = stileController.getScore();
    Integer contenuto = contenutoController.getScore();
    Integer gradevolezza = gradevolezzaController.getScore();
    Integer originalita = originalitaController.getScore();
    Integer edizione = edizioneController.getScore();

    if (stile == null || contenuto == null || gradevolezza == null || originalita == null || edizione == null) {
      setErrorFeedback("Seleziona un voto (1–5) per tutti i campi.");
      return null;
    }

    // Errore di fallback
    if (!AuthContext.isAuthenticated()) {
      showError("Devi effettuare l'accesso per inviare una recensione.");
      return null;
    }

    int userId = AuthContext.getUser().idUtente();

    return new Valutazione(
        libro.getIdLibro(),
        userId,
        stile, contenuto, gradevolezza, originalita, edizione,
        stileController.getTextReview(),
        contenutoController.getTextReview(),
        gradevolezzaController.getTextReview(),
        originalitaController.getTextReview(),
        edizioneController.getTextReview(),
        generaleController.getTextReview()
    );
  }

  /**
   * Imposta il titolo del form.
   *
   * @param title titolo del libro
   */
  private void setTitle(String title) {
    this.titleLabel.setText("Recensisci " + title);
  }

  /**
   * Mostra un messaggio di errore tramite {@link ErrorBannerController}.
   *
   * @param s testo dell’errore
   */
  private void showError(String s) {
    errorBannerController.show(s, null, null);
  }

  /**
   * Imposta il feedback testuale di validazione mostrato nella pagina.
   *
   * @param feedback messaggio da visualizzare
   */
  private void setErrorFeedback(String feedback) {
    this.feedbackLabel.setText(feedback);
  }
}
