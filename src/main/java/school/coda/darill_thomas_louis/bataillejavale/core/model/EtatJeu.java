package school.coda.darill_thomas_louis.bataillejavale.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EtatJeu {

    private JoueurPlay joueur1;
    private JoueurPlay joueur2;
    private int tourCourant;

    private boolean tourDuJoueur1;

    public EtatJeu() {
        this.joueur1 = new JoueurPlay("Joueur 1");
        this.joueur2 = new JoueurPlay("CPU");

        this.tourDuJoueur1 = true;
    }

    // --- Getters et Setters pour le JSON ---

    public JoueurPlay getJoueur1() { return joueur1; }
    public void setJoueur1(JoueurPlay joueur1) { this.joueur1 = joueur1; }

    public JoueurPlay getJoueur2() { return joueur2; }
    public void setJoueur2(JoueurPlay joueur2) { this.joueur2 = joueur2; }

    public int getTourCourant() { return tourCourant; }
    public void setTourCourant(int tourCourant) { this.tourCourant = tourCourant; }

    @JsonIgnore
    public int getMancheActuelle() {
        return (tourCourant / 2) + 1;
    }
}
