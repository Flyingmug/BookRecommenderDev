package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.RecommendationsController;
import bookrecommenderdev.client.controller.components.UserRecommendationsController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.ReviewsSectionController;
import bookrecommenderdev.client.controller.components.UserReviewSectionController;
import bookrecommenderdev.client.factory.StarIconFactory;
import bookrecommenderdev.model.base.CampoValutazione;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.route.Routable;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.model.dto.PaginaLibro;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;

import static bookrecommenderdev.model.utils.InputVerifiers.safeParseInt;

/**
 * Controller JavaFX responsabile della visualizzazione della pagina dei dettagli di un libro.
 *
 * <p> Coordina le funzioni di:
 * <ul>
 *   <li>Ottenimento e visualizzazione dei dati del libro tramite chiamata remota al server;</li>
 *   <li>Gestione della sezione recensioni e delle valutazioni aggregate;</li>
 *   <li>Componenti annidati (recensioni utente, raccomandazioni, banner di errore);</li>
 *   <li>Gestione di stati UI (contenuto visibile, assenza valutazioni, errori e not-found).</li>
 * </ul>
 * <p>
 * Nota: La gestione degli errori provenienti dal server avviene tramite un banner o reindirizzamento a pagina dedicata.
 */
public class BookController implements Routable {

  @FXML private ScrollPane bookPage;
  @FXML private VBox content;
  @FXML private Label titolo;
  @FXML private Label autori;
  @FXML private Label annoPubblicazione;
  @FXML private Label editore;
  @FXML private Label categorie;
  @FXML private TilePane scoresContainer;
  @FXML private VBox myReviewSection;

  @FXML private UserRecommendationsController userRecommendationsController;

  @FXML private VBox reviewsSection;
  @FXML private ReviewsSectionController reviewsSectionController;

  @FXML private UserReviewSectionController userReviewSectionController;

  @FXML private RecommendationsController recommendationsController;

  @FXML private ErrorBannerController errorBannerController;

  AppContext context;
  Integer idLibro;

  /** Flag che abilita la sezione di recensione dell’utente (dipende dallo stato di login e dalla libreria utente). */
  boolean userReviewVisible = false;

  /**
   * Metodo invocato dal sistema di routing quando la pagina viene caricata.
   *
   * <p>Recupera l'id del libro dai parametri di percorso, inizializza i componenti annidati con {@link AppContext}
   * e avvia il caricamento dei dati.
   *
   * <p>Verifica la correttezza dell'id fornito come parametro, eventualmente reindirizzando alla pagina {@code not-found}.
   *
   * @param params  parametri di percorso (atteso: {@code "id"} del libro)
   * @param context contesto applicativo client
   * @param state   eventuale stato aggiuntivo della navigazione (non utilizzato)
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;

    idLibro = (safeParseInt(params.get("id")));
    if (idLibro == null || idLibro <= 0) {
      Router.go("/not-found", TransitionAnimation.LEFT_SLIDE);
      return;
    }

    userReviewSectionController.setContext(context, idLibro);
    userRecommendationsController.setContext(context, idLibro);
    recommendationsController.setContext(context, idLibro);

    loadBookPage(idLibro);
  }

  /**
   * Qui vengono configurati handler UI non dipendenti dai parametri di percorso.
   */
  @FXML
  public void initialize() {

    setHandleLayoutChange();
  }

  /**
   * Ottiene i dati relativi a un libro tramite chiamata remota e aggiorna la pagina con le informazioni ricevute.
   *
   * <p>Gestisce inoltre:
   * <ul>
   *   <li>Abilitazione sezione “la mia recensione” (solo se autenticato e libro presente in libreria utente);</li>
   *   <li>Visualizzazione delle valutazioni aggregate (stelle + media generale + campi specifici);</li>
   *   <li>Contenuto UI per assenza di valutazioni;</li>
   *   <li>Routing verso pagina not-found o visualizzazione banner di errore.</li>
   * </ul>
   *
   * @param idLibro id del libro selezionato
   */
  protected void loadBookPage(int idLibro) {
    try {
      PaginaLibro pagina = context.server().getLibroCompleto(idLibro);

      setContentVisible(true);

      Libro l = pagina.getLibro();

      setTextValue(titolo, l.getTitolo());
      setTextValue(autori, l.getAutori());
      setTextValue(annoPubblicazione, Integer.toString(l.getAnnoPubblicazione()));
      setTextValue(editore, l.getEditore());
      setTextValue(categorie, l.getCategorie());

      if (AuthContext.isAuthenticated()) {
        int userId = AuthContext.getUser().idUtente();
        userReviewVisible = context.server().isLibroInLibrerieUtente(userId, idLibro);
      }

      myReviewSection.setVisible(userReviewVisible);
      myReviewSection.setManaged(userReviewVisible);

      double[] scores = pagina.getValutazioniAggregate();
      if (scoresPresent(pagina.getValutazioniAggregate())) {
        showReviews();  // mostra la sezione delle recensioni
        showScores(scores); // mostra le medie delle valutazioni
      } else {

        scoresContainer.getChildren().add(
            LabelCustomizer.createLabel(
                "Nessuna valutazione presente",
                Size.LG,
                Color.BLACK
            )
        );
        hideReviews();
      }

    } catch (NotFoundException e) {
      Platform.runLater(() -> Router.go("/not-found", TransitionAnimation.LEFT_SLIDE));

    } catch (DataAccessException e) {
      setContentVisible(false);
      content.setVisible(false);
      content.setManaged(false);

      errorBannerController.show(
          "Servizio dati non disponibile (errore database). Riprova tra poco.",
          () -> loadBookPage(idLibro),
          () -> Router.go("/")
      );

    } catch (RemoteException e) {
      content.setVisible(false);
      content.setManaged(false);

      errorBannerController.show(
          "Server non raggiungibile. Verifica la connessione e riprova.",
          () -> loadBookPage(idLibro),
          () -> Router.go("/")
      );
    }
  }

