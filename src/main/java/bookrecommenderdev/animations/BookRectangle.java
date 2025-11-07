package bookrecommenderdev.animations;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BookRectangle {
  AnimatedProperty posX;
  AnimatedProperty posY; // center position for simplicity
  AnimatedProperty width, height;
  AnimatedProperty convexR, concaveR, extendW, extendH;
  AnimatedProperty rotation; // degrees
  Color color;


  // For animation
  public BookRectangle(double x, double y, double width, double height, double convexR, double concaveR, double extW, double extH, Color color) {
    this.posX = new AnimatedProperty(x);
    this.posY = new AnimatedProperty(y);
    this.width = new AnimatedProperty(width);
    this.height = new AnimatedProperty(height);
    this.convexR = new AnimatedProperty(convexR);
    this.concaveR = new AnimatedProperty(concaveR);
    this.extendW = new AnimatedProperty(extW);
    this.extendH = new AnimatedProperty(extH);
    this.rotation = new AnimatedProperty(0);
    this.color = color;
  }

  public double baseWidth() { return width.getBaseValue(); }
  public double baseHeight() { return height.getBaseValue(); }
  public double baseX() { return posX.getBaseValue(); }
  public double baseY() { return posY.getBaseValue(); }
  public double baseExtendW() { return extendW.getBaseValue(); }
  public double baseExtendH() { return extendH.getBaseValue(); }
  public double baseConcaveR() { return concaveR.getBaseValue(); }
  public double baseConvexR() { return convexR.getBaseValue(); }
  public double baseRotation() { return rotation.getBaseValue(); }

  public AnimatedProperty getWidth() { return width; }
  public AnimatedProperty getHeight() { return height; }
  public AnimatedProperty getX() { return posX; }
  public AnimatedProperty getY() { return posY; }
  public AnimatedProperty getExtendW() { return extendW; }
  public AnimatedProperty getExtendH() { return extendH; }
  public AnimatedProperty getConcaveR() { return concaveR; }
  public AnimatedProperty getConvexR() { return convexR; }
  public AnimatedProperty getRotation() { return rotation; }

  public void draw(GraphicsContext gc, double currentTime) {
    gc.save();

    double w = width.getValue(currentTime);
    double h = height.getValue(currentTime);
    double extH = extendH.getValue(currentTime);
    double extW = Math.min(extendW.getValue(currentTime), w);
    double convR = Math.min(convexR.getValue(currentTime), w);
    double concR = Math.min(concaveR.getValue(currentTime), w);
//    double tmp = convR;
//    convR = Math.min(convR, w - concR);
//    concR = Math.min(concR, w - tmp);


    gc.translate(posX.getValue(currentTime) - w/2, posY.getValue(currentTime) - h/2);
//    gc.rotate(rotation);

    gc.setFill(color);

    // Path drawing
    gc.beginPath();

    gc.moveTo(0, 0);
    gc.lineTo(w - convR, 0);

    gc.quadraticCurveTo(w, 0, w, h/2);
    gc.quadraticCurveTo(w, h, w - convR, h);

    gc.lineTo(0, h);

    gc.lineTo(0, h - extH);
    gc.lineTo(extW, h - extH);

    gc.quadraticCurveTo(extW + concR, h - extH, extW + concR, h/2);
    gc.quadraticCurveTo(extW + concR, extH, extW, extH);

    gc.lineTo(0, extH);

    gc.closePath();
    gc.fill();

    gc.restore();
  }

}
