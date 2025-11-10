package bookrecommenderdev.client.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class LoadingController {


  @FXML public Group animationGroup;

  @FXML
  public void initialize() {
//    Group root = new Group();

    // === Create 4 groups, each with 2 paths ===
    for (int i = 0; i < 1; i++) {
      Group group = createPathGroup(300, 100, 5, 10, 25, 15);
      group.setTranslateY(150 + i * 160);

      // Animate the entire group (movement + rotation)
//      Timeline groupTimeline = new Timeline(
//          new KeyFrame(Duration.seconds(0),
//              new KeyValue(group.translateYProperty(), 150)
//          ),
//          new KeyFrame(Duration.seconds(2/* + i * 0.5*/), // stagger slightly
//              new KeyValue(group.translateYProperty(), 150 /*+ 50 * Math.sin(i * Math.PI / 2)*/)
//          )
//      );
//      groupTimeline.setCycleCount(Animation.INDEFINITE);
//      groupTimeline.setAutoReverse(true);
//      groupTimeline.play();

      animationGroup.getChildren().add(group);
    }

  }

  // === Helper: create a group with 2 related paths ===
  private Group createPathGroup(double width, double height, double extendH, double extendW, double convexR, double concaveR) {

    double extW = Math.min(extendW, width);
    double convR = Math.min(convexR, width);
    double concR = Math.min(concaveR, width);
//    double tmp = convR;
//    convR = Math.min(convR, w - concR);
//    concR = Math.min(concR, w - tmp);


    Path path1 = new Path(
        new MoveTo(0, 0),
        new LineTo(width - convR, 0),
        new QuadCurveTo(width, 0, width, height/2),
        new QuadCurveTo(width, height, width - convR, height),
        new LineTo(0, height),
        new LineTo(0, height - extendH),
        new LineTo(extW, height - extendH),
        new QuadCurveTo(extW + concR, height - extendH, extW + concR, height/2),
        new QuadCurveTo(extW + concR, extendH, extW, extendH),
        new LineTo(0, extendH),
        new ClosePath()
    );
    path1.setStrokeWidth(0);
    path1.setFill(Color.TURQUOISE);

    Path path2 = new Path(
        new MoveTo(extW, extendH),
        new LineTo(width - convR, extendH),
        new QuadCurveTo(width - extendH, extendH, width - extendH, height/2),
        new QuadCurveTo(width - extendH, height - extendH, width - convR, height - extendH),

        new LineTo(extW, height - extendH),
        new QuadCurveTo(extW + concR, height - extendH, extW + concR, height/2),
        new QuadCurveTo(extW + concR, extendH, extW, extendH)
    );
    path2.setStrokeWidth(0);
    path2.setFill(Color.IVORY);

    // Animate path2's curvature
//    QuadCurveTo curve2 = (QuadCurveTo) path2.getElements().get(2);
//    Timeline pathTimeline = new Timeline(
//        new KeyFrame(Duration.seconds(0),
//            new KeyValue(curve2.controlYProperty(), 40),
//            new KeyValue(path2.strokeProperty(), path2.getStroke())
//        ),
//        new KeyFrame(Duration.seconds(2),
//            new KeyValue(curve2.controlYProperty(), -40),
//            new KeyValue(path2.strokeProperty(), Color.WHITE)
//        )
//    );
//    pathTimeline.setCycleCount(Animation.INDEFINITE);
//    pathTimeline.setAutoReverse(true);
//    pathTimeline.play();

    return new Group(path1, path2);

    /*
    * double width = vals.get("vertical_projection_width");
    double hyp = Math.hypot(width, vals.get("vertical_projection_height"));
    cover.getWidth().addKeyframe(time, hyp);
    pages.getWidth().addKeyframe(time, width);
    * */
  }

  public static Map<String, Double> getVerticalProjections(double width, double height, double angleDeg) {
    double phi = Math.toRadians(angleDeg);
    double c = Math.cos(phi), s = Math.sin(phi);

    BigDecimal verticalProjWidth = BigDecimal.valueOf(Math.abs(width * s)).setScale(3, RoundingMode.HALF_EVEN);
    BigDecimal verticalProjHeight = BigDecimal.valueOf(Math.abs(height * c)).setScale(3, RoundingMode.HALF_EVEN);

    Map<String, Double> result = new HashMap<>();
    result.put("vertical_projection_width", verticalProjWidth.doubleValue());
    result.put("vertical_projection_height", verticalProjHeight.doubleValue());
    return result;
  }
}