  /**
   * Verifica la presenza e la coerenza delle valutazioni aggregate rispetto ai campi definiti in {@link CampoValutazione}.
   *
   * <p>Il vettore delle medie aggregate atteso contiene un valore per ogni campo specifico escluso il campo GENERALE
   * (che viene calcolato come media dei campi disponibili).
   *
   * @param scores vettore delle valutazioni aggregate
   * @return {@code true} se il vettore è non nullo e ha la dimensione attesa; {@code false} altrimenti
   */
  private boolean scoresPresent(double[] scores) {
    return scores != null && scores.length == CampoValutazione.values().length - 1;
  }

  /**
   * Calcola e visualizza nella UI le valutazioni aggregate del libro.
   *
   * <p>Viene calcolata una media “generale” come media dei campi con valore &gt; 0 e viene mostrata insieme alle
   * valutazioni per ciascun {@link CampoValutazione} specifico, rappresentate tramite stelle.
   *
   * @param valutazioni vettore contenente le valutazioni aggregate (un valore per ogni campo specifico)
   */
  private void showScores(double[] valutazioni) {

    double totalScore = Arrays.stream(valutazioni)
        .filter(v -> v > 0)
        .average()
        .orElse(0.0);

    scoresContainer.getChildren().add(
        buildScoreItem(CampoValutazione.GENERALE.label(), totalScore)
    );

    CampoValutazione[] campi = CampoValutazione.values();
    for (int i = 1; i < campi.length; i++) {
      scoresContainer.getChildren().add(
          buildScoreItem(campi[i].label(), valutazioni[i - 1])
      );
    }
  }

  /**
   * Costruisce il blocco UI che rappresenta un singolo punteggio:
   * <ul>
   *   <li>Nome del campo in alto;</li>
   *   <li>Valore numerico formattato;</li>
   *   <li>Rappresentazione grafica tramite stelle.</li>
   * </ul>
   *
   * @param name  nome del campo
   * @param score punteggio
   * @return un {@link VBox} contenente la rappresentazione
   */
  private VBox buildScoreItem(String name, double score) {
    HBox header = new HBox(
        LabelCustomizer.createLabel(name.toUpperCase(), Size.SM, FontWeight.MEDIUM, Color.BLACK),
        LabelCustomizer.createLabel(String.format("%.1f", score), Size.MD, Color.BLACK)
    );
    header.setSpacing(3);
    header.setPadding(new Insets(0, 0, 0, 5));
    header.setAlignment(Pos.BASELINE_LEFT);
    header.setCache(false);

    return new VBox(
        header,
        StarIconFactory.buildStars(score)
    );
  }

  /**
   * Registra un handler per gestire cambiamenti di layout.
   *
   * <p>Alcune operazioni (es. espansioni/collassi o aggiornamenti del layout) possono far “saltare” la posizione di scroll
   * dello {@link ScrollPane}. Questo handler preserva il valore di {@code vValue} ripristinando
   * la posizione tramite {@link Platform#runLater(Runnable)}.
   */
  private void setHandleLayoutChange() {

    if (userReviewSectionController != null) {
      userReviewSectionController.setOnLayoutChange(() -> {
        double v = bookPage.getVvalue();

        bookPage.applyCss();
        bookPage.layout();

        Platform.runLater(() -> bookPage.setVvalue(v));
      });
    }
  }

  /**
   * Imposta il testo di una {@link Label}, sostituendo valori nulli o vuoti con un placeholder.
   *
   * @param label label interessata
   * @param text  valore testuale
   */
  private void setTextValue(Label label, String text) {
    label.setText(text == null || text.isBlank() ? "Sconosciuto" : text);
  }

  /**
   * Mostra e inizializza la sezione recensioni del libro corrente delegando al {@link ReviewsSectionController}.
   * <p>
   * L’effettivo caricamento delle recensioni è gestito dal componente dedicato.
   */
  private void showReviews() {
    // caricamento delle review
    reviewsSectionController.initializeForBook(idLibro, context);
  }

  /**
   * Nasconde la sezione recensioni.
   */
  private void hideReviews() {
    reviewsSection.setVisible(false);
    reviewsSection.setManaged(false);
  }

  /**
   * Imposta la visibilità della sezione principale della pagina.
   *
   * @param b visibilità della pagina
   */
  private void setContentVisible(boolean b) {
    content.setVisible(b);
    content.setManaged(b);
  }

}
