package school.coda.darill_thomas_louis.bataillejavale.core.engine;

import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.JoueurPlay;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Session;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.JoueurRepository;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.PartieRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Le cœur logique du jeu. Indépendant de l'interface graphique.
 */
public class MoteurJeu {

    private final Random random = new Random();
    private final PartieRepository partieRepo = new PartieRepository();
    private final JoueurRepository joueurRepo = new JoueurRepository();

    // --- INITIALISATION ---

    public List<Vaisseau> genererFlotteStandard() {
        return new ArrayList<>(Arrays.asList(
                new Vaisseau("Porte-avions", 5), new Vaisseau("Cuirassé", 4),
                new Vaisseau("Destroyer", 3), new Vaisseau("Sous-marin", 3), new Vaisseau("Patrouilleur", 2)
        ));
    }

    public int initialiserPartieSolo(EtatJeu etat) {
        placerBateauxCPU(etat.getJoueur2());
        return partieRepo.creerNouvellePartie(etat);
    }

    public int hebergerPartieMulti(EtatJeu etat) {
        return partieRepo.hebergerPartieMulti(etat);
    }

    public void rejoindrePartieMulti(int idPartie, EtatJeu etat) {
        partieRepo.demarrerPartieMulti(idPartie, preparerEtatPourDB(etat, true));
    }

    // --- STRUCTURE DE RETOUR POUR L'UI ---

    public static class RapportTir {
        public int x, y;
        public ResultatTir resultat;
        public Vaisseau cible;
        public boolean partieTerminee;
        public boolean victoire;
    }

    // --- ACTIONS DE COMBAT ---

    public RapportTir executerTirJoueur(EtatJeu etat, int idPartie, int x, int y, boolean estInvite) {
        JoueurPlay attaquant = etat.getJoueur1();
        JoueurPlay defenseur = etat.getJoueur2();

        RapportTir rapport = new RapportTir();
        rapport.x = x; rapport.y = y;
        rapport.cible = defenseur.getGrilleOcean().getVaisseauAt(x, y);
        rapport.resultat = defenseur.getGrilleOcean().recevoirTir(x, y);
        attaquant.getGrilleRadar().enregistrerTir(x, y, rapport.resultat);

        etat.setTourCourant(etat.getTourCourant() + 1);
        partieRepo.mettreAJourPartie(idPartie, preparerEtatPourDB(etat, estInvite));

        verifierEtCloturerPartie(etat, idPartie, rapport);
        return rapport;
    }

    public RapportTir executerTourCPU(EtatJeu etat, int idPartie) {
        JoueurPlay cpu = etat.getJoueur2();
        JoueurPlay joueur = etat.getJoueur1();

        int[] coords = calculerCibleCPU(cpu);
        int x = coords[0];
        int y = coords[1];

        RapportTir rapport = new RapportTir();
        rapport.x = x; rapport.y = y;
        rapport.cible = joueur.getGrilleOcean().getVaisseauAt(x, y);
        rapport.resultat = joueur.getGrilleOcean().recevoirTir(x, y);
        cpu.getGrilleRadar().enregistrerTir(x, y, rapport.resultat);

        etat.setTourCourant(etat.getTourCourant() + 1);
        partieRepo.mettreAJourPartie(idPartie, etat);

        verifierEtCloturerPartie(etat, idPartie, rapport);
        return rapport;
    }

    private void verifierEtCloturerPartie(EtatJeu etat, int idPartie, RapportTir rapport) {
        if (etat.getJoueur2().aPerdu()) {
            rapport.partieTerminee = true; rapport.victoire = true;
            partieRepo.terminerPartie(idPartie, "TERMINEE_VICTOIRE", Session.idJoueur);
            joueurRepo.updateStats(true);
        } else if (etat.getJoueur1().aPerdu()) {
            rapport.partieTerminee = true; rapport.victoire = false;
            partieRepo.terminerPartie(idPartie, "TERMINEE_DEFAITE", -1);
            joueurRepo.updateStats(false);
        } else {
            rapport.partieTerminee = false;
        }
    }

    public void enregistrerDefaiteDistante() {
        joueurRepo.updateStats(false);
    }

    // --- SYNCHRONISATION RÉSEAU & RÉPARATION JSON ---

    public boolean verifierAdversaireRejoint(int idPartie) {
        return "EN_COURS".equals(partieRepo.getStatutPartie(idPartie));
    }

    public EtatJeu ecouterReseau(int idPartie, int tourLocal, boolean estInvite) {
        EtatJeu dbEtat = partieRepo.chargerPartieActiveOuTerminee(idPartie);
        if (dbEtat != null && dbEtat.getTourCourant() > tourLocal) {
            EtatJeu nouvelEtat = estInvite ? preparerEtatPourDB(dbEtat, true) : dbEtat;
            reparerLiensMemoireSauvegarde(nouvelEtat);
            return nouvelEtat;
        }
        return null;
    }

    public void reparerLiensMemoireSauvegarde(EtatJeu etat) {
        reparerJoueur(etat.getJoueur1(), etat.getJoueur2());
        reparerJoueur(etat.getJoueur2(), etat.getJoueur1());
    }

    // --- OUTILS INTERNES (IA et Mémoire) ---

    private void placerBateauxCPU(JoueurPlay cpu) {
        for (Vaisseau navire : genererFlotteStandard()) {
            boolean place = false;
            while (!place) {
                place = cpu.getGrilleOcean().placerVaisseau(navire, random.nextInt(10), random.nextInt(10), random.nextBoolean());
                if (place) cpu.getFlotte().add(navire);
            }
        }
    }

    private int[] calculerCibleCPU(JoueurPlay cpu) {
        int cx;
        int cy;
        do {
            cx = random.nextInt(10); cy = random.nextInt(10);
        } while (cpu.getGrilleRadar().getHistoriqueTirs()[cx][cy] != null);
        return new int[]{cx, cy};
    }

    private EtatJeu preparerEtatPourDB(EtatJeu etatLocal, boolean estInvite) {
        if (estInvite) {
            EtatJeu dbEtat = new EtatJeu();
            dbEtat.setJoueur1(etatLocal.getJoueur2());
            dbEtat.setJoueur2(etatLocal.getJoueur1());
            dbEtat.setTourCourant(etatLocal.getTourCourant());
            return dbEtat;
        }
        return etatLocal;
    }

    private void reparerJoueur(JoueurPlay defenseur, JoueurPlay attaquant) {
        Vaisseau[][] plateau = defenseur.getGrilleOcean().getPlateau();
        List<Vaisseau> flotte = defenseur.getFlotte();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (plateau[x][y] != null) {
                    for (Vaisseau vraiVaisseau : flotte) {
                        if (vraiVaisseau.getNom().equals(plateau[x][y].getNom())) {
                            plateau[x][y] = vraiVaisseau; break;
                        }
                    }
                }
            }
        }
        for (Vaisseau v : flotte) v.setCasesTouchees(0);
        ResultatTir[][] historique = attaquant.getGrilleRadar().getHistoriqueTirs();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if ((historique[x][y] == ResultatTir.TOUCHE || historique[x][y] == ResultatTir.COULE) && plateau[x][y] != null) {
                    plateau[x][y].recevoirDegat(x, y);
                }
            }
        }
    }
}