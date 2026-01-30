package bookrecommenderdev.server.dao;

import bookrecommenderdev.server.auth.Tokenizer;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static java.sql.Types.TIMESTAMP;

/**
 * DAO per la gestione delle sessioni utente basate su token.
 * <p>
 * Fornisce operazioni per creare, risolvere e invalidare token di sessione
 * persistiti a database. Supporta sia sessioni a scadenza fissa sia sessioni
 * con scadenza estesa (“sliding TTL”).
 * <p>
 * Il dettaglio delle query SQL e dello schema è descritto nella documentazione
 * tecnica esterna; qui viene documentata la semantica dei metodi e del ciclo
 * di vita dei token.
 */
public class SessioneDao {
  private final DataSource datasource;

  public SessioneDao(DataSource ds) {
    this.datasource = ds;
  }

  /**
   * Crea una nuova sessione per un utente e restituisce il token associato.
   * <p>
   * Il token generato è un valore casuale (64 caratteri esadecimali) e viene
   * immediatamente persistito.
   * <p>
   * Se {@code ttl} è {@code null}, la sessione non ha scadenza temporale.
   *
   * @param idUtente id dell’utente per cui creare la sessione
   * @param ttl      durata della sessione; {@code null} per sessione senza scadenza
   * @return token di sessione appena creato
   * @throws SQLException per errori di accesso ai dati
   */
  public String creaSessione(int idUtente, java.time.Duration ttl) throws SQLException {
    String token = Tokenizer.newToken64Hex();

    final String q = """
      INSERT INTO sessioniUtenti(token, id_utente, expires_at)
      VALUES (?, ?, ?)
      """;

    Timestamp expires =
        (ttl == null) ? null : Timestamp.from(java.time.Instant.now().plus(ttl));

    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {

      ps.setString(1, token);
      ps.setInt(2, idUtente);
      if (expires == null) ps.setNull(3, TIMESTAMP);
      else ps.setTimestamp(3, expires);

      ps.executeUpdate();
    }

    return token;
  }

  /**
   * Risolve un token di sessione restituendo l’id dell’utente associato.
   * <p>
   * Semantica:
   * <ul>
   *   <li>se il token è {@code null}, vuoto o inesistente, ritorna {@link Optional#empty()};</li>
   *   <li>se il token è scaduto, viene eliminato e ritorna {@link Optional#empty()};</li>
   *   <li>se valido, ritorna l’id utente associato;</li>
   *   <li>aggiorna sempre {@code last_used};</li>
   *   <li>se {@code slidingTtl} è fornito, estende la scadenza della sessione.</li>
   * </ul>
   *
   * @param token      token di sessione da risolvere
   * @param slidingTtl durata per l’estensione della sessione; {@code null} per non estendere
   * @return {@link Optional} contenente l’id utente se il token è valido
   * @throws SQLException per errori di accesso ai dati
   */
  public Optional<Integer> resolveToken(String token, Duration slidingTtl) throws SQLException {
    if (token == null || token.isBlank()) return Optional.empty();

    final String select = """
      SELECT id_utente, expires_at
      FROM sessioniUtenti
      WHERE token = ?
      """;

    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(select)) {

      ps.setString(1, token);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();

        Timestamp expiresAt = rs.getTimestamp("expires_at");
        if (expiresAt != null && expiresAt.toInstant().isBefore(Instant.now())) {
          deleteToken(token); // cleanup expired token
          return Optional.empty();
        }

        int idUtente = rs.getInt("id_utente");

        updateToken(c, token, slidingTtl); // update last_used (+ maybe extend expiry)
        return Optional.of(idUtente);
      }
    }
  }

  /**
   * Aggiorna i metadati di un token di sessione.
   * <p>
   * Aggiorna sempre il campo {@code last_used}. Se {@code slidingTtl} è {@code null},
   * la scadenza viene rimossa; altrimenti viene estesa a partire dall’istante corrente.
   * <p>
   * Questo metodo è pensato per essere invocato all’interno di {@link #resolveToken}.
   *
   * @param c          connessione JDBC già aperta
   * @param token      token da aggiornare
   * @param slidingTtl durata per la nuova scadenza; {@code null} per nessuna scadenza
   * @throws SQLException per errori di accesso ai dati
   */
  private void updateToken(Connection c, String token, Duration slidingTtl) throws SQLException {
    final String q = """
      UPDATE sessioniUtenti
      SET last_used = NOW(),
          expires_at = ?
      WHERE token = ?
      """;

    Timestamp newExpires =
        (slidingTtl == null) ? null : Timestamp.from(Instant.now().plus(slidingTtl));

    try (PreparedStatement ps = c.prepareStatement(q)) {

      if (newExpires == null) ps.setNull(1, TIMESTAMP);
      else ps.setTimestamp(1, newExpires);

      ps.setString(2, token);

      ps.executeUpdate();
    }
  }

  /**
   * Elimina esplicitamente un token di sessione.
   * <p>
   * L’operazione è idempotente: se il token non esiste, non viene sollevata alcuna eccezione.
   *
   * @param token token di sessione da eliminare
   * @throws SQLException per errori di accesso ai dati
   */
  public void deleteToken(String token) throws SQLException {
    final String q = "DELETE FROM sessioniUtenti WHERE token = ?";

    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {

      ps.setString(1, token);

      ps.executeUpdate();
    }
  }
}
