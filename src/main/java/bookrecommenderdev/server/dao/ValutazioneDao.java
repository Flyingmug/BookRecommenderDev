package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Valutazione;
import bookrecommenderdev.server.dto.PaginaValutazioni;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static bookrecommenderdev.Constants.REVIEWS_PAGE_SIZE;

public class ValutazioneDao {

  private final DataSource datasource;
  public ValutazioneDao(DataSource ds) { this.datasource = ds; }

  /**
   * todo documentation
   * */
  public Optional<Valutazione> get(int id_libro, int id_utente) throws SQLException {
    final String q = "SELECT * FROM valutazionilibri WHERE id_libro = ? AND id_utente = ?";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);
      ps.setInt(2, id_utente);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();

        Valutazione v = mapValutazione(rs);
        return Optional.of(v);
      }

    }
  }

  /**
   * todo doc
   * */
  public PaginaValutazioni getPage(int indicePagina, int id_libro) throws SQLException {
    final String q =
        "SELECT *, COUNT(*) OVER() as numero_risultati" +
            " FROM valutazionilibri" +
            " WHERE id_libro = ?" +
            " OFFSET ? LIMIT ?";

    List<Valutazione> valutazioni = new ArrayList<>();
    int totalCount = 0;

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);
      ps.setInt(2, indicePagina * REVIEWS_PAGE_SIZE);
      ps.setInt(3, REVIEWS_PAGE_SIZE);

      try (ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {

          totalCount = rs.getInt("numero_risultati");
          do {
            valutazioni.add(mapValutazione(rs));
          } while (rs.next());
        }
      }
    }

    return new PaginaValutazioni(valutazioni, totalCount);
  }

  /**
  * todo doc
  * aggiunge o aggiorna se presente ...
  * */
  public boolean save(Valutazione valutazione) throws SQLException {
    final String q = "INSERT INTO valutazionilibri (" +
      " id_utente, id_libro, stile, contenuto, gradevolezza, originalita, edizione," +
      " recensione_stile, recensione_contenuto, recensione_gradevolezza, recensione_originalita, recensione_edizione, recensione_generale)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
      " ON CONFLICT (id_utente, id_libro) DO UPDATE SET" +
      " stile = EXCLUDED.stile," +
      " contenuto = EXCLUDED.contenuto," +
      " gradevolezza = EXCLUDED.gradevolezza," +
      " originalita = EXCLUDED.originalita," +
      " edizione = EXCLUDED.edizione," +
      " recensione_stile = EXCLUDED.recensione_stile," +
      " recensione_contenuto = EXCLUDED.recensione_contenuto," +
      " recensione_gradevolezza = EXCLUDED.recensione_gradevolezza," +
      " recensione_originalita = EXCLUDED.recensione_originalita," +
      " recensione_edizione = EXCLUDED.recensione_edizione," +
      " recensione_generale = EXCLUDED.recensione_generale;";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, valutazione.getIdUtente());
      ps.setInt(2, valutazione.getIdLibro());

      ps.setInt(3, valutazione.getStile());
      ps.setInt(4, valutazione.getContenuto());
      ps.setInt(5, valutazione.getGradevolezza());
      ps.setInt(6, valutazione.getOriginalita());
      ps.setInt(7, valutazione.getEdizione());

      // if null in Java, it will be NULL in the database.
      ps.setString(8,  valutazione.getRecensioneStile());
      ps.setString(9,  valutazione.getRecensioneContenuto());
      ps.setString(10, valutazione.getRecensioneGradevolezza());
      ps.setString(11, valutazione.getRecensioneOriginalita());
      ps.setString(12, valutazione.getRecensioneEdizione());
      ps.setString(13, valutazione.getRecensioneGenerale());

      return ps.executeUpdate() > 0;
    }
  }

  /**
   * todo doc
   * */
  public boolean delete(int id_libro, int id_utente) throws SQLException {
    final String q =
        "DELETE FROM valutazionilibri WHERE id_libro = ? AND id_utente = ?";

    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);
      ps.setInt(2, id_utente);

      return ps.executeUpdate() > 0;
    }
  }

  /**
   * Calcola le medie delle valutazioni dei punteggi di un libro.
   * todo doc
   * @param id_libro Id di un libro.
   * @return Array di 5 float contenente le cinque medie.
   */
  public Optional<double[]> getAverageScores(int id_libro) throws SQLException {
    final String q =
        "SELECT Count(*) as totalCount," +
        " AVG(stile) as stile," +
        " AVG(contenuto) as contenuto," +
        " AVG(gradevolezza) as gradevolezza," +
        " AVG(originalita) as originalita," +
        " AVG(edizione) as edizione" +
        " FROM ValutazioniLibri" +
        " WHERE id_libro = ?";


    try (Connection conn = datasource.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {

      ps.setInt(1, id_libro);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();

        int totalCount = rs.getInt("totalCount");
        if (totalCount == 0) return Optional.empty();

        double[] avg = new double[5];
        avg[0] = rs.getDouble("stile");
        avg[1] = rs.getDouble("contenuto");
        avg[2] = rs.getDouble("gradevolezza");
        avg[3] = rs.getDouble("originalita");
        avg[4] = rs.getDouble("edizione");

        return Optional.of(avg);
      }

    }
  }

  private Valutazione mapValutazione(ResultSet rs) throws SQLException {
    return new Valutazione(
        rs.getInt("id_libro"),
        rs.getInt("id_utente"),
        rs.getInt("stile"),
        rs.getInt("contenuto"),
        rs.getInt("gradevolezza"),
        rs.getInt("originalita"),
        rs.getInt("edizione"),
        rs.getString("recensione_stile"),
        rs.getString("recensione_contenuto"),
        rs.getString("recensione_gradevolezza"),
        rs.getString("recensione_originalita"),
        rs.getString("recensione_edizione"),
        rs.getString("recensione_generale")
    );
  }
}
