package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Libreria;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.server.dto.LibraryResult;
import bookrecommenderdev.server.dto.PaginaLibriRisultati;
import javafx.util.Pair;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static bookrecommenderdev.Constants.PAGE_SIZE;

public class LibreriaDao {
  private final DataSource datasource;
  public LibreriaDao(DataSource ds) { this.datasource = ds; }


  // Get librerie
  /*
  * todo doc
  * Restituisce anche le librerie vuote.
  * */
  public List<LibraryResult> getLibrerie (long id_utente) throws SQLException {
    List<LibraryResult> elenco = new LinkedList<>();

    System.out.println("Chiave ricevuta: " + id_utente);

    String q = "SELECT l.id_libreria, l.nome AS nome_libreria, COUNT(c.id_libro) AS numero_libri " +
            " FROM Librerie l LEFT JOIN Libreria_Contiene c ON l.id_libreria = c.id_libreria " +
            " WHERE l.id_utente = ? GROUP BY l.id_libreria, l.nome;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setLong(1, id_utente);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        elenco.add(
          new LibraryResult(
            new Libreria(
              rs.getLong("id_libreria"),
              id_utente,
              rs.getString("nome_libreria")
            ),
            rs.getInt("numero_libri")
            )
          );
      }

      System.out.println("Numero risultati (Librerie): "+ elenco.size());

    }

    return elenco;
  }


  /*
  * todo documentazione
  * */
  public PaginaLibriRisultati getPage(int pageNumber, String query, long id_utente) throws SQLException {

    List<Libro> elenco = new LinkedList<>();
    int totalCount = 0;

    System.out.println("id Utente: " + id_utente);
    System.out.println("query: " + query);
    System.out.println("pageNumber: " + pageNumber);

    String q = "SELECT *, COUNT(*) OVER() as \"numero_risultati\" FROM vw_librerie_libri" +
        " WHERE id_utente = ? AND titolo ILIKE ?" +
        " OFFSET ? LIMIT ?";

    try (Connection conn = datasource.getConnection();
    PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setLong(1, id_utente);
      ps.setString(2, query);
      ps.setInt(3, pageNumber * PAGE_SIZE);
      ps.setInt(4, PAGE_SIZE);

      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        totalCount = rs.getInt("numero_risultati");

        do {
          elenco.add(new Libro(
              rs.getInt("id_libro"),
              rs.getString("titolo"),
              "", //rs.getString("autori"),
              rs.getInt("anno_pubblicazione")
          ));
        } while (rs.next());
      }

    }
    System.out.println("Numero risultati libri: " + elenco.size());
    return new PaginaLibriRisultati(elenco, totalCount);
  }


  public PaginaLibriRisultati getLibraryPage(long id_utente, String nome_libreria, int pageNumber) throws SQLException {
    List<Libro> elenco = new LinkedList<>();
    int totalCount = 0;

    System.out.println("id Utente: " + id_utente);
    System.out.println("nome libreria: " + nome_libreria);
    System.out.println("pageNumber: " + pageNumber);

    String q = "SELECT *, COUNT(*) OVER() as \"numero_risultati\" FROM vw_librerie_libri" +
        " WHERE id_utente = ? AND nome_libreria = ?" +
        " OFFSET ? LIMIT ?";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setLong(1, id_utente);
      ps.setString(2, nome_libreria);
      ps.setInt(3, pageNumber * PAGE_SIZE);
      ps.setInt(4, PAGE_SIZE);

      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        totalCount = rs.getInt("numero_risultati");

        do {
          elenco.add(new Libro(
              rs.getInt("id_libro"),
              rs.getString("titolo"),
              "",//rs.getString("autori"),
              rs.getInt("anno_pubblicazione")
          ));
        } while (rs.next());
      }

    }
    System.out.println("Numero risultati libri: " + elenco.size());
    return new PaginaLibriRisultati(elenco, totalCount);
  }


  /**
   * @param id_libreria
   * @return booleano per verificare se la cancellazione ha avuto successo
   */
  public boolean deleteLibreria(int id_libreria){
    System.out.println("Chiavi ricevute: " + id_libreria);

    String q = "DELETE FROM Libreria WHERE id_libreria = ?;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libreria);

      int rowsAffected = ps.executeUpdate(q);
      System.out.println("Righe eliminate: " + rowsAffected);
      return rowsAffected>0;
    } catch (SQLException e) {
      System.out.println("Errore nelle connessione al database.");
      e.printStackTrace();
    }
    return false;
  }
}
