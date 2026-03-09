package school.coda.darill_thomas_louis.bataillejavale.core.command;

import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.IGrille;

import java.util.List;

public interface IAttaque {
    List<ResultatTir> executer(IGrille grilleAdverse, int xCible, int yCible);
}
