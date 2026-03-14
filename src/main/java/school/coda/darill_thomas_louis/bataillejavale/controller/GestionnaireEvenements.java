package school.coda.darill_thomas_louis.bataillejavale.controller;

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
        gererEffetsEnCours();

        if (!config.isEvenementsActifs()) return;

        //vérification de l'Apocalypse (Manche 30+) vu qu'il annule tous les autres évènements
        if (etat.getMancheActuelle() >= 30) {
            declencherMeteores("ALERTE : APOCALYPSE ! LA PLUIE DE MÉTÉORES EST INCESSANTE !");
            return;
        }

        int tirage = random.nextInt(100);

        if (tirage < 5) {
            //5% de chanc météores
            declencherMeteores("ALERTE : PLUIE DE MÉTÉORES DÉTECTÉE SUR LE CHAMP DE BATAILLE !");
        } else if (tirage < 25) {
            //20% de chance pour brouillage Radar
            declencherBrouillage();
        }
    }

    private void declencherBrouillage() {
        toursRestantsBrouillage = 2;
        //alerte visuelle
        vue.notificationBox.afficherAlerteTaille("ÉVÉNEMENT : TEMPÊTE MAGNÉTIQUE ! RADAR BROUILLÉ (2 TOURS)", "#ffaa00", 15);

        // TODO : Coder l'effet visuel sur la grille radar plus tard
        System.out.println(">>> EVENT : Brouillage activé !");
    }

    private void declencherMeteores(String messageAlerte) {
        vue.notificationBox.afficherAlerteTaille(messageAlerte, "#ff0000", 16);

        // TODO : Faire tomber 2 météores au hasard sur les grilles des deux joueurs
        System.out.println(">>> EVENT : Météores en approche !");
    }

    private void gererEffetsEnCours() {
        if (toursRestantsBrouillage > 0) {
            toursRestantsBrouillage--;
            if (toursRestantsBrouillage == 0) {
                vue.notificationBox.afficherAlerte("FIN DU BROUILLAGE. RADAR OPÉRATIONNEL.", "#00ffff");
                System.out.println(">>> EVENT : Fin du brouillage");
            }
        }
    }
}