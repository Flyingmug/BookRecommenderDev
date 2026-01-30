package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.base.Utente;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * DAO per l’accesso ai dati degli utenti registrati.
 * <p>
 * Espone operazioni di lettura e inserimento per l’entità {@link Utente}.
 * Non applica logica di autenticazione o validazione di dominio avanzata,
 * che è demandata ai livelli superiori dell’applicazione.
 * <p>
 * Il dettaglio delle query SQL è descritto nella documentazione tecnica esterna;
 * qui viene documentato il contratto dei metodi e la semantica dei risultati.
 */
public class UtenteDao {
  private final DataSource datasource;
  public UtenteDao(DataSource ds) {
    this.datasource = ds;
  }

  /**
   * Recupera un utente a partire dal suo {@code userId}.
   * <p>
   * {@code userId} è un identificatore logico univoco (es. username).
   * Se non esiste alcun utente con il valore indicato, restituisce
   * {@link Optional#empty()}.
   *
   * @param userId identificatore logico dell’utente
   * @return utente se presente; {@link Optional#empty()} altrimenti
   * @throws SQLException per errori di accesso ai dati
   */
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

  /**
   * Recupera un utente a partire dal suo identificatore numerico.
   * <p>
   * Se non esiste alcun utente con l’id indicato, restituisce
   * {@link Optional#empty()}.
   *
   * @param id identificatore numerico dell’utente
   * @return utente se presente; {@link Optional#empty()} altrimenti
   * @throws SQLException per errori di accesso ai dati
   */
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

  /**
   * Persiste un nuovo utente nel database.
   * <p>
   * Il metodo assume che l’oggetto {@link Utente} sia già stato validato
   * (unicità di {@code userId}, formato campi, hashing password, ecc.)
   * dai livelli superiori.
   * <p>
   * In caso di violazioni di vincoli di database (es. unicità),
   * viene sollevata una {@link SQLException}.
   *
   * @param utente utente da salvare
   * @throws SQLException per errori di accesso ai dati o violazioni di vincoli
   */
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

  /**
   * Mappa una riga del {@link ResultSet} in un’istanza di {@link Utente}.
   * <p>
   * Metodo di utilità interno al DAO.
   *
   * @param rs result set posizionato su una riga valida
   * @return istanza di {@link Utente} popolata con i dati della riga corrente
   * @throws SQLException per errori di accesso ai dati
   */
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
