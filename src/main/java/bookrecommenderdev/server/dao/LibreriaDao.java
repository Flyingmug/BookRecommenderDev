package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Libreria;
import bookrecommenderdev.server.dto.LibraryResult;
import javafx.util.Pair;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class LibreriaDao {
  private final DataSource datasource;
  public LibreriaDao(DataSource ds) { this.datasource = ds; }


  //Get librerie
  public List<LibraryResult> getLibrerie (long id_utente) throws SQLException {
    List<LibraryResult> elenco = new LinkedList<>();

    System.out.println("Chiave ricevuta: " + id_utente);

    String q = "SELECT l.id_libreria, l.nome AS nome_libreria, COUNT(c.id_libro) AS numero_libri " +
            " FROM Librerie l LEFT JOIN Libreria_Contiene c ON l.id_libreria = c.id_libreria " +
            " WHERE l.id_utente = ? GROUP BY l.id_libreria, l.nome;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setLong(1, id_utente);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        elenco.add(
          new LibraryResult(
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

    }

    return elenco;
  }

  //show Libreria
/*  // show Libreria -> restituisce solo i libri di una libreria
  public List<Libro> showLibreria(int id_libreria) {
    System.out.println("Chiave ricevuta: " + id_libreria);

    String q = "SELECT b.id_libro, b.titolo, b.anno_pubblicazione, " +
      "a.id_autore, a.nome_autore, " +
      "e.id_editore, e.nome_editore, " +
      "c.id_categoria, c.nome_categoria " +
      "FROM Contiene co " +
      "INNER JOIN Libri b ON co.id_libro = b.id_libro " +
      "LEFT JOIN Autori a ON b.id_autore = a.id_autore " +
      "LEFT JOIN Editori e ON b.id_editore = e.id_editore " +
      "LEFT JOIN Libri_Categorie lc ON b.id_libro = lc.id_libro " +
      "LEFT JOIN Categorie c ON lc.id_categoria = c.id_categoria " +
      "WHERE co.id_libreria = ? " +
      "ORDER BY b.titolo;";

    List<Libro> libri = new LinkedList<>();

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libreria);
      ResultSet rs = ps.executeQuery();

      // per evitare duplicati dello stesso libro con più categorie
      java.util.Map<Integer, Libro> libriMap = new java.util.HashMap<>();

      while (rs.next()) {
        int idLibro = rs.getInt("id_libro");
        Libro libro = libriMap.get(idLibro);

        if (libro == null) {
          libro = new Libro(
            idLibro,
            rs.getString("titolo"),
            rs.getInt("anno_pubblicazione")
          );

          // autore
          if (rs.getInt("id_autore") != 0) {
            libro.setAutore(new Autore(
              rs.getInt("id_autore"),
              rs.getString("nome_autore")
            ));
          }

          // editore
          if (rs.getInt("id_editore") != 0) {
            libro.setEditore(new Editore(
              rs.getInt("id_editore"),
              rs.getString("nome_editore")
            ));
          }

          libriMap.put(idLibro, libro);
        }

        // categoria (aggiungi se presente)
        int idCategoria = rs.getInt("id_categoria");
        if (idCategoria != 0) {
          libro.addCategoria(new Categoria(
            idCategoria,
            rs.getString("nome_categoria")
          ));
        }
      }

      libri.addAll(libriMap.values());

    } catch (SQLException e) {
      System.out.println("Errore nella connessione al database.");
      e.printStackTrace();
    }

    return libri;
  }*/

  /**
   *
   * @param id_libreria
   * @return booleano per verificare se la cancellazione ha avuto successo
   */
  public boolean deleteLibreria(int id_libreria){
    System.out.println("Chiavi ricevute: " + id_libreria);

    String q = "DELETE FROM Libreria WHERE id_libreria = ?;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libreria);

      int rowsAffected = ps.executeUpdate(q);
      System.out.println("Righe eliminate: " + rowsAffected);
      return rowsAffected>0;
    } catch (SQLException e) {
      System.out.println("Errore nelle connessione al database.");
      e.printStackTrace();
    }
    return false;
  }
}
