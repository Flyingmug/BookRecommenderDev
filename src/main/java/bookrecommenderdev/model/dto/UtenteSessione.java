package bookrecommenderdev.model.dto;

import java.io.Serial;
import java.io.Serializable;

public record UtenteSessione(int idUtente, String nome, String cognome, String email, String userId)
  implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
