package school.coda.darill_thomas_louis.bataillejavale.infrastructure.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PartieRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    public static class PartieInfo {
        public final int id;
        public final String statut;
        public final int tour;
        public final String date;

        public PartieInfo(int id, String statut, int tour, String date) {
            this.id = id;
            this.statut = statut;
            this.tour = tour;
            this.date = date;
        }
    }

    public List<PartieInfo> getListePartiesJoueur() {
        List<PartieInfo> list = new java.util.ArrayList<>();

        // On récupère les parties où le joueur est impliqué, triées par les plus récentes
        String sql = "SELECT id, statut, tour_courant, created_at FROM parties " +
                "WHERE (joueur1_id = ? OR joueur2_id = ?) " +
                "AND statut = 'EN_COURS' " +
                "ORDER BY created_at DESC LIMIT 20";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Session.idJoueur);
            pstmt.setInt(2, Session.idJoueur);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // On formate un peu la date pour que ce soit joli à l'écran
                String datePropre = rs.getTimestamp("created_at").toString().substring(0, 16);

                list.add(new PartieInfo(
                        rs.getInt("id"),
                        rs.getString("statut"),
                        rs.getInt("tour_courant"),
                        datePropre
                ));
            }
        } catch (SQLException e) {
            IO.println("Erreur chargement liste parties : " + e.getMessage());
        }
        return list;
    }

    // Enregistre le début de la partie et renvoie l'ID généré par PostgreSQL
    public int creerNouvellePartie(EtatJeu etatJeu) {
        String sql = "INSERT INTO parties (joueur1_id, etat_jeu, statut) VALUES (?, ?::jsonb, 'EN_COURS') RETURNING id;";

        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Session.idJoueur);

            pstmt.setString(2, mapper.writeValueAsString(etatJeu));

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

    public int hebergerPartieMulti(EtatJeu etatJeu) {
        String sql = "INSERT INTO parties (joueur1_id, etat_jeu, statut) VALUES (?, ?::jsonb, 'ATTENTE_P2') RETURNING id;";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, school.coda.darill_thomas_louis.bataillejavale.core.model.Session.idJoueur);
            pstmt.setString(2, mapper.writeValueAsString(etatJeu));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException | JsonProcessingException e) {
            IO.println("Erreur d'hébergement : " + e.getMessage());
        }
        return -1;
    }

    public EtatJeu chargerSalonAttente(int idPartie) {
        String sql = "SELECT etat_jeu FROM parties WHERE id = ? AND statut = 'ATTENTE_P2'";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPartie);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapper.readValue(rs.getString("etat_jeu"), EtatJeu.class);
            }
        } catch (SQLException | JsonProcessingException e) {
            IO.println("Erreur de chargement du salon : " + e.getMessage());
        }
        return null;
    }

    public void demarrerPartieMulti(int idPartie, EtatJeu etatJeuComplet) {
        String sql = "UPDATE parties SET joueur2_id = ?, etat_jeu = ?::jsonb, statut = 'EN_COURS' WHERE id = ?";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, school.coda.darill_thomas_louis.bataillejavale.core.model.Session.idJoueur);
            pstmt.setString(2, mapper.writeValueAsString(etatJeuComplet));
            pstmt.setInt(3, idPartie);
            pstmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            IO.println("Erreur au démarrage multi : " + e.getMessage());
        }
    }

    public String getStatutPartie(int idPartie) {
        String sql = "SELECT statut FROM parties WHERE id = ?";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPartie);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("statut");
        } catch (SQLException e) {
            IO.println("Erreur de statut : " + e.getMessage());
        }
        return "ERREUR";
    }

    public EtatJeu chargerPartieActiveOuTerminee(int idPartie) {
        String sql = "SELECT etat_jeu FROM parties WHERE id = ?";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPartie);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapper.readValue(rs.getString("etat_jeu"), EtatJeu.class);
            }
        } catch (SQLException | JsonProcessingException e) {
            IO.println("Erreur de polling DB : " + e.getMessage());
        }
        return null;
    }
}