package bookrecommenderdev.model.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO restituito in fase di autenticazione o ripristino sessione.
 * <p>
 * Contiene il token di sessione e i dati essenziali dell’utente associato.
 *
 * @param token token di sessione (stringa non vuota)
 * @param user  dati utente associati alla sessione
 */
public record TokenSessione(String token, UtenteSessione user) implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}