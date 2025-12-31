package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Utente;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UtenteDao {
  private final DataSource datasource;
  public UtenteDao(DataSource ds) {
    this.datasource = ds;
  }

  public Optional<Utente> findByUserId(String userId) throws SQLException {
    String q = "SELECT * FROM UtentiRegistrati WHERE userId = ?";
    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {

      ps.setString(1, userId);

      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(mapUtente(rs)) : Optional.empty();
      }
    }
  }

  public Optional<Utente> findById(int id) throws SQLException {
    final String q = "SELECT * FROM UtentiRegistrati WHERE id_utente = ?";

    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {

      ps.setInt(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(mapUtente(rs)) : Optional.empty();
      }
    }
  }

  public void save(Utente utente) throws SQLException {
    System.out.println("Chiavi ricevute: " + utente.getNome()); // DEBUG
    String q = "INSERT INTO UtentiRegistrati" +
        " (nome, cognome, email, codice_fiscale, password, userId)" +
        " VALUES (?, ?, ?, ?, ?, ?);";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setString(1, utente.getNome());
      ps.setString(2, utente.getCognome());
      ps.setString(3, utente.getEmail());
      ps.setString(4, utente.getCodiceFiscale());
      ps.setString(5, utente.getPassword());
      ps.setString(6, utente.getUserId());

      int rowsAffected = ps.executeUpdate();
      System.out.println("Righe modificate: " + rowsAffected);  // DEBUG
    }
  }

  private Utente mapUtente(ResultSet rs) throws SQLException {
    return new Utente(
        rs.getInt("id_utente"),
        rs.getString("nome"),
        rs.getString("cognome"),
        rs.getString("email"),
        rs.getString("codice_fiscale"),
        rs.getString("password"),
        rs.getString("userId")
    );
  }
}
