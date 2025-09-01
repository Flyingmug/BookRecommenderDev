package com.example.bookrecommenderdev.server.dao;

import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.model.Utente;
import com.example.bookrecommenderdev.model.Valutazione;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UtenteDao {
  private final DataSource datasource;
  public UtenteDao(DataSource ds) {
    this.datasource = ds;
  }


  /**
   *
   * @param nome
   * @param password
   * @return ottiene la lista di utenti con nome e password corrispondenti
   */
  public List<Utente> get(String nome, String password){
    List<Utente> utenti = new LinkedList<>();

    System.out.println("Chiave ricevuta: " + nome);
    String q = "SELECT u.* FROM UtentiRegistrati u WHERE u.nome = ? AND u.password = ?;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setString(1, nome);
      ps.setString(2, password);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        utenti.add(new Utente(
          rs.getInt("id_utente"),
          rs.getString("nome"),
          rs.getString("cognome"),
          rs.getString("codice_fiscale"),
          rs.getString("email"),
          rs.getString("userId"),
          rs.getString("password")
        ));
      }

    } catch (SQLException e) {
      System.out.println("Errore nelle connessione al database.");
      e.printStackTrace();
    }

    System.out.println("Numero risultati: " + utenti.size());

    return utenti;
  }




  public boolean save(Utente utente){
    System.out.println("Chiavi ricevute: " + utente.getNome());
    String q = "INSERT INTO UtentiRegistrati (id_utente, nome, cognome, email, codice_fiscale, password, userId) VALUES (" +
      "?, ?, ?, ?, ?, ?, ?);";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, utente.getId_utente());
      ps.setString(2, utente.getNome());
      ps.setString(3, utente.getCognome());
      ps.setString(4, utente.getEmail());
      ps.setString(5, utente.getCodiceFiscale());
      ps.setString(6, utente.getPassword());
      ps.setString(7, utente.getUserId());

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
