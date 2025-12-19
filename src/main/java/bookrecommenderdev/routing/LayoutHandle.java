package bookrecommenderdev.routing;

import javafx.scene.Node;

public record LayoutHandle(
    LayoutController controller,
    Node root
) {}