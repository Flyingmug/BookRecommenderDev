package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.exceptions.AlreadyExistsException;
import bookrecommenderdev.model.exceptions.LimitExceededException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.server.dto.LibroConsigliato;
import bookrecommenderdev.server.dto.PaginaConsigliRisultati;
import bookrecommenderdev.server.dto.PaginaLibriRisultati;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsiglioLibroDao {
  private final DataSource datasource;
  public ConsiglioLibroDao(DataSource ds) { this.datasource = ds; }

  public void inserisciConsiglio(int idUtente, int idLibroBase, int idLibroCons)
      throws SQLException, NotFoundException, AlreadyExistsException, LimitExceededException {

    if (idLibroBase == idLibroCons) {
      throw new IllegalArgumentException("Libro base e libro raccomandato non possono corrispondere.");
    }

    final String verificaBase = "SELECT 1 FROM Libri WHERE id_libro = ? FOR UPDATE";
    final String possedimentoBase = """
      SELECT 1
      FROM Librerie l
      JOIN Libreria_Contiene lc ON lc.id_libreria = l.id_libreria
      WHERE l.id_utente = ? AND lc.id_libro = ?
      LIMIT 1
      """;
    final String contaQ = "SELECT COUNT(*) FROM ConsigliLibri WHERE id_utente = ? AND id_libro_base = ?";
    final String inserisciQ = """
      INSERT INTO ConsigliLibri(id_utente, id_libro_base, id_libro_cons)
      VALUES (?, ?, ?)
      """;

    try (Connection conn = datasource.getConnection()) {
      conn.setAutoCommit(false);

      try {
        // 1) Verifica che il libro esista; "blocca" il libro per la transazione
        try (PreparedStatement ps = conn.prepareStatement(verificaBase)) {
          ps.setInt(1, idLibroBase);
          try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
              throw new NotFoundException("Libro base non trovato.");
            }
          }
        }

        // 2) Verifica che l'utente possegga il libro nelle sue librerie
        try (PreparedStatement ps = conn.prepareStatement(possedimentoBase)) {
          ps.setInt(1, idUtente);
          ps.setInt(2, idLibroBase);
          try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
              throw new NotFoundException("Libro base non presente nelle librerie dell'utente.");
            }
          }
        }

        // 3) Conta il numero di consigli
        int cnt;
        try (PreparedStatement ps = conn.prepareStatement(contaQ)) {
          ps.setInt(1, idUtente);
          ps.setInt(2, idLibroBase);
          try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            cnt = rs.getInt(1);
          }
        }

        if (cnt >= 3) {
          throw new LimitExceededException("Massimo 3 raccomandazioni per libro.");
        }

        // 4) Inserimento
        try (PreparedStatement ps = conn.prepareStatement(inserisciQ)) {
          ps.setInt(1, idUtente);
          ps.setInt(2, idLibroBase);
          ps.setInt(3, idLibroCons);
          ps.executeUpdate();
        }

        conn.commit();

      } catch (SQLException e) {
        conn.rollback();

        if ("23505".equals(e.getSQLState())) {
          throw new AlreadyExistsException("Consiglio già presente.");
        }

        if ("23503".equals(e.getSQLState())) {
          throw new NotFoundException("Libro consigliato non trovato.");
        }

        throw e;
      } catch (NotFoundException | AlreadyExistsException | LimitExceededException ex) {
        conn.rollback();
        throw ex;
      } finally {
        conn.setAutoCommit(true);
      }
    }
  }

  public List<Libro> getConsigliUtente(int idUtente, int idLibroBase) throws SQLException {
    final String q = """
      SELECT v.id_libro, v.titolo, v.anno_pubblicazione, v.autore
      FROM ConsigliLibri c
      JOIN vw_libri_ricerca v ON v.id_libro = c.id_libro_cons
      WHERE c.id_utente = ? AND c.id_libro_base = ?
      """;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, idUtente);
      ps.setInt(2, idLibroBase);

      try (ResultSet rs = ps.executeQuery()) {
        List<Libro> list = new java.util.ArrayList<>();
        while (rs.next()) {
          list.add(new Libro(
              rs.getInt("id_libro"),
              rs.getString("titolo"),
              rs.getString("autori"),
              rs.getInt("anno_pubblicazione")
          ));
        }
        return list;
      }
    }
  }

  /*get page libri consigliati per un libro*/
  public PaginaConsigliRisultati searchConsigli(int idLibro, int indicePagina) throws SQLException {
    final String q = """
        SELECT
        v.id_libro, v.titolo, v.anno_pubblicazione, v.autori,
        COUNT(DISTINCT c.id_utente) AS num_consigliato,
        COUNT(*) OVER() AS totalCount
      FROM ConsigliLibri c
      JOIN vw_libri_ricerca v
        ON v.id_libro = c.id_libro_cons
      WHERE c.id_libro_base = ?
      GROUP BY v.id_libro, v.titolo, v.anno_pubblicazione, v.autori
      ORDER BY num_consigliato DESC, v.titolo, v.id_libro
      OFFSET ? LIMIT ?;
      """;

    int temp = 30;  // fixme constant after ux choices
    int offset = Math.max(0, indicePagina) * temp;

    List<LibroConsigliato> list = new ArrayList<>();
    int totalCount = 0;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, idLibro);
      ps.setInt(2, offset);
      ps.setInt(3, temp);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          totalCount = rs.getInt("totalCount");
          list.add(new LibroConsigliato(
              new Libro(
                rs.getInt("id_libro"),
                rs.getString("titolo"),
                rs.getString("autori"),
                rs.getInt("anno_pubblicazione")
              ),
              rs.getInt("num_consigliato")
          ));
        }
      }
    }

    return new PaginaConsigliRisultati(list, totalCount);
  }

  public boolean deleteConsiglio(int idUtente, int idLibroBase, int idLibroCons) throws SQLException {
    final String q = """
      DELETE FROM ConsigliLibri
      WHERE id_utente = ? AND id_libro_base = ? AND id_libro_cons = ?
      """;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, idUtente);
      ps.setInt(2, idLibroBase);
      ps.setInt(3, idLibroCons);

      return ps.executeUpdate() > 0;
    }
  }





