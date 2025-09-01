package com.example.bookrecommenderdev.server.dao;

import com.example.bookrecommenderdev.model.Libreria;
import com.example.bookrecommenderdev.model.Valutazione;
import javafx.util.Pair;
import org.postgresql.core.Tuple;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class LibreriaDAO {
  private final DataSource datasource;
  public LibreriaDAO(DataSource ds) { this.datasource = ds; }


  //Get librerie
  public List<Pair<Libreria, Integer>> getLibrerie (int id_utente){
    List<Pair<Libreria, Integer>> elenco = new LinkedList<>();


    //elenco.getFirst().getKey()= new Libreria();
    System.out.println("Chiave ricevuta: " + id_utente);

    String q = "SELECT id_libreria, l.nome AS nome_libreria, COUNT(c.id_libro) AS numero_libri " +
            " FROM Librerie l LEFT JOIN Contiene c ON l.id_libreria = c.id_libreria " +
            " WHERE l.id_utente = ? GROUP BY l.id_libreria, l.nome;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_utente);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        elenco.add(
          new Pair<>(
            new Libreria(
              rs.getInt("id_libreria"),
              rs.getInt("id_utente"),
              rs.getString("nome")
            ),
            rs.getInt("numero_libri")
          )
        );
      }


      System.out.println("Numero risultati: "+ elenco.size());

    } catch (SQLException e) {
      System.out.println("Errore nelle connessione al database.");
      e.printStackTrace();
    }

    return elenco;
  }



}
