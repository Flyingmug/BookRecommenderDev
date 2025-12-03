package bookrecommenderdev.model;

public record BookParams(
    int thickness,
    int extendH, int extendW,
    int convexR, int concaveR,
    int startAngle,
    int width, int height
) {}