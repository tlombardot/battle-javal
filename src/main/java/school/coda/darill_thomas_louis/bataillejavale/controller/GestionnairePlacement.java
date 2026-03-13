package school.coda.darill_thomas_louis.bataillejavale.controller;

import javafx.scene.paint.Color;
import school.coda.darill_thomas_louis.bataillejavale.core.engine.MoteurJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.GrilleOcean;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.ui.GrilleUI;
import school.coda.darill_thomas_louis.bataillejavale.ui.PlateauDeJeu;

import java.util.ArrayList;
import java.util.Random;

public class GestionnairePlacement {

    private final PartieControleur controleur;
    private final PlateauDeJeu vue;
    private final Random random = new Random();

    public GestionnairePlacement(PartieControleur controleur, PlateauDeJeu vue) {
        this.controleur = controleur;
        this.vue = vue;
    }

    /**
     * Vider la grille du joueur
     * @param vueOcean
     * Grille du joueur
     */
    public void viderGrille(GrilleUI vueOcean) {
        controleur.getEtat().getJoueur1().getGrilleOcean().vider();
        controleur.getEtat().getJoueur1().getFlotte().clear();
        controleur.setFlotteRestante(new MoteurJeu().genererFlotteStandard());
        vue.actualiserMenuBateaux();
        vueOcean.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean());
        vue.getBtnPret().setDisable(true);
    }

    /**
     * Option permettant de posé les bâteaux aléatoirement
     * @param vueOcean
     * Grille du joueur
     */
    public void placerMesBateauxAleatoirement(GrilleUI vueOcean) {
        viderGrille(vueOcean);
        for (Vaisseau navire : new ArrayList<>(controleur.getFlotteRestante())) {
            boolean place = false;
            while (!place) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                boolean h = random.nextBoolean();
                if (controleur.getEtat().getJoueur1().getGrilleOcean().placerVaisseau(navire, x, y, h)) {
                    navire.placer(x, y, h);
                    controleur.getEtat().getJoueur1().getFlotte().add(navire);
                    place = true;
                }
            }
        }
        controleur.getFlotteRestante().clear();
        vue.actualiserMenuBateaux();
        vueOcean.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean());
        vue.getBtnPret().setDisable(false);
    }

    public String gererDragStartOcean(GrilleUI grille, int x, int y) {
        if (controleur.isPhaseBataille()) return null;
        Vaisseau navire = controleur.getEtat().getJoueur1().getGrilleOcean().getVaisseauAt(x, y);
        if (navire == null) return null;
        boolean h = estVaisseauHorizontal(x, y, navire);
        controleur.getEtat().getJoueur1().getGrilleOcean().retirerVaisseau(navire);
        controleur.getEtat().getJoueur1().getFlotte().remove(navire);
        controleur.getFlotteRestante().add(navire);
        vue.actualiserMenuBateaux();
        grille.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean());
        vue.getBtnPret().setDisable(true);
        return navire.getNom() + ";" + h;
    }

    public void gererDragOverOcean(GrilleUI grille, int x, int y, String nomNavire, boolean h) {
        if (controleur.isPhaseBataille()) return;
        Vaisseau navire = trouverVaisseauRestant(nomNavire);
        if (navire == null) return;
        grille.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean());
        boolean valide = controleur.getEtat().getJoueur1().getGrilleOcean().estPlacementValide(navire, x, y, h);
        Color c = valide ? Color.color(0, 1, 0, 0.6) : Color.color(1, 0, 0, 0.6);
        for (int i = 0; i < navire.getTaille(); i++) {
            int cx = h ? x + i : x; int cy = !h ? y + i : y;
            if (cx < 10 && cy < 10) grille.dessinerApercu(cx, cy, c);
        }
    }

    public void gererDragDroppedOcean(GrilleUI grille, int x, int y, String nomNavire, boolean h) {
        if (controleur.isPhaseBataille()) return;
        Vaisseau navire = trouverVaisseauRestant(nomNavire);
        if (navire == null) { grille.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean()); return; }
        if (controleur.getEtat().getJoueur1().getGrilleOcean().placerVaisseau(navire, x, y, h)) {
            navire.placer(x, y, h);
            controleur.getEtat().getJoueur1().getFlotte().add(navire);
            controleur.getFlotteRestante().remove(navire);
            vue.retirerVaisseauZoneSelection(nomNavire);
        }
        grille.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean());
        if (controleur.getFlotteRestante().isEmpty()) vue.getBtnPret().setDisable(false);
    }

    private Vaisseau trouverVaisseauRestant(String nom) {
        return controleur.getFlotteRestante().stream().filter(v -> v.getNom().equals(nom)).findFirst().orElse(null);
    }

    private boolean estVaisseauHorizontal(int x, int y, Vaisseau navire) {
        GrilleOcean ocean = controleur.getEtat().getJoueur1().getGrilleOcean();
        return (x + 1 < 10 && ocean.getVaisseauAt(x + 1, y) == navire) || (x - 1 >= 0 && ocean.getVaisseauAt(x - 1, y) == navire);
    }
}