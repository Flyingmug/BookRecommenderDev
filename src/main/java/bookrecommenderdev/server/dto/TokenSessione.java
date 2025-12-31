package bookrecommenderdev.server.dto;

import java.io.Serial;
import java.io.Serializable;

public record TokenSessione(String token, UtenteSessione user) implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}