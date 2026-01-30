package bookrecommenderdev.client.routing.animation;

import bookrecommenderdev.client.routing.Router;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Gestisce tutte le animazioni utilizzate dal {@link Router} nelle transizioni di percorsi.
 *
 * <p>Ogni animazione accetta opzionalmente una callback di completamento,
 *  * utilizzata tipicamente per sbloccare la navigazione al termine
 *  * della transizione.</p>
 */
public class Animations {

  /**
   * Gestisce una transizione 'a scomparsa' tra due pagine riferite.
   * I parametri richiedono l'elemento contenitore e il nuovo nodo.
   * @param rootNode Elemento radice contenente il nodo corrente
   * @param newNode Nuovo elemento
   * @param duration Durata del'animazione
   */
  public static void fadeTransition(Pane rootNode, Node newNode, Duration duration, Runnable onFinished) {
    Node oldNode = rootNode.getChildren().isEmpty()
        ? null
        : rootNode.getChildren().getFirst();

    newNode.setOpacity(0);
    rootNode.getChildren().add(newNode);

    // Se il nodo precedente è nullo, esegue solo la transizione di fade-in
    if (oldNode == null) {
      FadeTransition fadeIn = new FadeTransition(duration, newNode);
      fadeIn.setFromValue(0);
      fadeIn.setToValue(1);
      fadeIn.play();
      return;
    }

    FadeTransition fadeOut = new FadeTransition(duration, oldNode);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);

    FadeTransition fadeIn = new FadeTransition(duration, newNode);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    fadeOut.setOnFinished(_ -> {
      rootNode.getChildren().remove(oldNode);
      fadeIn.play();
      if (onFinished != null) onFinished.run();
    });

    fadeOut.play();
  }

  /**
   * Gestisce una transizione 'a scorrimento' tra due pagine riferite.
   * I parametri richiedono l'elemento contenitore e il nuovo nodo.
   * @param rootNode Elemento radice contenente il nodo corrente
   * @param newNode Nuovo elemento
   * @param direction Direzione di scorrimento
   * @param duration Durata del'animazione
   */
  public static void slideTransition(Pane rootNode, Node newNode, Direction direction, Duration duration, Runnable onFinished) {
    Node oldNode = rootNode.getChildren().isEmpty()
        ? null
        : rootNode.getChildren().getFirst();

    double width  = rootNode.getWidth();
    double height = rootNode.getHeight();

    // Fallback se il layout non è carico
    if (width <= 0 || height <= 0) {
      rootNode.applyCss();
      rootNode.layout();
      width  = rootNode.getWidth();
      height = rootNode.getHeight();
    }

    double newStartX = 0, newStartY = 0;
    double oldEndX   = 0, oldEndY   = 0;

    switch (direction) {
      case LEFT -> {
        newStartX = width;
        oldEndX   = -width;
      }
      case RIGHT -> {
        newStartX = -width;
        oldEndX   = width;
      }
      case TOP -> {
        newStartY = height;
        oldEndY   = -height;
      }
      case BOTTOM -> {
        newStartY = -height;
        oldEndY   = height;
      }
    }

    newNode.setTranslateX(newStartX);
    newNode.setTranslateY(newStartY);
    newNode.setMouseTransparent(true);

    rootNode.getChildren().add(newNode);

    TranslateTransition in = new TranslateTransition(duration, newNode);
    in.setToX(0);
    in.setToY(0);

    if (oldNode == null) {
      in.setOnFinished(_ -> newNode.setMouseTransparent(false));
      in.play();
      return;
    }

    TranslateTransition out = new TranslateTransition(duration, oldNode);
    out.setToX(oldEndX);
    out.setToY(oldEndY);

    out.setOnFinished(_ -> {
      rootNode.getChildren().remove(oldNode);
      newNode.setMouseTransparent(false);
      if (onFinished != null) onFinished.run();
    });

    in.play();
    out.play();
  }

  /**
   * Inverte la direzione di una transizione, se logicamente possibile.
   * @param transition Transizione
   * @return Transizione opposta
   */
  public static TransitionAnimation reverseTransition(TransitionAnimation transition) {
    return switch (transition) {
      case FADE_INTO -> TransitionAnimation.FADE_INTO;
      case TOP_SLIDE -> TransitionAnimation.BOTTOM_SLIDE;
      case RIGHT_SLIDE -> TransitionAnimation.LEFT_SLIDE;
      case BOTTOM_SLIDE -> TransitionAnimation.TOP_SLIDE;
      case LEFT_SLIDE -> TransitionAnimation.RIGHT_SLIDE;
      case DEFAULT -> TransitionAnimation.DEFAULT;
    };
  }

}
