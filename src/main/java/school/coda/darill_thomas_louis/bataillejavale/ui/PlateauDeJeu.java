package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlateauDeJeu {

    private EtatJeu etatJeuBackend;
    private List<Vaisseau> flotteRestante;
    private boolean estHorizontal = true;
    private int hoverX = -1;
    private int hoverY = -1;
    private boolean phaseBataille = false;
    private final Random random = new Random();

    // --- CONTENEURS D'INTERFACE ---
    private final HBox racineVisuelle;
    private final VBox conteneurOcean;
    private final VBox conteneurRadar;
    private final SideBarUI sideBar;

    public PlateauDeJeu() {
        initialiserDonneesPartie();

        GrilleUI vueOcean = creerGrilleOcean();
        GrilleUI vueRadar = creerGrilleRadar(vueOcean);
        sideBar = new SideBarUI();

        conteneurOcean = new VBox(10, new Text("Grille Océan (Mes bateaux)"), vueOcean);
        conteneurOcean.setAlignment(Pos.CENTER);

        conteneurRadar = new VBox(10, new Text("Grille Radar (Mes tirs)"), vueRadar);
        conteneurRadar.setAlignment(Pos.CENTER);

        racineVisuelle = new HBox(50);
        racineVisuelle.setAlignment(Pos.CENTER);
        racineVisuelle.getChildren().add(conteneurOcean);
    }

    public HBox getRacineVisuelle() {
        return racineVisuelle;
    }

    private void initialiserDonneesPartie() {
        etatJeuBackend = new EtatJeu();
        phaseBataille = false;

        flotteRestante = new ArrayList<>(Arrays.asList(
                new Vaisseau("Porte-avions", 5),
                new Vaisseau("Cuirassé", 4),
                new Vaisseau("Destroyer", 3),
                new Vaisseau("Sous-marin", 3),
                new Vaisseau("Patrouilleur", 2)
        ));
    }

    private GrilleUI creerGrilleOcean() {
        GrilleUI vueOcean = new GrilleUI();
        vueOcean.setListener(new GrilleUI.GrilleListener() {
            @Override
            public void onCaseLeftClick(int x, int y) {
                gererClicPlacementOcean(vueOcean, x, y);
            }

            @Override
            public void onCaseRightClick(int x, int y) {
                if (!phaseBataille) {
                    estHorizontal = !estHorizontal;
                    afficherPrevisualisation(vueOcean);
                }
            }

            @Override
            public void onCaseHoverEnter(int x, int y) {
                if (!phaseBataille) { hoverX = x; hoverY = y; afficherPrevisualisation(vueOcean); }
            }

            @Override
            public void onCaseHoverExit(int x, int y) {
                if (!phaseBataille) { hoverX = -1; hoverY = -1; vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean()); }
            }
        });
        return vueOcean;
    }

    private void gererClicPlacementOcean(GrilleUI vueOcean, int x, int y) {
        if (flotteRestante.isEmpty()) return;

        Vaisseau navireEnCours = flotteRestante.getFirst();
        boolean success = etatJeuBackend.getJoueur1().getGrilleOcean().placerVaisseau(navireEnCours, x, y, estHorizontal);

        if (success) {
            etatJeuBackend.getJoueur1().getFlotte().add(navireEnCours);
            flotteRestante.removeFirst();
            vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());

            if (flotteRestante.isEmpty()) {
                passerEnModeBataille();
            } else {
                afficherPrevisualisation(vueOcean);
            }
        }
    }

    /**
     * Cette méthode déclenche l'apparition du Radar et des Logs
     */
    private void passerEnModeBataille() {
        IO.println("FLOTTE PLACÉE ! DÉBUT DE LA BATAILLE !");
        placerBateauxCPU();
        phaseBataille = true;

        racineVisuelle.getChildren().setAll(conteneurOcean, conteneurRadar, sideBar);

        sideBar.ajouterLog("Flotte en position.", Color.CYAN);
        sideBar.ajouterLog("La bataille commence !", Color.LIMEGREEN);
    }

    private GrilleUI creerGrilleRadar(GrilleUI vueOcean) {
        GrilleUI vueRadar = new GrilleUI();
        vueRadar.setListener(new GrilleUI.GrilleListener() {
            @Override
            public void onCaseLeftClick(int x, int y) {
                if (!phaseBataille) return;

                if (etatJeuBackend.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] != null) {
                    IO.println("Status du tir : " + ResultatTir.DEJA_TIRE + " ! Choisissez une autre case.");
                    return;
                }

                Vaisseau cibleAdverse = etatJeuBackend.getJoueur2().getGrilleOcean().getVaisseauAt(x, y);
                ResultatTir resultat = etatJeuBackend.getJoueur2().getGrilleOcean().recevoirTir(x, y);
                etatJeuBackend.getJoueur1().getGrilleRadar().enregistrerTir(x, y, resultat);

                if (resultat == ResultatTir.RATE) {
                    vueRadar.colorierCase(x, y, Color.WHITE);
                    sideBar.ajouterLog("Vous tirez en " + (char)('A' + y) + "-" + (x + 1) + " : À L'EAU !", Color.GRAY);
                } else {
                    vueRadar.colorierCase(x, y, Color.RED);
                    if (resultat == ResultatTir.TOUCHE){
                        sideBar.ajouterLog("Vous touchez un navire ennemi !", Color.ORANGE);
                    } else {
                        sideBar.ajouterLog("BOUM ! Le " + cibleAdverse.getNom() + " ennemi a coulé !", Color.LIMEGREEN);
                    }
                }

                verifierFinDePartie();
                if (!phaseBataille) return;

                riposteDuCPU(vueOcean);

                etatJeuBackend.setTourCourant(etatJeuBackend.getTourCourant() + 1);
                sideBar.setTour(etatJeuBackend.getTourCourant());

                verifierFinDePartie();
            }

            @Override public void onCaseRightClick(int x, int y) {}
            @Override public void onCaseHoverEnter(int x, int y) {}
            @Override public void onCaseHoverExit(int x, int y) {}
        });
        return vueRadar;
    }

    private void placerBateauxCPU() {
        List<Vaisseau> flotteCPU = Arrays.asList(
                new Vaisseau("Porte-avions", 5), new Vaisseau("Cuirassé", 4),
                new Vaisseau("Destroyer", 3), new Vaisseau("Sous-marin", 3), new Vaisseau("Patrouilleur", 2)
        );

        for (Vaisseau navire : flotteCPU) {
            boolean place = false;
            while (!place) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                boolean horiz = random.nextBoolean();
                place = etatJeuBackend.getJoueur2().getGrilleOcean().placerVaisseau(navire, x, y, horiz);
                if (place) {
                    etatJeuBackend.getJoueur2().getFlotte().add(navire);
                }
            }
        }
    }

    private void riposteDuCPU(GrilleUI vueOcean) {
        int cibleX;
        int cibleY;

        do {
            cibleX = random.nextInt(10);
            cibleY = random.nextInt(10);
        } while (etatJeuBackend.getJoueur2().getGrilleRadar().getHistoriqueTirs()[cibleX][cibleY] != null);

        Vaisseau notreCible = etatJeuBackend.getJoueur1().getGrilleOcean().getVaisseauAt(cibleX, cibleY);
        ResultatTir resultatCPU = etatJeuBackend.getJoueur1().getGrilleOcean().recevoirTir(cibleX, cibleY);
        etatJeuBackend.getJoueur2().getGrilleRadar().enregistrerTir(cibleX, cibleY, resultatCPU);

        if (resultatCPU == ResultatTir.RATE) {
            vueOcean.colorierCase(cibleX, cibleY, Color.LIGHTCYAN);
            sideBar.ajouterLog("CPU tire en " + (char)('A' + cibleY) + "-" + (cibleX + 1) + " : Raté !", Color.GRAY);
        } else {
            vueOcean.colorierCase(cibleX, cibleY, Color.DARKRED);
            if (resultatCPU == ResultatTir.TOUCHE) {
                sideBar.ajouterLog("ALERTE ! Votre navire a été touché !", Color.RED);
            } else {
                sideBar.ajouterLog("DÉSASTRE ! Votre " + notreCible.getNom() + " a coulé !", Color.DARKRED);
            }
        }
    }

    private void afficherPrevisualisation(GrilleUI vueOcean) {
        if (flotteRestante.isEmpty() || hoverX == -1 || hoverY == -1) return;
        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        Vaisseau navire = flotteRestante.getFirst();
        boolean valide = etatJeuBackend.getJoueur1().getGrilleOcean().estPlacementValide(navire, hoverX, hoverY, estHorizontal);
        Color couleurApercu = valide ? Color.LIGHTGREEN : Color.RED;

        for (int i = 0; i < navire.getTaille(); i++) {
            int currentX = estHorizontal ? hoverX + i : hoverX;
            int currentY = !estHorizontal ? hoverY + i : hoverY;
            if (currentX < 10 && currentY < 10) {
                vueOcean.colorierCase(currentX, currentY, couleurApercu);
            }
        }
    }

    private void verifierFinDePartie(){
        if (etatJeuBackend.getJoueur2().aPerdu()){
            phaseBataille = false;
            afficherEcranFin("VICTOIRE ! \nVous avez détruit la flotte ennemie !", Color.LIMEGREEN);
        } else if (etatJeuBackend.getJoueur1().aPerdu()){
            phaseBataille = false;
            afficherEcranFin("DÉFAITE...\nLe CPU a coulé votre flotte.", Color.RED);
        }
    }

    private void afficherEcranFin(String message, Color couleur) {
        javafx.scene.shape.Rectangle voileObscur = new javafx.scene.shape.Rectangle(
                com.almasb.fxgl.dsl.FXGL.getAppWidth(),
                com.almasb.fxgl.dsl.FXGL.getAppHeight(),
                Color.color(0, 0, 0, 0.8)
        );

        Text texteFin = new Text(message);
        texteFin.setFont(javafx.scene.text.Font.font("Consolas", 40));
        texteFin.setFill(couleur);
        texteFin.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        javafx.scene.control.Button btnQuitter = new javafx.scene.control.Button("Quitter le jeu");
        btnQuitter.setPrefSize(200, 50);
        btnQuitter.setOnAction(_ -> com.almasb.fxgl.dsl.FXGL.getGameController().exit());

        VBox ecranFin = new VBox(40, texteFin, btnQuitter);
        ecranFin.setAlignment(Pos.CENTER);

        com.almasb.fxgl.dsl.FXGL.addUINode(voileObscur);
        com.almasb.fxgl.dsl.FXGL.addUINode(ecranFin);
    }
}