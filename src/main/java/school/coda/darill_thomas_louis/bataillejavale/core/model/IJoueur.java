package school.coda.darill_thomas_louis.bataillejavale.core.model;

import java.util.List;

public interface IJoueur {
    String getPseudo();

    IGrille getGrilleOcean();
    IGrille getGrilleRadar();

    List<Vaisseau> getFlotte();

    boolean aPerdu();
}
