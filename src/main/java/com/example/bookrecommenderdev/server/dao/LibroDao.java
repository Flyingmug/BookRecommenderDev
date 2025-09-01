package com.example.bookrecommenderdev.server.dao;

import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.model.Valutazione;

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

  public Optional<Libro> get(int id) {

    // 1. connessione al database
    // 2. query SELECT tramite id del libro
    // 3. trasformazione del risultato in oggetto di classe libro

    return Optional.empty();
  }

  public List<Libro> getPage(int pageNumber, String title) {

    System.out.println("Chiave ricevuta: " + title);

    List<Libro> libri = new ArrayList<>();
    String q = "SELECT id_libro, titolo FROM Libri WHERE titolo ILIKE ? OFFSET ? LIMIT ?";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setString(1, "%"+title.toLowerCase()+"%");
      ps.setInt(2, pageNumber * PAGE_SIZE);
      ps.setInt(3, PAGE_SIZE);


      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        libri.add(new Libro(
            rs.getInt("id_libro"),
            rs.getString("titolo")
        ));
      }

    } catch (SQLException e) {
        System.out.println("Errore nelle connessione al database.");
        e.printStackTrace();
    }

      System.out.println("Numero risultati: " + libri.size());

    return libri;
  }

}
