package school.coda.darill_thomas_louis.bataillejavale.core.model;

import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;

public interface IGrille {
    int getTailleX();
    int getTailleY();

    // Pour la phase de placement
    boolean placerVaisseau(Vaisseau vaisseau, int x, int y, boolean estHorizontal);
    boolean estPlacementValide(Vaisseau vaisseau, int x, int y, boolean estHorizontal);

    // Pour la phase de bataille
    ResultatTir recevoirTir(int x, int y);
    boolean tousVaisseauxCoules();
}
