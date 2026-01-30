package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.model.dto.PaginaValutazioni;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static bookrecommenderdev.Constants.REVIEWS_PAGE_SIZE;

/**
 * DAO per l’accesso e la gestione delle valutazioni/recensioni dei libri.
 * <p>
 * Incapsula le operazioni CRUD per {@link Valutazione} e fornisce funzioni di lettura
 * paginata e aggregazione (medie dei punteggi).
 * <p>
 * Il dettaglio delle query SQL è descritto nella documentazione tecnica esterna; qui viene
 * documentato il contratto dei metodi (semantica risultati, paginazione, idempotenza).
 */
public class ValutazioneDao {

  private final DataSource datasource;
  public ValutazioneDao(DataSource ds) { this.datasource = ds; }

  /**
   * Recupera la valutazione di un utente per un determinato libro.
   * <p>
   * Se l’utente non ha inserito alcuna valutazione per quel libro, restituisce
   * {@link Optional#empty()}.
   *
   * @param id_libro  id del libro
   * @param id_utente id dell’utente
   * @return valutazione se presente; {@link Optional#empty()} altrimenti
   * @throws SQLException per errori di accesso ai dati
   */
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
   * Restituisce una pagina di valutazioni associate a un libro.
   * <p>
   * {@code indicePagina} è 0-based.
   * Se non esistono valutazioni, la pagina risultante contiene lista vuota e totalCount = 0.
   * <p>
   * Nota: l’ordinamento è definito dalla query SQL (documentazione tecnica esterna).
   *
   * @param indicePagina indice pagina (0-based)
   * @param id_libro     id del libro
   * @return pagina con lista risultati e conteggio totale
   * @throws SQLException per errori di accesso ai dati
   */
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
   * Inserisce o aggiorna una valutazione.
   * <p>
   * L’operazione è un “upsert”: se esiste già una valutazione per la coppia
   * (utente, libro), viene aggiornata; altrimenti viene inserita.
   * <p>
   * Le stringhe recensione possono essere {@code null}: in tal caso vengono
   * persistite come {@code NULL} nel database.
   *
   * @param valutazione valutazione da salvare (insert/update)
   * @throws SQLException per errori di accesso ai dati o violazioni di vincoli
   */
  public void save(Valutazione valutazione) throws SQLException {
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

      ps.executeUpdate();
    }
  }

  /**
   * Elimina la valutazione di un utente per un determinato libro.
   * <p>
   * L’operazione è idempotente: se la valutazione non esiste, non viene sollevata
   * alcuna eccezione e il metodo restituisce {@code false}.
   *
   * @param id_libro  id del libro
   * @param id_utente id dell’utente
   * @return {@code true} se una riga è stata rimossa; {@code false} se non esisteva
   * @throws SQLException per errori di accesso ai dati
   */
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
   * Calcola le medie dei punteggi di valutazione per un libro.
   * <p>
   * Se non esistono valutazioni per il libro indicato, restituisce {@link Optional#empty()}.
   * L’array risultante contiene 5 valori (in quest’ordine):
   * <ol>
   *   <li>stile</li>
   *   <li>contenuto</li>
   *   <li>gradevolezza</li>
   *   <li>originalita</li>
   *   <li>edizione</li>
   * </ol>
   *
   * @param id_libro id del libro
   * @return {@link Optional} con array di 5 medie; {@link Optional#empty()} se nessuna valutazione
   * @throws SQLException per errori di accesso ai dati
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

  /**
   * Mappa la riga corrente del {@link ResultSet} in un oggetto {@link Valutazione}.
   * <p>
   * Metodo di utilità interno al DAO.
   *
   * @param rs result set posizionato su una riga valida
   * @return istanza di {@link Valutazione} popolata con i dati della riga corrente
   * @throws SQLException per errori di accesso ai dati
   */
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
