package bookrecommenderdev.routing;

public enum LayoutType {
  DEFAULT("default-layout.fxml"),
  INTEGRATED("integrated-layout.fxml"),
  EMPTY("empty-layout.fxml");

  private final String fxml;

  LayoutType(String fxml) {
    this.fxml = fxml;
  }

  public String fxml() {
    return fxml;
  }       // no navbar
}
