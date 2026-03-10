package school.coda.darill_thomas_louis.bataillejavale.core.model;

import java.util.List;

public interface Joueur {

    String getPseudo();

    List<Vaisseau> getFlotte();

    boolean aPerdu();

}
