package bookrecommenderdev.server.dto;

import bookrecommenderdev.routing.auth.AuthStatus;
import bookrecommenderdev.model.Utente;

import java.io.Serial;
import java.io.Serializable;

public record AuthResult(Utente user, AuthStatus authStatus) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
}
