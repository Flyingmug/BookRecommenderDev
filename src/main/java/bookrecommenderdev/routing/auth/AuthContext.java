package bookrecommenderdev.routing.auth;

import bookrecommenderdev.model.Utente;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * todo
 * */
public class AuthContext {

  private static final ObjectProperty<Utente> currentUser =
      new SimpleObjectProperty<>(null);

  private AuthContext() {}

  public static ReadOnlyObjectProperty<Utente> userProperty() {
    return currentUser;
  }

  public static Utente getUser() {
    return currentUser.get();
  }

  public static boolean isAuthenticated() {
    return currentUser.get() != null;
  }

  public static void login(Utente user) {
    currentUser.set(user);
  }

  public static void logout() {
    currentUser.set(null);
  }
}
