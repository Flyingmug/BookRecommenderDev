package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Libro;
import bookrecommenderdev.server.dto.PaginaLibriRisultati;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static bookrecommenderdev.Constants.PAGE_SIZE;

public class LibroDao {

  private final DataSource datasource;
  public LibroDao(DataSource ds) {
    this.datasource = ds;
  }

  public Optional<Libro> getBasic(int id_libro) throws SQLException {
    final String q = "SELECT " +
        "id_libro, " +
        "anno_pubblicazione, " +
        "FROM Libri " +
        "WHERE l.id_libro = ? ";

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

  public PaginaLibriRisultati searchTitolo(int pageNumber, String title) throws SQLException {
    final String q =
        "SELECT " +
            "l.id_libro, l.titolo, a.nome_autore AS autori, l.anno_pubblicazione, " +
            "COUNT(*) OVER() AS numero_risultati " +
            "FROM Libri l " +
            "JOIN Autori a ON l.id_autore = a.id_autore " +
            "WHERE l.titolo ILIKE ? " +
            "ORDER BY l.titolo, l.id_libro " +
            "OFFSET ? LIMIT ?";

    List<Libro> items = new ArrayList<>();
    int totalCount = 0;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setString(1, "%" + title.toLowerCase() + "%");
      ps.setInt(2, pageNumber * PAGE_SIZE);
      ps.setInt(3, PAGE_SIZE);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          totalCount = rs.getInt("numero_risultati");
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

    return new PaginaLibriRisultati(items, totalCount);
  }
}
