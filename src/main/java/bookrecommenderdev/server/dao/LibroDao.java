package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Libro;
import bookrecommenderdev.server.dto.PaginaLibriRisultati;
import javafx.util.Pair;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static bookrecommenderdev.Constants.PAGE_SIZE;

public class LibroDao {

  private final DataSource datasource;
  public LibroDao(DataSource ds) {
    this.datasource = ds;
  }

  public Libro get(int id_libro) throws SQLException {
    Libro libro = null;
    System.out.println("Chiave ricevuta: " + id_libro);
    String q = "SELECT " +
      " l.id_libro as idLibro," +
      " l.anno_pubblicazione as annoPubblicazione," +
      " l.titolo," +
      " a.nome_autore as autori," +
      " e.nome_editore as editore, " +
      " STRING_AGG(c.nome_categoria, ', ' ORDER BY c.nome_categoria) AS categorie " +
      "FROM Libri l JOIN Autori a ON l.id_autore = a.id_autore JOIN Editori e ON l.id_editore = e.id_editore " +
      "JOIN Libri_Categorie lc ON l.id_libro = lc.id_libro JOIN Categorie c ON lc.id_categoria = c.id_categoria " +
      "WHERE l.id_libro = ? " +
      "GROUP BY l.id_libro, l.anno_pubblicazione, l.titolo, a.nome_autore, e.nome_editore;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        libro = new Libro(
          rs.getInt("idLibro"),
          rs.getInt("annoPubblicazione"),
          rs.getString("titolo"),
          rs.getString("autori"),
          rs.getString("editore"),
          rs.getString("categorie"));
      }

    }
    System.out.println("DB:" + libro);
    return libro;
  }

  public PaginaLibriRisultati getPage(int pageNumber, String title) throws SQLException {
    System.out.println("Chiave ricevuta: " + title);
    List<Libro> libri = new ArrayList<>();
    int totalCount = 0;
    String q = "SELECT id_libro, titolo, a.nome_autore as autori, anno_pubblicazione, COUNT(*) OVER() as numero_risultati" +
        " FROM Libri l JOIN Autori a ON l.id_autore = a.id_autore WHERE titolo ILIKE ? OFFSET ? LIMIT ?";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setString(1, "%"+title.toLowerCase()+"%");
      ps.setInt(2, pageNumber * PAGE_SIZE);
      ps.setInt(3, PAGE_SIZE);

      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        totalCount = rs.getInt("numero_risultati");

        do {
          libri.add(new Libro(
              rs.getInt("id_libro"),
              rs.getString("titolo"),
              rs.getString("autori"),
              rs.getInt("anno_pubblicazione")
          ));
        } while (rs.next());
      }
    }
    System.out.println("Numero risultati QUERY: " + libri.size());
    return new PaginaLibriRisultati(libri, totalCount);
  }

  public List<Libro> getFrom(List<Integer> elenco_id) throws SQLException {
    return null;
  }
}
