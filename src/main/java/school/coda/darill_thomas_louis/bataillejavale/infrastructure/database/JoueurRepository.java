package school.coda.darill_thomas_louis.bataillejavale.infrastructure.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JoueurRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    public void connexionAutomatique() {
        String pseudoOS = System.getProperty("user.name");
        System.out.println("Tentative de connexion pour : " + pseudoOS);

        String sqlSelect = "SELECT id, stats FROM joueurs WHERE pseudo = ?";
        String sqlInsert = "INSERT INTO joueurs (pseudo, mot_de_passe, stats) VALUES (?, 'auto_login', '{\"victoires\":0, \"defaites\":0}'::jsonb) RETURNING id";

        try (Connection conn = CreateDB.getConnection()) {
            try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
                pstmtSelect.setString(1, pseudoOS);
                ResultSet rs = pstmtSelect.executeQuery();

                if (rs.next()) {
                    Session.idJoueur = rs.getInt("id");
                    Session.pseudo = pseudoOS;
                    chargerStats(rs.getString("stats")); // On lit le JSON de la DB
                    System.out.println("Bon retour " + Session.pseudo + " ! (V:" + Session.victoires + " / D:" + Session.defaites + ")");
                    return;
                }
            }

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                pstmtInsert.setString(1, pseudoOS);
                ResultSet rsInsert = pstmtInsert.executeQuery();

                if (rsInsert.next()) {
                    Session.idJoueur = rsInsert.getInt("id");
                    Session.pseudo = pseudoOS;
                    Session.victoires = 0;
                    Session.defaites = 0;
                }
            }
        } catch (Exception e) {
            IO.println("Erreur connexion joueur : " + e.getMessage());
        }
    }

    private void chargerStats(String statsJson) {
        try {
            if (statsJson != null && !statsJson.isEmpty() && !statsJson.equals("{}")) {
                JsonNode root = mapper.readTree(statsJson);
                if (root.has("victoires")) Session.victoires = root.get("victoires").asInt();
                if (root.has("defaites")) Session.defaites = root.get("defaites").asInt();
            } else {
                Session.victoires = 0;
                Session.defaites = 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur parsing stats : " + e.getMessage());
        }
    }

    public void updateStats(boolean victoire) {
        if (victoire) Session.victoires++;
        else Session.defaites++;

        String sql = "UPDATE joueurs SET stats = ?::jsonb WHERE id = ?";
        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ObjectNode statsNode = mapper.createObjectNode();
            statsNode.put("victoires", Session.victoires);
            statsNode.put("defaites", Session.defaites);

            pstmt.setString(1, mapper.writeValueAsString(statsNode));
            pstmt.setInt(2, Session.idJoueur);
            pstmt.executeUpdate();

        } catch (Exception e) {
            IO.println("Erreur sauvegarde stats : " + e.getMessage());
        }
    }
}