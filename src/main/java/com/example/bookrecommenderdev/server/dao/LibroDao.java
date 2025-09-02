package com.example.bookrecommenderdev.server.dao;

import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.model.Valutazione;
import javafx.util.Pair;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDao {

  private final int PAGE_SIZE = 50;
  private final DataSource datasource;
  public LibroDao(DataSource ds) {
    this.datasource = ds;
  }

  public Libro get(int id_libro) {
    Libro libro = null;
    System.out.println("Chiave ricevuta: " + id_libro);
    String q = "SELECT " +
      "    l.id_libro, l.anno_pubblicazione, l.titolo, a.nome_autore, e.nome_editore, " +
      "    STRING_AGG(c.nome_categoria, ', ' ORDER BY c.nome_categoria) AS categorie " +
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
          rs.getInt("id_libro"),
          rs.getInt("annoPubblicazione"),
          rs.getString("titolo"),
          rs.getString("autori"),
          rs.getString("editore"),
          rs.getString("categorie"));
      }

      System.out.println("Valutazione istanziata: " + (libro != null));

    } catch (SQLException e) {
      System.out.println("Errore nelle connessione al database.");
      e.printStackTrace();
    }
    return libro;
  }

  public Pair<List<Libro>, Integer> getPage(int pageNumber, String title) {
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
              rs.getString("titolo")
          ));
        } while (rs.next());
      }

    } catch (SQLException e) {
        System.out.println("Errore nelle connessione al database.");
        e.printStackTrace();
    }
      System.out.println("Numero risultati QUERY: " + libri.size());
    return new Pair<>(libri, totalCount);
  }
}
