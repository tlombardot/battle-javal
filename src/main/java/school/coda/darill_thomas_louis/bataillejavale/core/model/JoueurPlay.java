package school.coda.darill_thomas_louis.bataillejavale.core.model;

import java.util.ArrayList;
import java.util.List;

public class JoueurPlay implements Joueur {
    private String pseudo;
    private GrilleOcean grilleOcean;
    private GrilleRadar grilleRadar;
    private List<Vaisseau> flotte;

    public JoueurPlay() {
    }

    public JoueurPlay(String pseudo) {
        this.pseudo = pseudo;
        this.grilleOcean = new GrilleOcean();
        this.grilleRadar = new GrilleRadar();
        this.flotte = new ArrayList<>();
    }

    @Override
    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public GrilleOcean getGrilleOcean() { return grilleOcean; }
    public void setGrilleOcean(GrilleOcean grilleOcean) { this.grilleOcean = grilleOcean; }

    public GrilleRadar getGrilleRadar() { return grilleRadar; }
    public void setGrilleRadar(GrilleRadar grilleRadar) { this.grilleRadar = grilleRadar; }

    @Override
    public List<Vaisseau> getFlotte() { return flotte; }
    public void setFlotte(List<Vaisseau> flotte) { this.flotte = flotte; }

    @Override
    public boolean aPerdu() {
        if (flotte.isEmpty()) return false;
        return flotte.stream().allMatch(Vaisseau::estCoule);
    }
}