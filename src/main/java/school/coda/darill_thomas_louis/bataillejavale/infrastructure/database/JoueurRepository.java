package school.coda.darill_thomas_louis.bataillejavale.infrastructure.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import school.coda.darill_thomas_louis.bataillejavale.core.model.AppPreferences;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Session;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.config.PreferencesManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JoueurRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    public void connexionAutomatique() {
        String pseudoOS = System.getProperty("user.name");
        System.out.println("Tentative de connexion pour : " + pseudoOS);

        String sqlSelect = "SELECT id, stats, preferences FROM joueurs WHERE pseudo = ?";
        String sqlInsert = "INSERT INTO joueurs (pseudo, mot_de_passe, stats) VALUES (?, 'auto_login', '{\"victoires\":0, \"defaites\":0}'::jsonb) RETURNING id";

        try (Connection conn = CreateDB.getConnection()) {
            try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
                pstmtSelect.setString(1, pseudoOS);
                ResultSet rs = pstmtSelect.executeQuery();

                if (rs.next()) {
                    Session.idJoueur = rs.getInt("id");
                    Session.pseudo = pseudoOS;
                    chargerStats(rs.getString("stats")); // je lit... nous lisons ^^ le JSON de la DB

                    chargerPreferences(rs.getString("preferences"));

                    IO.println("Bon retour " + Session.pseudo + " ! (V:" + Session.victoires + " / D:" + Session.defaites + ")");
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

    /**
     *
     */
    private void chargerPreferences(String prefsJson) {
        try {
            if (prefsJson != null && !prefsJson.isEmpty() && !prefsJson.equals("{}")) {
                JsonNode root = mapper.readTree(prefsJson);

                AppPreferences prefs = PreferencesManager.getInstance().getPreferences();

                if (root.has("sonActive")) prefs.sonActive = root.get("sonActive").asBoolean();
                if (root.has("volumeMusique")) prefs.volumeMusique = root.get("volumeMusique").asDouble();
                if (root.has("ravitaillementActive")) prefs.ravitaillementActive = root.get("ravitaillementActive").asBoolean();
                if (root.has("evenementsActive")) prefs.evenementsActive = root.get("evenementsActive").asBoolean();

                IO.println("Préférences Cloud chargées en mémoire !");
                PreferencesManager.getInstance().sauvegarderPreferences();
            }
        } catch (Exception e) {
            IO.println("Erreur deparsing préférences depuis la DB : " + e.getMessage());
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

    /**
     *
     */
    public void sauvegarderPreferencesDansCloud(AppPreferences prefs) {
        if (Session.idJoueur == 0) {
            System.out.println("Le joueur n'est pas connecté, sauvegarde ignorée.");
            return;
        }

        String sql = "UPDATE joueurs SET preferences = ?::jsonb WHERE id = ?";

        try (Connection conn = CreateDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ObjectNode prefsNode = mapper.createObjectNode();
            prefsNode.put("sonActive", prefs.sonActive);
            prefsNode.put("volumeMusique", prefs.volumeMusique);
            prefsNode.put("ravitaillementActive", prefs.ravitaillementActive);
            prefsNode.put("evenementsActive", prefs.evenementsActive);

            pstmt.setString(1, mapper.writeValueAsString(prefsNode));
            pstmt.setInt(2, Session.idJoueur);

            pstmt.executeUpdate();
            System.out.println("[MES LOGS JOYEUX (Darill logs...)] -> Préférences synchronisées dans le Cloud avec succès !");

        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde des préférences dans la DB : " + e.getMessage());
        }
    }
}