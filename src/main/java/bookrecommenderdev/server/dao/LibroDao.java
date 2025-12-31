package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.model.dto.PaginaLibriRisultati;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static bookrecommenderdev.Constants.PAGE_SIZE;
import static bookrecommenderdev.model.utils.InputVerifiers.notNull;

public class LibroDao {
  private final DataSource datasource;
  public LibroDao(DataSource ds) { this.datasource = ds; }

  final String baseSearchQuery =
      "SELECT id_libro," +
          " titolo," +
          " autori," +
          " anno_pubblicazione," +
          " COUNT(*) OVER() AS totalCount " +
      "FROM vw_libri_ricerca l ";

  /**
   * todo doc
   * */
  public Optional<Libro> getBasic(int id_libro) throws SQLException {
    final String q = "SELECT " +
        "id_libro, " +
        "titolo, " +
        "anno_pubblicazione " +
        "FROM Libri " +
        "WHERE id_libro = ? ";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();

        return Optional.of(new Libro(
            rs.getInt("id_libro"),
            rs.getInt("anno_pubblicazione"),
            rs.getString("titolo")
        ));
      }

    }
  }

  /**
   * todo doc
   * */
  public Optional<Libro> getComplete(int id_libro) throws SQLException {
    final String q = "SELECT " +
        "l.id_libro, " +
        "l.anno_pubblicazione, " +
        "l.titolo, " +
        "a.nome_autore AS autori, " +
        "e.nome_editore AS editore,STRING_AGG(c.nome_categoria, ', ' ORDER BY c.nome_categoria) AS categorie " +
        "FROM Libri l " +
        "JOIN Autori a ON l.id_autore = a.id_autore " +
        "JOIN Editori e ON l.id_editore = e.id_editore " +
        "LEFT JOIN Libri_Categorie lc ON l.id_libro = lc.id_libro " +
        "LEFT JOIN Categorie c ON lc.id_categoria = c.id_categoria " +
        "WHERE l.id_libro = ? " +
        "GROUP BY " +
        "l.id_libro, " +
        "l.anno_pubblicazione, " +
        "l.titolo, " +
        "a.nome_autore, " +
        "e.nome_editore;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();

        Libro libro = new Libro(
            rs.getInt("id_libro"),
            rs.getInt("anno_pubblicazione"),
            rs.getString("titolo"),
            rs.getString("autori"),
            rs.getString("editore"),
            rs.getString("categorie")
        );
        return Optional.of(libro);
      }

    }
  }

  /**
   * todo doc
   * */
  public PaginaLibriRisultati search(SearchRequest req, int indicePagina) throws SQLException {

    // controllo validità richiesta
    if (req == null || req.getTipo() == null) {
      return new PaginaLibriRisultati(List.of(), 0);
    }

    // calcolo offset
    int offset = Math.max(0, indicePagina) * PAGE_SIZE;

    return switch (req.getTipo()) {

      case TITOLO -> {
        String titolo = notNull(req.getTitolo());
        if (titolo.isBlank()) yield new PaginaLibriRisultati(List.of(), 0);

        String q = baseSearchQuery + "WHERE l.titolo ILIKE ? " +
            "ORDER BY titolo, id_libro " +
            "OFFSET ? LIMIT ?";

        yield runSearchQuery(q, ps -> {
          ps.setString(1, "%" + titolo.trim() + "%");
          ps.setInt(2, offset);
          ps.setInt(3, PAGE_SIZE);
        });
      }

      case AUTORE -> {
        String autori = notNull(req.getAutore());
        if (autori.isBlank()) yield new PaginaLibriRisultati(List.of(), 0);

        String q = baseSearchQuery +
            "WHERE autori ILIKE ? " +
            "ORDER BY titolo, id_libro " +
            "OFFSET ? LIMIT ?";

        yield runSearchQuery(q, ps -> {
          ps.setString(1, "%" + autori.trim() + "%");
          ps.setInt(2, offset);
          ps.setInt(3, PAGE_SIZE);
        });
      }

      case AUTORE_ANNO -> {
        String autori = notNull(req.getAutore());
        Integer anno = req.getAnno();
        if (autori.isBlank() || anno == null) { yield new  PaginaLibriRisultati(List.of(), 0); }

        String q = baseSearchQuery +
            "WHERE autori ILIKE ? AND anno_pubblicazione = ? " +
            "ORDER BY titolo, id_libro " +
            "OFFSET ? LIMIT ?";

        yield runSearchQuery(q, ps -> {
          ps.setString(1, "%" + autori.trim() + "%");
          ps.setInt(2, anno);
          ps.setInt(3, offset);
          ps.setInt(4, PAGE_SIZE);
        });
      }

    };
  }

  /**
   * todo doc
   * */
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
}

