package school.coda.darill_thomas_louis.bataillejavale.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.core.engine.MoteurJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.ModeJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.ui.PlateauDeJeu;

import java.util.List;

/**
 * Le Contrôleur (MVC). Il fait le lien entre l'Interface (La Vue) et la Logique (Le Moteur).
 * Il gère les Timers, les tours, et le réseau.
 */
public class PartieControleur {

    private final PlateauDeJeu vue;
    private final MoteurJeu moteur = new MoteurJeu();

    private final ModeJeu modeActuel;
    private EtatJeu etat;
    private int idPartie;
    private List<Vaisseau> flotteRestante;

    private boolean phaseBataille = false;
    private boolean tourJoueur = false;
    private TimerAction pollingTimer;

    public PartieControleur(PlateauDeJeu vue, ModeJeu mode, EtatJeu etat, int idPartie) {
        this.vue = vue;
        this.modeActuel = mode;
        this.etat = etat == null ? new EtatJeu() : etat;
        this.idPartie = idPartie;
        this.flotteRestante = moteur.genererFlotteStandard();
    }

    public void initialiserPartieExistante() {
        moteur.reparerLiensMemoireSauvegarde(etat);
        passerEnModeBataille(false);
        vue.restaurerVisuelBataille(etat);
    }

    public void passerEnModeBataille(boolean estNouvelle) {
        if (modeActuel == ModeJeu.SOLO) {
            if (estNouvelle) idPartie = moteur.initialiserPartieSolo(etat);
            demarrerBataille(true);
        } else if (modeActuel == ModeJeu.MULTI_HOTE) {
            idPartie = moteur.hebergerPartieMulti(etat);
            demarrerBataille(false);
            demarrerPollingAttenteJoueur2();
        } else if (modeActuel == ModeJeu.MULTI_INVITE) {
            moteur.rejoindrePartieMulti(idPartie, etat);
            demarrerBataille(false);
            demarrerPollingTourAdversaire();
        } else if (modeActuel == ModeJeu.REPLAY) {
            demarrerBataille(tourJoueur);
        }
    }

    private void demarrerBataille(boolean aMoi) {
        phaseBataille = true;
        tourJoueur = aMoi;
        vue.afficherModeBataille(aMoi, idPartie, modeActuel);
    }

    public void gererTirJoueur(int x, int y) {
        if (!phaseBataille || !tourJoueur) return;
        if (etat.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] != null) return;

        MoteurJeu.RapportTir rapport = moteur.executerTirJoueur(etat, idPartie, x, y, modeActuel == ModeJeu.MULTI_INVITE);
        vue.afficherImpactVisuel(x, y, rapport.resultat, rapport.cible, true);

        if (rapport.partieTerminee) {
            terminerPartie(rapport.victoire);
        } else {
            tourJoueur = false;
            vue.bloquerTour("TOUR ADVERSE...", "RÉFLEXION ENNEMIE...");
            if (modeActuel == ModeJeu.SOLO) FXGL.getGameTimer().runOnceAfter(this::riposteDuCPU, Duration.seconds(1.0));
            else demarrerPollingTourAdversaire();
        }
    }

    private void riposteDuCPU() {
        if (!phaseBataille) return;
        MoteurJeu.RapportTir rapport = moteur.executerTourCPU(etat, idPartie);
        vue.afficherImpactVisuel(rapport.x, rapport.y, rapport.resultat, rapport.cible, false);

        if (rapport.partieTerminee) terminerPartie(rapport.victoire);
        else { tourJoueur = true; vue.activerMonTour(etat.getMancheActuelle()); }
    }

    private void demarrerPollingAttenteJoueur2() {
        pollingTimer = FXGL.getGameTimer().runAtInterval(() -> {
            if (moteur.verifierAdversaireRejoint(idPartie)) {
                pollingTimer.expire();
                EtatJeu dbEtat = moteur.ecouterReseau(idPartie, -1, false);
                if (dbEtat != null) etat = dbEtat;
                tourJoueur = true;
                vue.notifierAdversaireRejoint();
                vue.activerMonTour(etat.getMancheActuelle());
            }
        }, Duration.seconds(2.0));
    }

    private void demarrerPollingTourAdversaire() {
        pollingTimer = FXGL.getGameTimer().runAtInterval(() -> {
            EtatJeu dbEtat = moteur.ecouterReseau(idPartie, etat.getTourCourant(), modeActuel == ModeJeu.MULTI_INVITE);
            if (dbEtat != null) {
                pollingTimer.expire();
                etat = dbEtat;
                vue.restaurerVisuelBataille(etat);

                if (etat.getJoueur1().aPerdu()) {
                    moteur.enregistrerDefaiteDistante();
                    terminerPartie(false);
                } else {
                    tourJoueur = true;
                    vue.activerMonTour(etat.getMancheActuelle());
                }
            }
        }, Duration.seconds(2.0));
    }

    private void terminerPartie(boolean victoire) {
        phaseBataille = false;
        if (pollingTimer != null) pollingTimer.expire();
        vue.afficherEcranFin(victoire);
    }

    public void stopperControleur() {
        if (pollingTimer != null) pollingTimer.expire();
    }

    // --- GETTERS POUR LA VUE ET LE PLACEMENT ---
    public EtatJeu getEtat() { return etat; }
    public List<Vaisseau> getFlotteRestante() { return flotteRestante; }
    public void setFlotteRestante(List<Vaisseau> flotteRestante) { this.flotteRestante = flotteRestante; }
    public boolean isPhaseBataille() { return phaseBataille; }
}