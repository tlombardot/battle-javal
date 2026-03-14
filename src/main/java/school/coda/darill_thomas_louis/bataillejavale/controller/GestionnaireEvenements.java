package school.coda.darill_thomas_louis.bataillejavale.controller;

import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.ConfigPartie;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.ui.PlateauDeJeu;

import java.util.Random;

public class GestionnaireEvenements {

    private final PartieControleur controleur;
    private final PlateauDeJeu vue;
    private final ConfigPartie config;
    private final Random random;

    private int toursRestantsBrouillage = 0;

    public GestionnaireEvenements(PartieControleur controleur, PlateauDeJeu vue, ConfigPartie config) {
        this.controleur = controleur;
        this.vue = vue;
        this.config = config;
        this.random = new Random();
    }

    /**
     * Méthode appelée à la fin de chaque manche complète (quand c'est de nouveau au joueur 1 de jouer)
     */
    public void evaluerFinDeManche(EtatJeu etat) {

        if (!config.isEvenementsActifs()) return;

        if (toursRestantsBrouillage > 0) {
            toursRestantsBrouillage--;
            if (toursRestantsBrouillage == 0) {
                vue.activerBrouillageVisuel(false);
                vue.notificationBox.afficherAlerteTaille("VISIBILITÉ RADAR RÉTABLIE", "#00ffff", 16);
                vue.sideBar.ajouterLog("» Fin de tempête. Le radar est de nouveau opérationnel.", "INFO");
            }
        }

        if (etat.getMancheActuelle() >= 30) {
            declencherMeteores(etat, "APOCALYPSE ! LA PLUIE DE MÉTÉORES EST INCESSANTE !");
            return;
        }

        int tirage = random.nextInt(100);

        if (tirage < 5) {
            // 5% de chances
            declencherMeteores(etat, "PLUIE DE MÉTÉORES DÉTECTÉE SUR LE CHAMP DE BATAILLE !");

        } else if (tirage < 25 && toursRestantsBrouillage == 0) {
            declencherBrouillage();
        }
    }

    private void declencherBrouillage() {
        toursRestantsBrouillage = 2;
        //alerte visuelle
        vue.activerBrouillageVisuel(true);
        vue.notificationBox.afficherAlerteTaille("TEMPÊTE MAGNÉTIQUE ! RADAR BROUILLÉ (2 TOURS)", "#ffaa00", 16);
        vue.sideBar.ajouterLog("» TEMPÊTE MAGNÉTIQUE ! Visibilité radar réduite pour 2 tours.", "ALERTE");

        // TODO : Coder l'effet visuel sur la grille radar plus tard
        System.out.println(">>> EVENT : Brouillage activé !");
    }

    private void declencherMeteores(EtatJeu etat, String messageAlerte) {
        vue.notificationBox.afficherAlerteTaille(messageAlerte, "#ffaa00", 17);
        System.out.println(">>> EVENT : Météores en approche !");

        for (int i = 0; i < 2; i++) {

            int x1, y1;
            do {
                x1 = random.nextInt(10);
                y1 = random.nextInt(10);
            } while (etat.getJoueur2().getGrilleRadar().getHistoriqueTirs()[x1][y1] != null);

            ResultatTir res1 = etat.getJoueur1().getGrilleOcean().recevoirTir(x1, y1);
            etat.getJoueur2().getGrilleRadar().enregistrerTir(x1, y1, res1);

            vue.afficherImpactMeteore(x1, y1, res1, etat.getJoueur1().getGrilleOcean().getVaisseauAt(x1, y1), false);


            int x2, y2;
            do {
                x2 = random.nextInt(10);
                y2 = random.nextInt(10);
            } while (etat.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x2][y2] != null);

            ResultatTir res2 = etat.getJoueur2().getGrilleOcean().recevoirTir(x2, y2);
            etat.getJoueur1().getGrilleRadar().enregistrerTir(x2, y2, res2);

            vue.afficherImpactMeteore(x2, y2, res2, etat.getJoueur2().getGrilleOcean().getVaisseauAt(x2, y2), true);
        }

        if (etat.getJoueur1().aPerdu()) {
            controleur.terminerPartie(false);
        } else if (etat.getJoueur2().aPerdu()) {
            controleur.terminerPartie(true);
        }
    }

}