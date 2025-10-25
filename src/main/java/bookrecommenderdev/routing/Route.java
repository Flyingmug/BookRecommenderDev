package bookrecommenderdev.routing;

public class Route {
  private final String fxml;
  private final RouteGroup group;

  // Constructor with explicit group
  public Route(String fxml, RouteGroup group) {
    this.fxml = fxml;
    this.group = group;
  }

  // Constructor with default group
  public Route(String fxml) {
    this(fxml, RouteGroup.DEFAULT);
  }

  public String fxml() { return fxml; }
  public RouteGroup group() { return group; }
}