package bookrecommenderdev.animations;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

public class CurvedRectangle {
  private Point2D position;  // center position for simplicity
  private double width, height;
  private double convexR, concaveR, sideExtend;
  private double rotation; // degrees

  // For animation
  private int phase = 0;
  private double phaseTime = 0;

  public CurvedRectangle(double x, double y, double width, double height) {
    this.position = new Point2D(x, y);
    this.width = width;
    this.height = height;
    this.convexR = 20;
    this.concaveR = 25;
    this.sideExtend = 10;
    this.rotation = 0;
  }

  public void update(double delta) {
    phaseTime += delta;

    switch (phase) {
      case 0 -> translatePhase(delta);
      case 1 -> rotatePhase(delta);
      case 2 -> morphPhase(delta);
    }
  }

  private void translatePhase(double delta) {
    Point2D start = new Point2D(100, 200);
    Point2D end = new Point2D(400, 200);
    double duration = 2.0;
    double t = Math.min(phaseTime / duration, 1.0);
    double newX = start.getX() + (end.getX() - start.getX()) * t;
    position = new Point2D(newX, start.getY());
    if (t >= 1.0) nextPhase();
  }

  private void rotatePhase(double delta) {
    double duration = 1.0;
    double t = Math.min(phaseTime / duration, 1.0);
    rotation = 90 * t;
    if (t >= 1.0) nextPhase();
  }

  private void morphPhase(double delta) {
    double duration = 2.0;
    double t = Math.min(phaseTime / duration, 1.0);
    // Simple easing
    t = t * t * (3 - 2 * t);

    // Morph curve radii and extension
    convexR = 20 + 10 * Math.sin(t * Math.PI);
    concaveR = 25 + 5 * Math.cos(t * Math.PI);
    sideExtend = 10 + 5 * Math.sin(t * Math.PI);

    if (t >= 1.0) nextPhase();
  }

  private void nextPhase() {
    phase++;
    phaseTime = 0;
  }

  public void draw(GraphicsContext gc) {
    gc.save();
    gc.translate(position.getX(), position.getY());
    gc.rotate(rotation);

    double hw = width / 2;
    double hh = height / 2;

    gc.setFill(Color.AQUAMARINE);
    gc.beginPath();

    // Start top-left (at convex curve start)
    gc.moveTo(-hw + convexR, -hh);

    // Top edge → extend slightly on right
    gc.lineTo(hw + sideExtend, -hh);

    // Concave side → inward curve
    gc.quadraticCurveTo(hw + concaveR, 0, hw + sideExtend, hh);

    // Bottom edge → back to left
    gc.lineTo(-hw + convexR, hh);

    // Convex side → outward curve
    gc.quadraticCurveTo(-hw - convexR, 0, -hw + convexR, -hh);

    gc.closePath();
    gc.fill();
    gc.setStroke(Color.DARKBLUE);
    gc.stroke();

    gc.restore();
  }
}