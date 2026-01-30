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
   * Recupera un libro in forma minimale (“basic”) tramite id.
   * <p>
   * Se non esiste alcun libro con l’id indicato, restituisce {@link Optional#empty()}.
   *
   * @param id_libro id del libro
   * @return libro minimale se presente; {@link Optional#empty()} altrimenti
   * @throws SQLException per errori di accesso ai dati
   */
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
   * Recupera un libro in forma completa (“complete”) tramite id.
   * <p>
   * Include informazioni addizionali rispetto al metodo {@link #getBasic(int)} (es. autore/editore/categorie),
   * secondo il modello {@link Libro} usato dall’applicazione.
   * Se il libro non esiste, restituisce {@link Optional#empty()}.
   *
   * @param id_libro id del libro
   * @return libro completo se presente; {@link Optional#empty()} altrimenti
   * @throws SQLException per errori di accesso ai dati
   */
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
   * Esegue una ricerca paginata di libri in base a una {@link SearchRequest}.
   * <p>
   * Semantica:
   * <ul>
   *   <li>Se {@code req} è {@code null} o {@code req.getTipo()} è {@code null}, ritorna una pagina vuota.</li>
   *   <li>Se i campi richiesti dal tipo di ricerca sono vuoti/non validi, ritorna una pagina vuota.</li>
   *   <li>{@code indicePagina} è 0-based; valori negativi sono trattati come 0.</li>
   *   <li>La dimensione pagina è {@link bookrecommenderdev.Constants#PAGE_SIZE}.</li>
   * </ul>
   *
   * @param req         richiesta di ricerca (tipo + campi)
   * @param indicePagina indice pagina (0-based)
   * @return pagina di risultati con conteggio totale (può essere vuota)
   * @throws SQLException per errori di accesso ai dati
   */
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
   * Esegue una query di ricerca paginata e costruisce una {@link PaginaLibriRisultati}.
   * <p>
   * Il {@code Binder} ricevuto si occupa di impostare i parametri sul {@link PreparedStatement}.
   * Se la query non produce righe, la pagina risultante avrà lista vuota e totalCount = 0.
   *
   * @param sql         query SQL parametrizzata
   * @param fieldBinder binder dei parametri
   * @return pagina risultati (anche vuota)
   * @throws SQLException per errori di accesso ai dati
   */
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

