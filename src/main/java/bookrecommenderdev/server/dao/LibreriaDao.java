package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Libreria;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.server.dto.PaginaLibreria;
import bookrecommenderdev.server.dto.PaginaLibrerieRisultati;
import bookrecommenderdev.server.dto.PaginaLibriRisultati;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static bookrecommenderdev.Constants.LIBRARIES_PAGE_SIZE;
import static bookrecommenderdev.Constants.PAGE_SIZE;
import static bookrecommenderdev.utils.InputVerifiers.notNull;

public class LibreriaDao {
  private final DataSource datasource;
  public LibreriaDao(DataSource ds) { this.datasource = ds; }

  final String baseSearchAllQuery =
      "SELECT b.id_libro, b.titolo, b.autori, b.anno_pubblicazione, " +
          "COUNT(*) OVER() AS totalCount " +
          "FROM vw_libri_ricerca b " +
          "WHERE EXISTS (" +
          "  SELECT 1 FROM vw_librerie_libri ll " +
          "  WHERE ll.id_utente = ? AND ll.id_libro = b.id_libro" +
          ") ";

  public Optional<Libreria> getLibreria(int idUtente, int idLibreria) throws SQLException {
    final String q = "SELECT id_libreria, nome FROM Librerie WHERE id_libreria = ? AND id_utente = ? ";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, idLibreria);
      ps.setInt(2, idUtente);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new Libreria(
              rs.getInt("id_libreria"),
              rs.getString("nome")
          ));
        }
      }
    }

    return Optional.empty();
  }

  /*
  * todo doc
  * Restituisce anche le librerie vuote.
  * */
  public PaginaLibrerieRisultati getPageListLibrerie(int id_utente, int indicePagina) throws SQLException {
    final String q ="""
      SELECT t.id_libreria, t.nome_libreria, t.localCount,
             COUNT(*) OVER() AS totalCount
      FROM (
        SELECT
          l.id_libreria,
          l.nome AS nome_libreria,
          COUNT(c.id_libro) AS localCount
        FROM Librerie l
        LEFT JOIN Libreria_Contiene c ON l.id_libreria = c.id_libreria
        WHERE l.id_utente = ?
        GROUP BY l.id_libreria, l.nome
      ) t
      ORDER BY t.nome_libreria, t.id_libreria
      OFFSET ? LIMIT ?
      """;

    List<PaginaLibreria> elenco = new LinkedList<>();
    int totalCount = 0;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_utente);
      ps.setInt(2, Math.max(0, indicePagina) * LIBRARIES_PAGE_SIZE);
      ps.setInt(3, LIBRARIES_PAGE_SIZE);

      try (ResultSet rs = ps.executeQuery()) {
        if(rs.next()) {
          totalCount = rs.getInt("totalCount");

          do {
            elenco.add(new PaginaLibreria(
                new Libreria(
                    rs.getInt("id_libreria"),
                    id_utente,
                    rs.getString("nome_libreria")
                ),
                rs.getInt("localCount")
            ));
          } while (rs.next());
        }
      }
    }

    return new PaginaLibrerieRisultati(elenco, totalCount);
  }


  /*
  * todo doc
  * */
  public PaginaLibriRisultati searchAll(int id_utente, SearchRequest req, int indicePagina) throws SQLException {

    if (req == null || req.getTipo() == null) {
      return new PaginaLibriRisultati(List.of(), 0);
    }

    int offset = Math.max(0, indicePagina) * PAGE_SIZE;

    return switch (req.getTipo()) {

      case TITOLO -> {
        String titolo = notNull(req.getTitolo());
        if (titolo.isBlank()) yield new PaginaLibriRisultati(List.of(), 0);

        String q = baseSearchAllQuery +
            "AND b.titolo ILIKE ? " +
            "ORDER BY b.titolo, b.id_libro " +
            "OFFSET ? LIMIT ?";

        yield runSearchQuery(q, ps -> {
          ps.setInt(1, id_utente);
          ps.setString(2, "%" + titolo + "%");
          ps.setInt(3, offset);
          ps.setInt(4, PAGE_SIZE);
        });
      }

      case AUTORE -> {
        String autore = notNull(req.getAutore());
        if (autore.isBlank()) yield new PaginaLibriRisultati(List.of(), 0);

        String q = baseSearchAllQuery +
            "AND b.autori ILIKE ? " +
            "ORDER BY b.titolo, b.id_libro " +
            "OFFSET ? LIMIT ?";

        yield runSearchQuery(q, ps -> {
          ps.setInt(1, id_utente);
          ps.setString(2, "%" + autore + "%");
          ps.setInt(3, offset);
          ps.setInt(4, PAGE_SIZE);
        });
      }

      case AUTORE_ANNO -> {
        String autore = notNull(req.getAutore());
        Integer anno = req.getAnno();
        if (autore.isBlank() || anno == null) yield new PaginaLibriRisultati(List.of(), 0);

        String q = baseSearchAllQuery +
            "AND b.autori ILIKE ? AND b.anno_pubblicazione = ? " +
            "ORDER BY b.titolo, b.id_libro " +
            "OFFSET ? LIMIT ?";

        yield runSearchQuery(q, ps -> {
          ps.setInt(1, id_utente);
          ps.setString(2, "%" + autore + "%");
          ps.setInt(3, anno);
          ps.setInt(4, offset);
          ps.setInt(5, PAGE_SIZE);
        });
      }

    };
  }

  private PaginaLibriRisultati runSearchQuery(String sql, Binder fieldBinder) throws SQLException {
    List<Libro> items = new ArrayList<>();
    int total = 0;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      fieldBinder.bind(ps);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          total = rs.getInt("totalCount");
          do {
            items.add(new Libro(
                rs.getInt("id_libro"),
                rs.getString("titolo"),
                rs.getString("autori"),
                rs.getInt("anno_pubblicazione")
            ));
          } while (rs.next());
        }
      }
    }
    return new PaginaLibriRisultati(items, total);
  }


  /**
   * todo doc
   */
  public PaginaLibriRisultati searchIn(int id_utente, int id_libreria, int indicePagina) throws SQLException {
    String q =
        "SELECT *," +
            " COUNT(*) OVER() as totalCount" +
            " FROM vw_librerie_libri" +
            " WHERE id_libreria = ? AND id_utente = ? " +
            " OFFSET ? LIMIT ?";

    List<Libro> elenco = new LinkedList<>();
    int totalCount = 0;

    int offset = Math.max(0, indicePagina) * PAGE_SIZE;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libreria);
      ps.setInt(2, id_utente);
      ps.setInt(3, offset);
      ps.setInt(4, PAGE_SIZE);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {

          totalCount = rs.getInt("totalCount");
          do {
            elenco.add(new Libro(
                rs.getInt("id_libro"),
                rs.getString("titolo"),
                rs.getString("autori"),
                rs.getInt("anno_pubblicazione")
            ));
          } while (rs.next());
        }
      }

    }

    return new PaginaLibriRisultati(elenco, totalCount);
  }

  /**
   * todo doc
   * */
  public int creaLibreria(int id_utente, String nome, List<Integer> id_list) throws SQLException {
    if (nome == null || nome.isBlank()) throw new SQLException("Nome libreria non valido");
    if (id_list == null) id_list = List.of();

    final String creaLibreria =
        "INSERT INTO Librerie (id_utente, nome) VALUES (?, ?) RETURNING id_libreria";

    final String inserisciInLibreria =
        "INSERT INTO Libreria_Contiene (id_libreria, id_libro) VALUES (?, ?)";

    try (Connection conn = datasource.getConnection()) {
      conn.setAutoCommit(false);

      try {
        int id_libreria;

        try (PreparedStatement ps = conn.prepareStatement(creaLibreria)) {
          ps.setLong(1, id_utente);
          ps.setString(2, nome.trim());
          try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new SQLException("Impossibile creare libreria");
            id_libreria = rs.getInt(1);
          }
        }

        if (!id_list.isEmpty()) {
          try (PreparedStatement ps = conn.prepareStatement(inserisciInLibreria)) {
            for (Integer idLibro : id_list) {
              ps.setInt(1, id_libreria);
              ps.setInt(2, idLibro);
              ps.addBatch();
            }
            ps.executeBatch();
          }
        }

        conn.commit();
        return id_libreria;

      } catch (SQLException e) {
        conn.rollback();
        throw e;

      } finally {
        conn.setAutoCommit(true);
      }
    }
  }

  /**
   * todo doc
   * @param id_libreria Id libreria
   * @return booleano per verificare se la cancellazione ha avuto successo
   */
  public boolean deleteLibreria(int id_utente, int id_libreria) throws SQLException {
    String q = "DELETE FROM Librerie WHERE id_libreria = ? AND id_utente = ?;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libreria);
      ps.setInt(2, id_utente);
      return ps.executeUpdate() > 0;
    }
  }

  public boolean verificaLibroInLibrerieUtente(int idUtente, int idLibro) throws SQLException {
    final String q = """
    SELECT 1
    FROM Librerie l
    JOIN Libreria_Contiene lc ON lc.id_libreria = l.id_libreria
    WHERE l.id_utente = ? AND lc.id_libro = ?
    LIMIT 1
    """;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {
      ps.setInt(1, idUtente);
      ps.setInt(2, idLibro);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }
}
