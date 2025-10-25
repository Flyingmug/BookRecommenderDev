package bookrecommenderdev.server.dao;

import bookrecommenderdev.model.Valutazione;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ValutazioneDao
{
    private final DataSource datasource;
    public ValutazioneDao(DataSource ds) { this.datasource = ds; }

    /**
     * Calcola le medie delle valutazioni dei punteggi di un libro
     *
     * @param id_libro id di un libro
     * @return array di 5 float contenente le cinque medie
     */
    public double[] getAverage(int id_libro) throws SQLException {

        System.out.println("Chiave ricevuta: " + id_libro);
        double[] averageScores = new double[5];


        String q = "SELECT AVG(stile) as stile, AVG(contenuto) as contenuto, AVG(gradevolezza) as gradevolezza, AVG(originalita) as originalita, AVG(edizione) as edizione FROM ValutazioniLibri WHERE id_libro = ?";

        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, id_libro);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                averageScores[0] = rs.getInt("stile");
                averageScores[1] = rs.getInt("contenuto");
                averageScores[2] = rs.getInt("gradevolezza");
                averageScores[3] = rs.getInt("originalita");
                averageScores[4] = rs.getInt("edizione");
            }

            System.out.println("Numero risultati: " +
                    averageScores[0] +
                    averageScores[1] +
                    averageScores[2] +
                    averageScores[3] +
                    averageScores[4]);

            return averageScores[0] > 0 ? averageScores : null;
        }
    }

    public List<Valutazione> getAll (int id_libro){
        List<Valutazione> elenco = new LinkedList<>();

        System.out.println("Chiave ricevuta: " + id_libro);

        String q = "SELECT stile, contenuto, gradevolezza, originalita, edizione, " +
                "recensione_stile, recensione_contenuto, recensione_gradevolezza, recensione_originalita, recensione_edizione " +
                "FROM  Valutazioni WHERE id_libro = ?";

        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, id_libro);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                elenco.add(new Valutazione(rs.getInt("stile"),
                        rs.getInt("contenuto"),
                        rs.getInt("gradevolezza"),
                        rs.getInt("originalita"),
                        rs.getInt("edizione"),
                        rs.getString("recensione_stile"),
                        rs.getString("recensione_contenuto"),
                        rs.getString("recensione_gradevolezza"),
                        rs.getString("recensione_originalita"),
                        rs.getString("recensione_edizione")));
            }

            System.out.println("Numero risultati: "+ elenco.size());

        } catch (SQLException e) {
            System.out.println("Errore nelle connessione al database.");
            e.printStackTrace();
        }
        return elenco;
    }

    public Valutazione get(int id_libro, int id_utente){
        Valutazione valutazione = null;
        System.out.println("Chiave ricevuta: " + id_libro);
        String q = "SELECT * FROM Valutazioni WHERE id_libro = ? AND id_utente = ?";

        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, id_libro);
            ps.setInt(2, id_utente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                valutazione = new Valutazione(rs.getInt("id_libro"),
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
                        rs.getString("recensione_edizione"));
            }

            System.out.println("Valutazione istanziata: " + (valutazione != null));

        } catch (SQLException e) {
            System.out.println("Errore nelle connessione al database.");
            e.printStackTrace();
        }
        return valutazione;
    }


    public boolean save (int id_libro, Valutazione valutazione){
        System.out.println("Chiavi ricevute: " + id_libro);
        String q = "INSERT INTO Valutazioni (" +
          "id_utente, id_libro, stile, contenuto, gradevolezza, originalita, edizione, " +
          "recensione_stile, recensione_contenuto, recensione_gradevolezza, recensione_originalita, recensione_edizione) " +
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, valutazione.getIdUtente());
            ps.setInt(2, valutazione.getIdLibro());
            ps.setInt(3, valutazione.getStile());
            ps.setInt(4, valutazione.getContenuto());
            ps.setInt(5, valutazione.getGradevolezza());
            ps.setInt(6, valutazione.getOriginalita());
            ps.setInt(7, valutazione.getEdizione());
            ps.setString(8, valutazione.getRecensioneStile());
            ps.setString(9, valutazione.getRecensioneContenuto());
            ps.setString(10, valutazione.getRecensioneGradevolezzo());
            ps.setString(11, valutazione.getRecensioneOriginalita());
            ps.setString(12, valutazione.getRecensioneEdizione());

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
