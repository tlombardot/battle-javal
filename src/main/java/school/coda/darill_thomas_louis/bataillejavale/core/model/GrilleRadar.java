package school.coda.darill_thomas_louis.bataillejavale.core.model;

import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;

public class GrilleRadar implements Grille {
    private final int tailleX;
    private final int tailleY;

    private ResultatTir[][] historiqueTirs;

    public GrilleRadar() {
        this.tailleX = 10;
        this.tailleY = 10;
        this.historiqueTirs = new ResultatTir[tailleX][tailleY];
    }

    @Override
    public int getTailleX() { return tailleX; }
    @Override
    public int getTailleY() { return tailleY; }

    public void enregistrerTir(int x, int y, ResultatTir resultat) {
        historiqueTirs[x][y] = resultat;
    }

    // Getter & Setter pour JSON
    public ResultatTir[][] getHistoriqueTirs() { return historiqueTirs; }
    public void setHistoriqueTirs(ResultatTir[][] historiqueTirs) {
        this.historiqueTirs = historiqueTirs;
    }
}
