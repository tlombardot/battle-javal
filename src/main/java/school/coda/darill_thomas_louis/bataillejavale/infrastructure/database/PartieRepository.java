package school.coda.darill_thomas_louis.bataillejavale.infrastructure.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PartieRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    // Enregistre le début de la partie et renvoie l'ID généré par PostgreSQL
    public int creerNouvellePartie(EtatJeu etatJeu) {
        String sql = "INSERT INTO parties (etat_jeu, statut) VALUES (?::jsonb, 'EN_COURS') RETURNING id;";

        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mapper.writeValueAsString(etatJeu));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException | JsonProcessingException e) {
            IO.println("Erreur lors de la création en DB : " + e.getMessage());
        }
        return -1;
    }

    // Écrase l'ancien état du jeu par le nouveau après chaque tour
    public void mettreAJourPartie(int idPartie, EtatJeu etatJeu) {
        if (idPartie == -1) return; // Sécurité si la partie n'a pas d'ID

        String sql = "UPDATE parties SET etat_jeu = ?::jsonb, tour_courant = ? WHERE id = ?;";

        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mapper.writeValueAsString(etatJeu));
            pstmt.setInt(2, etatJeu.getTourCourant());
            pstmt.setInt(3, idPartie);

            pstmt.executeUpdate();

        } catch (SQLException | JsonProcessingException e) {
            IO.println("Erreur lors de la MAJ en DB : " + e.getMessage());
        }
    }

    // Permet de charger une partie existante depuis PostgreSQL
    public EtatJeu chargerPartie(int idPartie) {
        // La condition statut = 'EN_COURS' empêche de charger une partie finie !
        String sql = "SELECT etat_jeu FROM parties WHERE id = ? AND statut = 'EN_COURS'";

        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPartie);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String json = rs.getString("etat_jeu");
                return mapper.readValue(json, EtatJeu.class);
            }

        } catch (SQLException | JsonProcessingException e) {
            IO.println("Erreur lors du chargement en DB : " + e.getMessage());
        }
        return null;
    }

    public void terminerPartie(int idPartie, String resultatFinal) {
        if (idPartie == -1) return;
        String sql = "UPDATE parties SET statut = ? WHERE id = ?;";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resultatFinal); // Ex: 'VICTOIRE' ou 'DEFAITE'
            pstmt.setInt(2, idPartie);
            pstmt.executeUpdate();
            IO.println("Partie " + idPartie + " verrouillée en base (" + resultatFinal + ").");
        } catch (SQLException e) {
            IO.println("Erreur de verrouillage DB : " + e.getMessage());
        }
    }
}