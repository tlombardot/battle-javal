package school.coda.darill_thomas_louis.bataillejavale.infrastructure.database;

import school.coda.darill_thomas_louis.bataillejavale.core.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoueurRepository {

    public void connexionAutomatique() {
        String pseudoOS = System.getProperty("user.name");

        IO.println("Tentative de connexion pour le profil OS : " + pseudoOS);

        String sqlSelect = "SELECT id FROM joueurs WHERE pseudo = ?";
        String sqlInsert = "INSERT INTO joueurs (pseudo, mot_de_passe) VALUES (?, 'auto_login') RETURNING id";

        try (Connection conn = CreateDB.getConnection()) {

            try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
                pstmtSelect.setString(1, pseudoOS);
                ResultSet rs = pstmtSelect.executeQuery();

                if (rs.next()) {
                    Session.idJoueur = rs.getInt("id");
                    Session.pseudo = pseudoOS;
                    IO.println("Bon retour, Commandant " + pseudoOS + " ! (ID: " + Session.idJoueur + ")");
                    return;
                }
            }

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                pstmtInsert.setString(1, pseudoOS);
                ResultSet rsInsert = pstmtInsert.executeQuery();

                if (rsInsert.next()) {
                    Session.idJoueur = rsInsert.getInt("id");
                    Session.pseudo = pseudoOS;
                    IO.println("Nouveau profil créé pour le Commandant " + pseudoOS + " ! (ID: " + Session.idJoueur + ")");
                }
            }

        } catch (SQLException e) {
            IO.println("Erreur lors de la connexion/création du joueur : " + e.getMessage());
        }
    }
}