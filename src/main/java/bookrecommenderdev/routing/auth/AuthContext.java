package bookrecommenderdev.routing.auth;

import bookrecommenderdev.server.dto.UtenteSessione;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * todo
 * */
public class AuthContext {

  private static final ObjectProperty<UtenteSessione> currentUser =
      new SimpleObjectProperty<>(null);

  private AuthContext() {}

  public static ReadOnlyObjectProperty<UtenteSessione> userProperty() {
    return currentUser;
  }

  public static UtenteSessione getUser() {
    return currentUser.get();
  }

  public static boolean isAuthenticated() {
    return currentUser.get() != null;
  }

  public static void login(UtenteSessione user) {
    currentUser.set(user);
  }

  public static void logout() {
    currentUser.set(null);
  }
}
