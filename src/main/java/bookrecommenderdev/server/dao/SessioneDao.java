package bookrecommenderdev.server.dao;

import bookrecommenderdev.server.auth.Tokenizer;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static java.sql.Types.TIMESTAMP;

public class SessioneDao {
  private final DataSource datasource;

  public SessioneDao(DataSource ds) {
    this.datasource = ds;
  }

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

  /** Returns user id if token exists and is not expired. Also updates last_used and optionally extends expiry. */
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

  public void deleteToken(String token) throws SQLException {
    final String q = "DELETE FROM sessioniUtenti WHERE token = ?";

    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {

      ps.setString(1, token);

      ps.executeUpdate();
    }
  }
}
