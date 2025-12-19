package bookrecommenderdev.routing;

public record Route(
    String fxml,
    LayoutType layout)
{

  // Constructor with default group
  public Route(String fxml) {
    this(fxml, LayoutType.DEFAULT);
  }
}