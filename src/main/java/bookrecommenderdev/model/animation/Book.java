package bookrecommenderdev.model.animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Book {

  Group book;
  SequentialTransition coverAnimation;
  SequentialTransition pagesAnimation;

  public Book(double bookThickness,
              double extendH, double extendW,
              double convexR, double concaveR,
              double startAngle, double bookWidth, double bookHeight) {
    createPathGroup(bookThickness, extendH, extendW, convexR, concaveR, startAngle, bookWidth, bookHeight);

  }

  public Group getGroup() { return book; }

  private void createPathGroup(double bookThickness,
                               double extendH, double extendW,
                               double convexR, double concaveR,
                               double startAngle, double bookWidth, double bookHeight) {
    // Sizes evaluation
    double projWidth = getWidthVertProjection(bookWidth, startAngle); // proiezione della base sul piano
    double projHeight = getHeightVertProjection(bookHeight, startAngle); // proiezione dell'altezza sul piano
    double width = projWidth + projHeight;

    // Input clamp
    double extW = Math.min(extendW, width);
    double convR = Math.min(convexR, width);
    double concR = Math.min(concaveR, width);

    // "Cover" path definition
    LineTo line0 = new LineTo(width - convR, 0);
    QuadCurveTo q0 = new QuadCurveTo(width, 0, width, bookThickness / 2);
    QuadCurveTo q1 = new QuadCurveTo(width, bookThickness, width - convR, bookThickness);
    LineTo line1 = new LineTo(0, bookThickness);
    LineTo line2 = new LineTo(0, bookThickness - extendH);
    LineTo line3 = new LineTo(extW, bookThickness - extendH);
    QuadCurveTo q2 = new QuadCurveTo(extW + concR, bookThickness - extendH, extW + concR, bookThickness / 2);
    QuadCurveTo q3 = new QuadCurveTo(extW + concR, extendH, extW, extendH);
    LineTo line4 = new LineTo(0, extendH);

    Path path1 = new Path(new MoveTo(0, 0), line0, q0, q1, line1, line2, line3, q2, q3, line4, new ClosePath());
    path1.setStrokeWidth(0);
    path1.setFill(Color.TURQUOISE);

    // keyframes
    List<Timeline> keyframes = new LinkedList<>();

    double t1 = 0; // intermediate keyframe time instant
    double duration = 1; // animation duration
    double vertAngle = getVerticalDiagonalAngle(bookWidth, bookHeight);

    if(startAngle > vertAngle) {
      double maxDiagonalProj = Math.hypot(bookWidth, bookHeight);
      List<KeyValue> keyValues = new ArrayList<>();

      // Cover Keyframe 1
      addXAnimation(keyValues, line0, maxDiagonalProj - convR);
      addCurveXAnimation(keyValues, q0, maxDiagonalProj, maxDiagonalProj);
      addCurveXAnimation(keyValues, q1, maxDiagonalProj, maxDiagonalProj - convR);

      t1 = getIntermediateTime(duration, bookWidth, bookHeight, startAngle, vertAngle);
      keyframes.add(new Timeline(
          new KeyFrame(Duration.seconds(t1), keyValues.toArray(KeyValue[]::new))
      ));
    }

    // Cover Keyframe 2
    List<KeyValue> keyValues = new ArrayList<>();

    addXAnimation(keyValues, line0, bookHeight);
    addCurveXAnimation(keyValues, q0, bookHeight, bookHeight);
    addCurveXAnimation(keyValues, q1, bookHeight, bookHeight);
    addXAnimation(keyValues, line3, 0);
    addCurveXAnimation(keyValues, q2, 0, 0);
    addCurveXAnimation(keyValues, q3, 0, 0);

    keyframes.add(new Timeline(
        new KeyFrame(Duration.seconds(duration - t1), keyValues.toArray(KeyValue[]::new))
    ));

    coverAnimation =
        new SequentialTransition(keyframes.toArray(Timeline[]::new));
    coverAnimation.setCycleCount(-1);
    coverAnimation.setAutoReverse(true);


    //
    //
    //
    //
    //
    // "Pages" path definition
    LineTo line5 = new LineTo(projWidth - convR, extendH);
    QuadCurveTo q4 = new QuadCurveTo(projWidth - extendH, extendH, projWidth - extendH, bookThickness / 2);
    QuadCurveTo q5 = new QuadCurveTo(projWidth - extendH, bookThickness - extendH, projWidth - convR, bookThickness - extendH);
    LineTo line6 = new LineTo(extW, bookThickness - extendH);
    QuadCurveTo q6 = new QuadCurveTo(extW + concR, bookThickness - extendH, extW + concR, bookThickness / 2);
    QuadCurveTo q7 = new QuadCurveTo(extW + concR, extendH, extW, extendH);

    Path path2 = new Path(
        new MoveTo(extW, extendH),
        line5, q4, q5, line6, q6, q7,
        new ClosePath()
    );
    path2.setStrokeWidth(0);
    path2.setFill(Color.IVORY);

    // keyframes
    List<Timeline> keyframesPages = new LinkedList<>();

    double t12 = 0; // intermediate keyframe time instant
    double vertAngle2 = getVerticalDiagonalAngle(bookWidth, bookHeight);

    if(startAngle > vertAngle2) {
//      double maxDiagonalProj = Math.hypot(bookWidth, bookHeight); // fixme check if needed  at all
      double hProjWidthKEYFRAME = getWidthVertProjection(bookWidth, vertAngle2);
      List<KeyValue> keyValuesPages = new ArrayList<>();

      // Cover Keyframe 1
      addXAnimation(keyValuesPages, line5, extW + hProjWidthKEYFRAME);
      addCurveXAnimation(keyValuesPages, q4, extW + hProjWidthKEYFRAME + convR, extW + hProjWidthKEYFRAME + convR);
      addCurveXAnimation(keyValuesPages, q5, extW + hProjWidthKEYFRAME + convR, extW + hProjWidthKEYFRAME);

      t12 = getIntermediateTime(duration, bookWidth, bookHeight, startAngle, vertAngle2);
      keyframesPages.add(new Timeline(
          new KeyFrame(Duration.seconds(t12), keyValuesPages.toArray(KeyValue[]::new))
      ));
    }

    // Cover Keyframe 2
    List<KeyValue> keyValuesPages = new ArrayList<>();

    addXAnimation(keyValuesPages, line5, 0);
    addCurveXAnimation(keyValuesPages, q4, 0, 0);
    addCurveXAnimation(keyValuesPages, q5, 0, 0);
    addXAnimation(keyValuesPages, line6, 0);
    addCurveXAnimation(keyValuesPages, q6, 0, 0);
    addCurveXAnimation(keyValuesPages, q7, 0, 0);

    keyframesPages.add(new Timeline(
        new KeyFrame(Duration.seconds(duration - t12), keyValuesPages.toArray(KeyValue[]::new))
    ));

    pagesAnimation =
        new SequentialTransition(keyframesPages.toArray(Timeline[]::new));
    pagesAnimation.setCycleCount(-1);
    pagesAnimation.setAutoReverse(true);

    book = new Group(path1, path2);
  }

  private double getVerticalDiagonalAngle(double base, double height) {
    return Math.toDegrees(Math.atan(base/height));
  }

  private double getIntermediateTime(double finalTime, double base, double height, double startAngle, double vertAngle) {
    return finalTime *  (1 - vertAngle/startAngle);
  }

  private void addXAnimation(List<KeyValue> kvs, LineTo line, double targetX) {
    kvs.add(new KeyValue(line.xProperty(), targetX));
  }

  private void addCurveXAnimation(List<KeyValue> kvs, QuadCurveTo curve, double targetControlX, double targetX) {
    kvs.add(new KeyValue(curve.controlXProperty(), targetControlX));
    kvs.add(new KeyValue(curve.xProperty(), targetX));
  }

  /**
   * Calcola la proiezione verticale dell'altezza di un rettangolo
   * di altezza {@code height} ruotato secondo il parametro {@code angleDeg}.
   * @param height Altezza del rettangolo
   * @param angleDeg Angolo di rotazione
   * @return Valore risultante
   */
  private double getHeightVertProjection(double height, double angleDeg) {
    double phi = Math.toRadians(angleDeg);
    double c = Math.cos(phi);

    BigDecimal verticalProjHeight = BigDecimal.valueOf(Math.abs(height * c)).setScale(3, RoundingMode.HALF_EVEN);

    return verticalProjHeight.doubleValue();
  }

  /**
   * Calcola la proiezione verticale dell'altezza di un rettangolo
   * di altezza {@code width} ruotato secondo il parametro {@code angleDeg}.
   * @param width Altezza del rettangolo
   * @param angleDeg Angolo di rotazione
   * @return Valore risultante
   */
  private double getWidthVertProjection(double width, double angleDeg) {
    double phi = Math.toRadians(angleDeg);
    double s = Math.sin(phi);

    BigDecimal verticalProjWidth = BigDecimal.valueOf(Math.abs(width * s)).setScale(3, RoundingMode.HALF_EVEN);

    return verticalProjWidth.doubleValue();
  }

  public void startAnimation() {
    coverAnimation.play();
    pagesAnimation.play();
  }
}
