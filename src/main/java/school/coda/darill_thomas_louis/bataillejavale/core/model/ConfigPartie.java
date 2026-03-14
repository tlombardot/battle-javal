package school.coda.darill_thomas_louis.bataillejavale.core.model;

/**
 * Représente la configuration finale d'une partie.
 * Elle fusionne les Préférences Générales (socle) avec les choix spécifiques de la partie (override).
 */
public class ConfigPartie {

    // les paramètres spécifiques
    private int largeurGrille;
    private int hauteurGrille;
    private boolean evenementsActifs;
    private boolean ravitaillementActif;
    // les paramètres globaux
    private final boolean sonActif;
    private final double volumeMusique;
    private final double volumeEffets;
    private final boolean systemeRecompensesActif;

    public ConfigPartie(AppPreferences globales) {
        this.largeurGrille = globales.largeurGrilleDefaut;
        this.hauteurGrille = globales.hauteurGrilleDefaut;
        this.evenementsActifs = globales.evenementsActive;
        this.ravitaillementActif = globales.ravitaillementActive;

        this.sonActif = globales.sonActive;
        this.volumeMusique = globales.volumeMusique;
        this.volumeEffets = globales.volumeEffets;
        this.systemeRecompensesActif = globales.systemeRecompensesActive;
    }

    public int getLargeurGrille() { return largeurGrille; }
    public int getHauteurGrille() { return hauteurGrille; }
    public boolean isEvenementsActifs() { return evenementsActifs; }
    public boolean isRavitaillementActif() { return ravitaillementActif; }
    public boolean isSonActif() { return sonActif; }
    public double getVolumeMusique() { return volumeMusique; }
    public double getVolumeEffets() { return volumeEffets; }
    public boolean isSystemeRecompensesActif() { return systemeRecompensesActif; }

    public void setDimensionsGrille(int taille) {
        this.largeurGrille = taille;
        this.hauteurGrille = taille;
    }
    public void setEvenementsActifs(boolean evenementsActifs) { this.evenementsActifs = evenementsActifs; }
    public void setRavitaillementActif(boolean ravitaillementActif) { this.ravitaillementActif = ravitaillementActif; }
}