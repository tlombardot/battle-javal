package school.coda.darill_thomas_louis.bataillejavale.core.model;

public class EtatJeu {

    private GrilleOcean oceanJoueur1;
    private GrilleRadar radarJoueur1;


    private GrilleOcean oceanJoueur2;
    private GrilleRadar radarJoueur2;

    private boolean tourDuJoueur1;

    public EtatJeu() {
        this.oceanJoueur1 = new GrilleOcean();
        this.radarJoueur1 = new GrilleRadar();

        this.oceanJoueur2 = new GrilleOcean();
        this.radarJoueur2 = new GrilleRadar();

        this.tourDuJoueur1 = true;
    }

    // --- Getters et Setters pour le JSON ---

    public GrilleOcean getOceanJoueur1() { return oceanJoueur1; }
    public void setOceanJoueur1(GrilleOcean oceanJoueur1) { this.oceanJoueur1 = oceanJoueur1; }

    public GrilleRadar getRadarJoueur1() { return radarJoueur1; }
    public void setRadarJoueur1(GrilleRadar radarJoueur1) { this.radarJoueur1 = radarJoueur1; }

    public GrilleOcean getOceanJoueur2() { return oceanJoueur2; }
    public void setOceanJoueur2(GrilleOcean oceanJoueur2) { this.oceanJoueur2 = oceanJoueur2; }

    public GrilleRadar getRadarJoueur2() { return radarJoueur2; }
    public void setRadarJoueur2(GrilleRadar radarJoueur2) { this.radarJoueur2 = radarJoueur2; }

    public boolean isTourDuJoueur1() { return tourDuJoueur1; }
    public void setTourDuJoueur1(boolean tourDuJoueur1) { this.tourDuJoueur1 = tourDuJoueur1; }
}
