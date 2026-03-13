package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.scene.paint.Color;
import school.coda.darill_thomas_louis.bataillejavale.core.model.GrilleOcean;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

import java.util.ArrayList;
import java.util.Random;

public class GestionnairePlacement {

    private final PlateauDeJeu plateau;
    private final Random random = new Random();

    public GestionnairePlacement(PlateauDeJeu plateau) {
        this.plateau = plateau;
    }

    /**
     * Vider la grille du joueur
     * @param vueOcean
     * Grille du joueur
     */
    public void viderGrille(GrilleUI vueOcean) {
        plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean().vider();
        plateau.getEtatJeuBackend().getJoueur1().getFlotte().clear();
        plateau.rechargerFlotteRestante();
        plateau.actualiserMenuBateaux();
        vueOcean.rafraichir(plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean());
        plateau.getBtnPret().setDisable(true);
    }

    /**
     * Option permettant de posé les bâteaux aléatoirement
     * @param vueOcean
     * Grille du joueur
     */
    public void placerMesBateauxAleatoirement(GrilleUI vueOcean) {
        viderGrille(vueOcean);
        for (Vaisseau navire : new ArrayList<>(plateau.getFlotteRestante())) {
            boolean place = false;
            while (!place) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                boolean estHorizontal = random.nextBoolean();

                place = plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean().placerVaisseau(navire, x, y, estHorizontal);
                if (place) {
                    navire.placer(x, y, estHorizontal);
                    plateau.getEtatJeuBackend().getJoueur1().getFlotte().add(navire);
                }
            }
        }
        plateau.getFlotteRestante().clear();
        plateau.actualiserMenuBateaux();

        vueOcean.rafraichir(plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean());
        plateau.getBtnPret().setDisable(false);
    }

    public String gererDragStartOcean(GrilleUI grille, int x, int y) {
        if (plateau.isPhaseBataille()) return null;

        Vaisseau navire = plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean().getVaisseauAt(x, y);
        if (navire == null) return null;

        boolean estHoriz = estVaisseauHorizontal(x, y, navire);

        plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean().retirerVaisseau(navire);
        plateau.getEtatJeuBackend().getJoueur1().getFlotte().remove(navire);
        plateau.getFlotteRestante().add(navire);

        plateau.actualiserMenuBateaux();
        grille.rafraichir(plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean());
        plateau.getBtnPret().setDisable(true);

        return navire.getNom() + ";" + estHoriz;
    }

    public void gererDragOverOcean(GrilleUI grille, int x, int y, String nomNavire, boolean estHorizontal) {
        if (plateau.isPhaseBataille()) return;
        Vaisseau navire = trouverVaisseauRestant(nomNavire);
        if (navire == null) return;

        grille.rafraichir(plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean());

        boolean valide = plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean().estPlacementValide(navire, x, y, estHorizontal);
        Color couleurApercu = valide ? Color.color(0, 1, 0, 0.6) : Color.color(1, 0, 0, 0.6);

        dessinerApercuPlacement(grille, navire, x, y, estHorizontal, couleurApercu);
    }

    public void gererDragDroppedOcean(GrilleUI grille, int x, int y, String nomNavire, boolean estHorizontal) {
        if (plateau.isPhaseBataille()) return;
        Vaisseau navire = trouverVaisseauRestant(nomNavire);
        if (navire == null) {
            grille.rafraichir(plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean());
            return;
        }

        boolean success = plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean().placerVaisseau(navire, x, y, estHorizontal);
        if (success) {
            navire.placer(x, y, estHorizontal);

            plateau.getEtatJeuBackend().getJoueur1().getFlotte().add(navire);
            plateau.getFlotteRestante().remove(navire);
            plateau.retirerVaisseauZoneSelection(nomNavire);
        }

        grille.rafraichir(plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean());

        if (plateau.getFlotteRestante().isEmpty()) plateau.getBtnPret().setDisable(false);
    }

    // ==========================================
    // MÉTHODES INTERNES
    // ==========================================

    private Vaisseau trouverVaisseauRestant(String nom) {
        return plateau.getFlotteRestante().stream().filter(v -> v.getNom().equals(nom)).findFirst().orElse(null);
    }

    private boolean estVaisseauHorizontal(int x, int y, Vaisseau navire) {
        GrilleOcean ocean = plateau.getEtatJeuBackend().getJoueur1().getGrilleOcean();
        return (x + 1 < 10 && ocean.getVaisseauAt(x + 1, y) == navire) ||
                (x - 1 >= 0 && ocean.getVaisseauAt(x - 1, y) == navire);
    }

    private void dessinerApercuPlacement(GrilleUI grille, Vaisseau navire, int startX, int startY, boolean horizontal, Color couleur) {
        for (int i = 0; i < navire.getTaille(); i++) {
            int curX = horizontal ? startX + i : startX;
            int curY = !horizontal ? startY + i : startY;
            if (curX < 10 && curY < 10) grille.dessinerApercu(curX, curY, couleur);
        }
    }
}