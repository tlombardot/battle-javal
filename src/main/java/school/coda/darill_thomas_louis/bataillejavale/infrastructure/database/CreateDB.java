package school.coda.darill_thomas_louis.bataillejavale.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDB {

    public CreateDB() {
        initDatabase();
    }
    // L'URL de Supabase se trouve dans Settings > Database > Connection String > JDBC
    private static final String URL = "jdbc:postgresql://aws-1-eu-central-2.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.wswxgvauucumhkgbssnx";
    private static final String PASSWORD = "DarillisGay";

    /**
     * Fonction pour établir la connexion avec la base de données PostgreSQL.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Initialise les tables nécessaires au lancement du jeu.
     */
    public void initDatabase() {
        String sqlJoueurs = """
            CREATE TABLE IF NOT EXISTS joueurs (
                id SERIAL PRIMARY KEY,
                pseudo VARCHAR(50) UNIQUE NOT NULL,
                mot_de_passe VARCHAR(255) NOT NULL,
                preferences JSONB default '{}',
                succes_debloques JSONB default '{}',
                stats JSONB default '{}',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String sqlParties = """
            CREATE TABLE IF NOT EXISTS parties (
                id SERIAL PRIMARY KEY,
                joueur1_id INT REFERENCES joueurs(id),
                joueur2_id INT REFERENCES joueurs(id),
                tour_courant INT DEFAULT 1,
                etat_jeu JSONB NOT NULL,
                statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
                winner_id INT REFERENCES joueurs(id),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Création des tables
            stmt.execute(sqlJoueurs);
            stmt.execute(sqlParties);
            IO.println("Base de données initialisée avec succès !");

        } catch (SQLException e) {
            IO.println("Erreur lors de l'initialisation de la base de données : " + e.getMessage());
        }
    }


}
