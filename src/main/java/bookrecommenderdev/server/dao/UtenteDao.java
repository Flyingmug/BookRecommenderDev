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


//  /**
//   *
//   * @param email
//   * @param fiscalCode
//   * @return ottiene la lista di utenti con email e password/codiceFiscale corrispondenti
//   */
//        public List<Utente> get(String email, String key, boolean fiscalCode) throws SQLException {
//    List<Utente> utenti = new LinkedList<>();
//
//    String q = "SELECT u.* FROM UtentiRegistrati u WHERE u.email = ?" + (fiscalCode ? " OR u.codice_fiscale = ?;" : " AND u.password = ?;");
//
//    try (Connection conn = datasource.getConnection();
//         PreparedStatement ps = conn.prepareStatement(q)) {
//
//      ps.setString(1, email);
//      ps.setString(2, key);
//
//      ResultSet rs = ps.executeQuery();
//
//      while (rs.next()) {
//        utenti.add(new Utente(
//          rs.getInt("id_utente"),
//          rs.getString("nome"),
//          rs.getString("cognome"),
//          rs.getString("email"),
//          rs.getString("codice_fiscale"),
//          rs.getString("password"),
//          rs.getString("userId")
//        ));
//      }
//
//    }
//
//    System.out.println("Numero risultati: " + utenti.size());
//
//    return utenti;
//  }

  public Optional<Utente> findByFiscalCode(String codiceFiscale) throws SQLException {
    String q = "SELECT * FROM UtentiRegistrati WHERE codice_fiscale = ?";

    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {

      ps.setString(1, codiceFiscale);
      ResultSet rs = ps.executeQuery();

      return rs.next() ? Optional.of(mapUtente(rs)) : Optional.empty();
    }
  }

  public Optional<Utente> findByEmailAndPassword(String email, String password) throws SQLException {
    String q = "SELECT * FROM UtentiRegistrati WHERE email = ? AND password = ?";

    try (Connection c = datasource.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {

      ps.setString(1, email);
      ps.setString(2, password);
      ResultSet rs = ps.executeQuery();

      return rs.next() ? Optional.of(mapUtente(rs)) : Optional.empty();
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

//      try (ResultSet keys = ps.getGeneratedKeys()) {
//        if (keys.next()) {
//          utente.setIdUtente(keys.getInt(1));
//        }
//      }

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
