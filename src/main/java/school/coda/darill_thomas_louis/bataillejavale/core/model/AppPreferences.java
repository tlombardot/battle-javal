package school.coda.darill_thomas_louis.bataillejavale.core.model;

public class AppPreferences {
    public boolean sonActive = true;
    public double volumeMusique = 0.5;
    public double volumeEffets = 0.8;

    public boolean ravitaillementActive = false;
    public boolean evenementsActive = false;
    public boolean systemeRecompensesActive = true;

    // 🚨 Les informations sensibles ne devraient pas être en dur dans le code
    // Il est préférable de les charger via des variablesd d'environnement
    // ou via des fichiers de properties dont les valeurs peuvent être fournies lors de l'exécution
    public String dbConnectionString = "jdbc:postgresql://aws-1-eu-central-2.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.wswxgvauucumhkgbssnx";
    private static final String PASSWORD = "hL3VHKQ2iPgvIJXO";

    public int largeurGrilleDefaut = 10;
    public int hauteurGrilleDefaut = 10;
}
