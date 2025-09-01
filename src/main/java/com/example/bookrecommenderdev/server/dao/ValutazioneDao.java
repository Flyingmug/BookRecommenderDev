package com.example.bookrecommenderdev.server.dao;

import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.model.Valutazione;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ValutazioneDao
{
    private final DataSource datasource;

    public ValutazioneDao(DataSource ds) {
        this.datasource = ds;
    }

    /**
     * Cerca medie delle valutazioni dei punteggi
     * @param id_libro
     * @return array di 5 float contentente le cinque medie
     */
    public float[] getAverage (int id_libro){

        System.out.println("Chiave ricevuta: " + id_libro);
        float[] averageScores = new float[5];


        String q = "SELECT AVG(stile) as stile, AVG(contenuto) as contenuto, AVG(gradevolezza) as gradevolezza, AVG(originalita) as originalita, AVG(edizione) as edizione FROM Valutazioni WHERE id_libro = ?";

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

        } catch (SQLException e) {
            System.out.println("Errore nelle connessione al database.");
            e.printStackTrace();
        }

        return averageScores;
    }

    public List<Valutazione> getAll (int id_libro){
        List<Valutazione> elenco = new LinkedList<Valutazione>();

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

    public Valutazione get (int id_libro, int id_utente){
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


    public boolean save (int id_libro, int id_utente, Valutazione valutazione){

        System.out.println("Chiavi ricevute: " + id_libro);

        String q = "INSERT INTO Valutazioni (";

        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, id_libro);

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
