package bookrecommenderdev.model.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO che rappresenta una sessione locale persistita lato client.
 * <p>
 * Contiene esclusivamente il token di sessione, senza alcuna informazione
 * aggiuntiva sull’utente.
 * @param token token di sessione associato all’utente
 */
public record SessioneLocale(String token) implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}