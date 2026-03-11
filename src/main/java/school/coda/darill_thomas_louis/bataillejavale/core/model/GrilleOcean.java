package school.coda.darill_thomas_louis.bataillejavale.core.model;

import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;

public class GrilleOcean implements Grille {
    private final int tailleX;
    private final int tailleY;

    private final Vaisseau[][] plateau;

    public GrilleOcean() {
        this.tailleX = 10;
        this.tailleY = 10;
        this.plateau = new Vaisseau[tailleX][tailleY];
    }

    @Override
    public int getTailleX() { return tailleX; }
    @Override
    public int getTailleY() { return tailleY; }

    public boolean estPlacementValide(Vaisseau vaisseau, int startX, int startY, boolean horizontal) {
        if (horizontal && startX + vaisseau.getTaille() > tailleX) return false;
        if (!horizontal && startY + vaisseau.getTaille() > tailleY) return false;

        for (int i = 0; i < vaisseau.getTaille(); i++) {
            int x = horizontal ? startX + i : startX;
            int y = !horizontal ? startY + i : startY;
            if (plateau[x][y] != null) {
                return false;
            }
        }
        return true;
    }

    public boolean placerVaisseau(Vaisseau vaisseau, int startX, int startY, boolean horizontal) {
        if (!estPlacementValide(vaisseau, startX, startY, horizontal)) {
            return false;
        }

        for (int i = 0; i < vaisseau.getTaille(); i++) {
            int x = horizontal ? startX + i : startX;
            int y = !horizontal ? startY + i : startY;
            plateau[x][y] = vaisseau;
        }
        return true;
    }

    public Vaisseau[][] getPlateau() { return plateau; }

    public ResultatTir recevoirTir(int x, int y) {
        Vaisseau cible = plateau[x][y];

        if (cible == null) {
            return ResultatTir.RATE; // À l'eau ! (Torpille blanche)
        } else {
            boolean estCoule = cible.recevoirDegat(x, y);
            return estCoule ? ResultatTir.COULE : ResultatTir.TOUCHE; // Torpille rouge !
        }
    }

    // Permet de savoir quel bateau a été touché pour l'afficher dans les logs
    public Vaisseau getVaisseauAt(int x, int y) {
        return plateau[x][y];
    }

    // Permet de vider la grille pour le placement aléatoire
    public void vider() {
        for (int x = 0; x < tailleX; x++) {
            for (int y = 0; y < tailleY; y++) {
                plateau[x][y] = null;
            }
        }
    }

    // Permet d'effacer un bateau précis (pour pouvoir le déplacer)
    public void retirerVaisseau(Vaisseau vaisseau) {
        for (int x = 0; x < tailleX; x++) {
            for (int y = 0; y < tailleY; y++) {
                if (plateau[x][y] == vaisseau) {
                    plateau[x][y] = null;
                }
            }
        }
    }
}