package bookrecommenderdev.model.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO che rappresenta i dati essenziali di un utente autenticato.
 * <p>
 * Viene utilizzato nel contesto di una sessione (es. Login) e contiene
 * solo informazioni non sensibili, adatte a essere trasmesse al client.
 *
 * @param idUtente identificatore numerico dell’utente
 * @param nome     nome dell’utente
 * @param cognome  cognome dell’utente
 * @param email    email dell’utente
 * @param userId   identificatore logico (es. Username)
 */
public record UtenteSessione(int idUtente, String nome, String cognome, String email, String userId)
  implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
