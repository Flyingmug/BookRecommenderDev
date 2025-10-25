package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.ConsigliLettura;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ConsiglioLibroDao {
  private final DataSource datasource;
  public ConsiglioLibroDao(DataSource ds) { this.datasource = ds; }

  /**
   * Ottiene tutti i consigli per un determinato libro
   * @param id_libro secondo cui ricercare
   * @return lista di consigli
   */
  public List<ConsigliLettura> getAll(int id_libro, boolean limited) throws SQLException {
    List<ConsigliLettura> elenco = new LinkedList<>();

    System.out.println("Chiave ricevuta: " + id_libro);

    String q = "SELECT " +
      "u.id_utente, cl.id_libro, l1.id_libro AS idconsiglio1, l2.id_libro AS idconsiglio2, l3.id_libro AS idconsiglio3 " +
      "FROM ConsigliLibri cl INNER JOIN UtentiRegistrati u ON cl.id_utente = u.id_utente " +
      "INNER JOIN Libri lb ON cl.id_libro = lb.id_libro LEFT JOIN Libri l1 ON cl.consiglio1 = l1.id_libro " +
      "LEFT JOIN Libri l2 ON cl.consiglio2 = l2.id_libro " +
      "LEFT JOIN Libri l3 ON cl.consiglio3 = l3.id_libro " +
      "WHERE cl.id_libro = ? ORDER BY cl.id_utente " + (limited ? ";" : "LIMIT 10");

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        elenco.add(
          new ConsigliLettura(
            rs.getInt("id_utente"),
            rs.getInt("id_libro"),
            rs.getInt("idconsiglio1"),
            rs.getInt("idconsiglio2"),
            rs.getInt("idconsiglio3")));
      }

      System.out.println("Numero risultati: "+ elenco.size());

    }
    return elenco;
  }


  public boolean save (int id_libro, ConsigliLettura consigliLettura){
    System.out.println("Chiavi ricevute: " + id_libro);
    String q = "INSERT INTO ConsigliLibri (id_utente, id_libro, consiglio1, consiglio2, consiglio3) " +
      "VALUES (?, ?, ?, ?, ?);";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, consigliLettura.getIdUtente());
      ps.setInt(2, consigliLettura.getIdLibro());
      ps.setInt(3, consigliLettura.getIdconsiglio1());
      ps.setInt(4, consigliLettura.getIdconsiglio2());
      ps.setInt(5, consigliLettura.getIdconsiglio3());

      int rowsAffected = ps.executeUpdate(q);
      System.out.println("Righe modificate: " + rowsAffected);
      return rowsAffected>0;
    } catch (SQLException e) {
      System.out.println("Errore nelle connessione al database.");
      e.printStackTrace();
    }
    return false;
  }
}
