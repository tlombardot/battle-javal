package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlateauDeJeu {

    private static final String TEXT_FONT = "Times New Roman";

    private EtatJeu etatJeuBackend;
    private List<Vaisseau> flotteRestante;
    private boolean phaseBataille = false;
    private final Random random = new Random();

    private final StackPane racineVisuelle;
    private final BorderPane layoutPrincipal;

    private final GrilleUI vueOcean;
    private final GrilleUI vueRadar;
    private final SideBarUI sideBar;
    private final NotificationUI notificationBox;

    private SelectBoard zoneSelectionBateaux;
    private final VBox conteneurOcean;
    private final VBox conteneurRadar;
    private VBox panneauPlacement;
    private Button btnPret;

    public PlateauDeJeu() {
        initialiserDonneesPartie();

        vueOcean = creerGrilleOcean();
        vueRadar = creerGrilleRadar(vueOcean);
        sideBar = new SideBarUI();
        notificationBox = new NotificationUI();

        DropShadow neonGlowOcean = new DropShadow(25, Color.web("#00ffff"));
        DropShadow neonGlowRadar = new DropShadow(25, Color.web("#ff0000"));

        Text txtOcean = new Text("ZONE ALLIÉE (Océan)");
        txtOcean.setFont(Font.font(TEXT_FONT, 20));
        txtOcean.setFill(Color.CYAN);
        conteneurOcean = new VBox(15, txtOcean, vueOcean);
        conteneurOcean.setAlignment(Pos.CENTER);
        conteneurOcean.setEffect(neonGlowOcean);

        Text txtRadar = new Text("ZONE ENNEMIE (Radar)");
        txtRadar.setFont(Font.font(TEXT_FONT, 20));
        txtRadar.setFill(Color.RED);
        conteneurRadar = new VBox(15, txtRadar, vueRadar);
        conteneurRadar.setAlignment(Pos.CENTER);
        conteneurRadar.setEffect(neonGlowRadar);

        ColorAdjust desaturate = new ColorAdjust();
        desaturate.setBrightness(-0.5);
        conteneurRadar.setEffect(desaturate);

        creerPanneauPlacement();

        Rectangle fondPlateau = new Rectangle();
        Stop[] stops = new Stop[] { new Stop(0, Color.web("#050814")), new Stop(1, Color.web("#0a1526")) };
        LinearGradient bgGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        fondPlateau.setFill(bgGradient);
        fondPlateau.setWidth(FXGL.getAppWidth());
        fondPlateau.setHeight(FXGL.getAppHeight());

        layoutPrincipal = new BorderPane();
        layoutPrincipal.setPrefWidth(FXGL.getAppWidth());
        layoutPrincipal.setPrefHeight(FXGL.getAppHeight());

        BorderPane.setMargin(panneauPlacement, new Insets(0, 0, 0, 50));
        layoutPrincipal.setLeft(panneauPlacement);
        layoutPrincipal.setCenter(conteneurOcean);

        racineVisuelle = new StackPane();
        racineVisuelle.getChildren().addAll(fondPlateau, layoutPrincipal, notificationBox);

        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());

        sideBar.ajouterLog("Système de contrôle initialisé. En attente de déploiement.", "INFO");
        notificationBox.afficherAlerte("DÉPLOIE TA FLOTTE, AMIRAL !", "#00ffff");
    }

    public StackPane getRacineVisuelle() {
        return racineVisuelle;
    }

    private void initialiserDonneesPartie() {
        etatJeuBackend = new EtatJeu();
        phaseBataille = false;
        rechargerFlotteRestante();
    }

    private void rechargerFlotteRestante() {
        flotteRestante = new ArrayList<>(Arrays.asList(
                new Vaisseau("Porte-avions", 5),
                new Vaisseau("Cuirassé", 4),
                new Vaisseau("Destroyer", 3),
                new Vaisseau("Sous-marin", 3),
                new Vaisseau("Patrouilleur", 2)
        ));
    }

    private void creerPanneauPlacement() {
        panneauPlacement = new VBox(25);
        panneauPlacement.setAlignment(Pos.CENTER);
        panneauPlacement.setStyle("-fx-background-color: #1a2230; -fx-padding: 30; -fx-border-color: #00ffff; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        panneauPlacement.setPrefWidth(320);

        Text titre = new Text("DÉPLOIEMENT");
        titre.setFont(Font.font(TEXT_FONT, 28));
        titre.setFill(Color.WHITE);

        Text instructions = new Text("Clic Droit (sur bateau) : Tourner\nGlisser-Déposer : Placer");
        instructions.setFont(Font.font(TEXT_FONT, 14));
        instructions.setFill(Color.GRAY);
        instructions.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        zoneSelectionBateaux = new SelectBoard(flotteRestante);

        Button btnAleatoire = styleBouton("PLACEMENT ALÉATOIRE", "#00ffff");
        btnAleatoire.setOnAction(_ -> placerMesBateauxAleatoirement());

        Button btnVider = styleBouton("VIDER LA GRILLE", "#ffaa00");
        btnVider.setOnAction(_ -> viderGrille());

        btnPret = styleBouton("DÉMARRER BATAILLE", "#32cd32");
        btnPret.setDisable(true);
        btnPret.setOnAction(_ -> passerEnModeBataille());

        // On ajoute la zoneSelectionBateaux au menu !
        panneauPlacement.getChildren().addAll(titre, instructions, zoneSelectionBateaux, btnAleatoire, btnVider, btnPret);
    }

    private Button styleBouton(String texte, String couleurHex) {
        Button btn = new Button(texte);
        btn.setFont(Font.font(TEXT_FONT, 16));
        btn.setPrefSize(260, 50);

        btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + couleurHex + "; -fx-text-fill: " + couleurHex + "; -fx-border-width: 2px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + couleurHex + "; -fx-text-fill: #1a2230; -fx-border-width: 2px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + couleurHex + "; -fx-text-fill: " + couleurHex + "; -fx-border-width: 2px; -fx-cursor: hand;"));
        return btn;
    }

    private void viderGrille() {
        etatJeuBackend.getJoueur1().getGrilleOcean().vider();
        etatJeuBackend.getJoueur1().getFlotte().clear();
        rechargerFlotteRestante();

        panneauPlacement.getChildren().remove(zoneSelectionBateaux);
        zoneSelectionBateaux = new SelectBoard(flotteRestante);
        panneauPlacement.getChildren().add(2, zoneSelectionBateaux);

        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        btnPret.setDisable(true);
    }

    private void placerMesBateauxAleatoirement() {
        viderGrille();

        for (Vaisseau navire : new ArrayList<>(flotteRestante)) {
            boolean place = false;
            while (!place) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                boolean horiz = random.nextBoolean();
                place = etatJeuBackend.getJoueur1().getGrilleOcean().placerVaisseau(navire, x, y, horiz);
                if (place) etatJeuBackend.getJoueur1().getFlotte().add(navire);
            }
        }

        flotteRestante.clear();
        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        btnPret.setDisable(false);
    }

    private void passerEnModeBataille() {
        placerBateauxCPU();
        phaseBataille = true;

        layoutPrincipal.setLeft(null);

        HBox zoneBataille = new HBox(60, conteneurOcean, conteneurRadar, sideBar);
        zoneBataille.setAlignment(Pos.CENTER);

        layoutPrincipal.setCenter(zoneBataille);
        layoutPrincipal.setRight(sideBar); // On affiche la SideBar à droite !

        sideBar.setPhase("DEBUT DE LA PHASE DE COMBAT : À VOUS !");
        sideBar.ajouterLog("Flotte en position. La bataille commence !", "ALERTE");
        notificationBox.afficherAlerte("BATAILLE IMMINENTE", "#ff3333");
    }


    private GrilleUI creerGrilleOcean() {
        GrilleUI grille = new GrilleUI();
        grille.setListener(new GrilleUI.GrilleListener() {
            @Override public void onCaseLeftClick(int x, int y) {}
            @Override public void onCaseRightClick(int x, int y) {}
            @Override
            public String onDragStart(int x, int y) {
                if (phaseBataille) return null;

                Vaisseau navire = etatJeuBackend.getJoueur1().getGrilleOcean().getVaisseauAt(x, y);
                if (navire == null) return null;

                boolean estHoriz = true;
                if ((x + 1 < 10 && etatJeuBackend.getJoueur1().getGrilleOcean().getVaisseauAt(x + 1, y) == navire) ||
                        (x - 1 >= 0 && etatJeuBackend.getJoueur1().getGrilleOcean().getVaisseauAt(x - 1, y) == navire)) {
                    estHoriz = true;
                } else if ((y + 1 < 10 && etatJeuBackend.getJoueur1().getGrilleOcean().getVaisseauAt(x, y + 1) == navire) ||
                        (y - 1 >= 0 && etatJeuBackend.getJoueur1().getGrilleOcean().getVaisseauAt(x, y - 1) == navire)) {
                    estHoriz = false;
                }

                etatJeuBackend.getJoueur1().getGrilleOcean().retirerVaisseau(navire);
                etatJeuBackend.getJoueur1().getFlotte().remove(navire);

                flotteRestante.add(navire);

                panneauPlacement.getChildren().remove(zoneSelectionBateaux);
                zoneSelectionBateaux = new SelectBoard(flotteRestante);
                panneauPlacement.getChildren().add(2, zoneSelectionBateaux);

                grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
                btnPret.setDisable(true);

                return navire.getNom() + ";" + estHoriz;
            }

            @Override
            public void onDragOver(int hoverX, int hoverY, String nomNavire, boolean estHorizontal) {
                if (phaseBataille) return;

                Vaisseau navire = flotteRestante.stream().filter(v -> v.getNom().equals(nomNavire)).findFirst().orElse(null);
                if (navire == null) return;

                grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
                boolean valide = etatJeuBackend.getJoueur1().getGrilleOcean().estPlacementValide(navire, hoverX, hoverY, estHorizontal);
                Color couleurApercu = valide ? Color.color(0, 1, 0, 0.6) : Color.color(1, 0, 0, 0.6);

                for (int i = 0; i < navire.getTaille(); i++) {
                    int currentX = estHorizontal ? hoverX + i : hoverX;
                    int currentY = !estHorizontal ? hoverY + i : hoverY;
                    if (currentX < 10 && currentY < 10) grille.colorierCase(currentX, currentY, couleurApercu);
                }
            }

            @Override
            public void onDragExited() {
                if (!phaseBataille) grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
            }

            @Override
            public void onDragDropped(int dropX, int dropY, String nomNavire, boolean estHorizontal) {
                if (phaseBataille) return;

                Vaisseau navire = flotteRestante.stream().filter(v -> v.getNom().equals(nomNavire)).findFirst().orElse(null);
                if (navire == null) return;

                boolean success = etatJeuBackend.getJoueur1().getGrilleOcean().placerVaisseau(navire, dropX, dropY, estHorizontal);

                if (success) {
                    etatJeuBackend.getJoueur1().getFlotte().add(navire);
                    flotteRestante.remove(navire);
                    zoneSelectionBateaux.retirerVaisseau(nomNavire);
                    grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());

                    if (flotteRestante.isEmpty()) {
                        btnPret.setDisable(false);
                    }
                }
            }
        });
        return grille;
    }

    private GrilleUI creerGrilleRadar(GrilleUI oceanRef) {
        GrilleUI radar = new GrilleUI();
        radar.setListener(new GrilleUI.GrilleListener() {
            @Override
            public void onCaseLeftClick(int x, int y) {
                if (!phaseBataille) {
                    notificationBox.afficherAlerte("PHASE DE DÉPLOIEMENT", "#ffaa00");
                    return;
                }
                if (etatJeuBackend.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] != null) return;

                Vaisseau cibleAdverse = etatJeuBackend.getJoueur2().getGrilleOcean().getVaisseauAt(x, y);
                ResultatTir resultat = etatJeuBackend.getJoueur2().getGrilleOcean().recevoirTir(x, y);
                etatJeuBackend.getJoueur1().getGrilleRadar().enregistrerTir(x, y, resultat);

                if (resultat == ResultatTir.RATE) {
                    radar.colorierCase(x, y, Color.WHITE);
                    sideBar.ajouterLog("Tir allié en " + (char)('A' + y) + "-" + (x + 1) + " : Raté", "RATE");
                } else {
                    radar.colorierCase(x, y, Color.RED);
                    if (resultat == ResultatTir.TOUCHE) {
                        sideBar.ajouterLog("Cible ennemie touchée !", "TOUCHE");
                    } else {
                        sideBar.ajouterLog("BOUM ! " + cibleAdverse.getNom() + " ennemi coulé !", "ALERTE");
                        notificationBox.afficherAlerte("NAVIRE ENNEMI DÉTRUIT", "#ffaa00");
                    }
                }

                verifierFinDePartie();
                if (!phaseBataille) return;

                // Tour du CPU
                sideBar.setPhase("TOUR DU CPU...");

                com.almasb.fxgl.dsl.FXGL.getGameTimer().runOnceAfter(() -> {
                    riposteDuCPU(oceanRef);
                    etatJeuBackend.setTourCourant(etatJeuBackend.getTourCourant() + 1);
//                    sideBar.setTour(etatJeuBackend.getTourCourant());
                    sideBar.setPhase("PHASE DE COMBAT : À VOUS !");
                    verifierFinDePartie();
                }, javafx.util.Duration.seconds(1.0));
            }
            @Override public void onCaseRightClick(int x, int y) {}
            @Override public void onDragOver(int x, int y, String nomNavire, boolean estHorizontal) {}
            @Override public void onDragDropped(int x, int y, String nomNavire, boolean estHorizontal) {}
            @Override public void onDragExited() {}
            @Override public String onDragStart(int x, int y) { return null; }
        });
        return radar;
    }

    private void placerBateauxCPU() {
        List<Vaisseau> flotteCPU = Arrays.asList(
                new Vaisseau("Porte-avions", 5), new Vaisseau("Cuirassé", 4),
                new Vaisseau("Destroyer", 3), new Vaisseau("Sous-marin", 3), new Vaisseau("Patrouilleur", 2)
        );
        for (Vaisseau navire : flotteCPU) {
            boolean place = false;
            while (!place) {
                place = etatJeuBackend.getJoueur2().getGrilleOcean().placerVaisseau(navire, random.nextInt(10), random.nextInt(10), random.nextBoolean());
                if (place) etatJeuBackend.getJoueur2().getFlotte().add(navire);
            }
        }
    }

    private void riposteDuCPU(GrilleUI oceanRef) {
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
            oceanRef.colorierCase(cibleX, cibleY, Color.LIGHTCYAN);
            sideBar.ajouterLog("CPU : Tir raté en " + (char)('A' + cibleY) + "-" + (cibleX + 1), "RATE");
        } else {
            oceanRef.colorierCase(cibleX, cibleY, Color.DARKRED);
            if (resultatCPU == ResultatTir.TOUCHE) {
                sideBar.ajouterLog("ALERTE ! Navire allié touché !", "TOUCHE");
                notificationBox.afficherAlerte("IMPACT CONFIRMÉ SUR NOTRE FLOTTE", "#ff3333");
            } else {
                sideBar.ajouterLog("DÉSASTRE ! " + notreCible.getNom() + " allié coulé !", "ALERTE");
                notificationBox.afficherAlerte("NAVIRE ALLIÉ PERDU", "#ff0000");
            }
        }
    }

    private void verifierFinDePartie(){
        if (etatJeuBackend.getJoueur2().aPerdu()){
            phaseBataille = false;
            afficherEcranFin("VICTOIRE !\nVous avez détruit la flotte ennemie !", Color.LIMEGREEN);
        } else if (etatJeuBackend.getJoueur1().aPerdu()){
            phaseBataille = false;
            afficherEcranFin("DÉFAITE...\nLe CPU a coulé votre flotte.", Color.RED);
        }
    }

    private void afficherEcranFin(String message, Color couleur) {
        Rectangle voileObscur = new Rectangle(
                com.almasb.fxgl.dsl.FXGL.getAppWidth(), com.almasb.fxgl.dsl.FXGL.getAppHeight(), Color.color(0, 0, 0, 0.8));
        Text texteFin = new Text(message);
        texteFin.setFont(Font.font(TEXT_FONT, 40));
        texteFin.setFill(couleur);
        texteFin.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Button btnQuitter = styleBouton("QUITTER", "#ff0000");
        btnQuitter.setOnAction(_ -> com.almasb.fxgl.dsl.FXGL.getGameController().exit());
        VBox ecranFin = new VBox(40, texteFin, btnQuitter);
        ecranFin.setAlignment(Pos.CENTER);

        // On ajoute l'écran de fin par-dessus tout le reste dans le StackPane
        racineVisuelle.getChildren().addAll(voileObscur, ecranFin);
    }
}