//  /**
//   * Ottiene tutti i consigli per un determinato libro
//   * @param id_libro secondo cui ricercare
//   * @return lista di consigli
//   */
//  public List<ConsigliLettura> getAll(int id_libro, boolean limited) throws SQLException {
//    List<ConsigliLettura> elenco = new LinkedList<>();
//
//    System.out.println("Chiave ricevuta: " + id_libro);
//
//    String q = "SELECT " +
//      "u.id_utente, cl.id_libro, l1.id_libro AS idconsiglio1, l2.id_libro AS idconsiglio2, l3.id_libro AS idconsiglio3 " +
//      "FROM ConsigliLibri cl INNER JOIN UtentiRegistrati u ON cl.id_utente = u.id_utente " +
//      "INNER JOIN Libri lb ON cl.id_libro = lb.id_libro LEFT JOIN Libri l1 ON cl.consiglio1 = l1.id_libro " +
//      "LEFT JOIN Libri l2 ON cl.consiglio2 = l2.id_libro " +
//      "LEFT JOIN Libri l3 ON cl.consiglio3 = l3.id_libro " +
//      "WHERE cl.id_libro = ? ORDER BY cl.id_utente " + (limited ? ";" : "LIMIT 10");
//
//    try (Connection conn = datasource.getConnection();
//         PreparedStatement ps = conn.prepareStatement(q)) {
//
//      ps.setInt(1, id_libro);
//      ResultSet rs = ps.executeQuery();
//
//      while (rs.next()) {
//        elenco.add(
//          new ConsigliLettura(
//            rs.getInt("id_utente"),
//            rs.getInt("id_libro"),
//            rs.getInt("idconsiglio1"),
//            rs.getInt("idconsiglio2"),
//            rs.getInt("idconsiglio3")));
//      }
//
//      System.out.println("Numero risultati: "+ elenco.size());
//
//    }
//    return elenco;
//  }
//
//
//  public boolean save (int id_libro, ConsigliLettura consigliLettura){
//    System.out.println("Chiavi ricevute: " + id_libro);
//    String q = "INSERT INTO ConsigliLibri (id_utente, id_libro, consiglio1, consiglio2, consiglio3) " +
//      "VALUES (?, ?, ?, ?, ?);";
//
//    try (Connection conn = datasource.getConnection();
//         PreparedStatement ps = conn.prepareStatement(q)) {
//
//      ps.setInt(1, consigliLettura.getIdUtente());
//      ps.setInt(2, consigliLettura.getIdLibro());
//      ps.setInt(3, consigliLettura.getIdconsiglio1());
//      ps.setInt(4, consigliLettura.getIdconsiglio2());
//      ps.setInt(5, consigliLettura.getIdconsiglio3());
//
//      int rowsAffected = ps.executeUpdate(q);
//      System.out.println("Righe modificate: " + rowsAffected);
//      return rowsAffected>0;
//    } catch (SQLException e) {
//      System.out.println("Errore nelle connessione al database.");
//      e.printStackTrace();
//    }
//    return false;
//  }
}
