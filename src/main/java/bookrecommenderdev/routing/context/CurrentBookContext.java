package bookrecommenderdev.routing.context;

import bookrecommenderdev.model.Libro;

import java.util.Optional;

public final class CurrentBookContext {

  private static Libro current;

  private CurrentBookContext() {}

  public static void set(Libro libro) {
    current = libro;
  }

  public static Optional<Libro> get() {
    return Optional.ofNullable(current);
  }

  public static void clear() {
    current = null;
  }
}
