package bookrecommenderdev.routing.context;

import bookrecommenderdev.model.Libreria;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Optional;

public final class CurrentLibraryContext {

  private static final ObjectProperty<Libreria> current =
      new SimpleObjectProperty<>();

  private CurrentLibraryContext() {}

  public static Optional<Libreria> get() {
    return Optional.ofNullable(current.get());
  }

  public static void set(Libreria library) {
    current.set(library);
  }

  public static void clear() {
    current.set(null);
  }

  public static ReadOnlyObjectProperty<Libreria> property() {
    return current;
  }
}