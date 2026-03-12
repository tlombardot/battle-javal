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
import school.coda.darill_thomas_louis.bataillejavale.core.model.GrilleOcean;
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

        conteneurOcean = assemblerConteneurGrille("ZONE ALLIÉE (Océan)", Color.CYAN, vueOcean, "#00ffff");
        conteneurRadar = assemblerConteneurGrille("ZONE ENNEMIE (Radar)", Color.RED, vueRadar, "#ff0000");

        ColorAdjust desaturate = new ColorAdjust();
        desaturate.setBrightness(-0.5);
        conteneurRadar.setEffect(desaturate);

        creerPanneauPlacement();

        Rectangle fondPlateau = creerFondEcran();

        layoutPrincipal = new BorderPane();
        layoutPrincipal.setPrefWidth(FXGL.getAppWidth());
        layoutPrincipal.setPrefHeight(FXGL.getAppHeight());
        BorderPane.setMargin(panneauPlacement, new Insets(0, 0, 0, 50));
        layoutPrincipal.setLeft(panneauPlacement);
        layoutPrincipal.setCenter(conteneurOcean);

        racineVisuelle = new StackPane();
        racineVisuelle.getChildren().addAll(fondPlateau, layoutPrincipal, notificationBox);

        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());

        sideBar.ajouterLog("Système initialisé. En attente de déploiement.", "INFO");
        notificationBox.afficherAlerte("DÉPLOIE TA FLOTTE, AMIRAL !", "#00ffff");
    }

    public StackPane getRacineVisuelle() { return racineVisuelle; }

    // ==========================================
    // INITIALISATION ET UI DE BASE
    // ==========================================

    private void initialiserDonneesPartie() {
        etatJeuBackend = new EtatJeu();
        phaseBataille = false;
        rechargerFlotteRestante();
    }

    private void rechargerFlotteRestante() {
        flotteRestante = new ArrayList<>(Arrays.asList(
                new Vaisseau("Porte-avions", 5), new Vaisseau("Cuirassé", 4),
                new Vaisseau("Destroyer", 3), new Vaisseau("Sous-marin", 3), new Vaisseau("Patrouilleur", 2)
        ));
    }

    private Rectangle creerFondEcran() {
        Rectangle fond = new Rectangle();
        Stop[] stops = new Stop[] { new Stop(0, Color.web("#050814")), new Stop(1, Color.web("#0a1526")) };
        LinearGradient bgGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        fond.setFill(bgGradient);
        fond.setWidth(FXGL.getAppWidth());
        fond.setHeight(FXGL.getAppHeight());
        return fond;
    }

    private VBox assemblerConteneurGrille(String titre, Color couleurTexte, GrilleUI grille, String couleurNeon) {
        Text texte = new Text(titre);
        texte.setFont(Font.font(TEXT_FONT, 20));
        texte.setFill(couleurTexte);
        VBox conteneur = new VBox(15, texte, grille);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setEffect(new DropShadow(25, Color.web(couleurNeon)));
        return conteneur;
    }

    // ==========================================
    // GESTION DU PANNEAU DE PLACEMENT
    // ==========================================

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

    private void actualiserMenuBateaux() {
        panneauPlacement.getChildren().remove(zoneSelectionBateaux);
        zoneSelectionBateaux = new SelectBoard(flotteRestante);
        panneauPlacement.getChildren().add(2, zoneSelectionBateaux);
    }

    private void viderGrille() {
        etatJeuBackend.getJoueur1().getGrilleOcean().vider();
        etatJeuBackend.getJoueur1().getFlotte().clear();
        rechargerFlotteRestante();
        actualiserMenuBateaux();
        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        btnPret.setDisable(true);
    }

    private void placerMesBateauxAleatoirement() {
        viderGrille();
        for (Vaisseau navire : new ArrayList<>(flotteRestante)) {
            boolean place = false;
            while (!place) {
                place = etatJeuBackend.getJoueur1().getGrilleOcean().placerVaisseau(navire, random.nextInt(10), random.nextInt(10), random.nextBoolean());
                if (place) etatJeuBackend.getJoueur1().getFlotte().add(navire);
            }
        }
        flotteRestante.clear();
        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        btnPret.setDisable(false);
    }

    // ==========================================
    // CRÉATION DES GRILLES
    // ==========================================

    private GrilleUI creerGrilleOcean() {
        GrilleUI grille = new GrilleUI();
        grille.setListener(new GrilleUI.GrilleListener() {
            @Override public void onCaseLeftClick(int x, int y) { /* On ne l'utilise pas */ }
            @Override public void onCaseRightClick(int x, int y) { /* On ne l'utilise pas */ }

            @Override public String onDragStart(int x, int y) {
                return gererDragStartOcean(grille, x, y);
            }
            @Override public void onDragOver(int x, int y, String nomNavire, boolean estHorizontal) {
                gererDragOverOcean(grille, x, y, nomNavire, estHorizontal);
            }
            @Override public void onDragExited() {
                if (!phaseBataille) grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
            }
            @Override public void onDragDropped(int x, int y, String nomNavire, boolean estHorizontal) {
                gererDragDroppedOcean(grille, x, y, nomNavire, estHorizontal);
            }
        });
        return grille;
    }

    private GrilleUI creerGrilleRadar(GrilleUI oceanRef) {
        GrilleUI radar = new GrilleUI();
        radar.setListener(new GrilleUI.GrilleListener() {
            @Override public void onCaseLeftClick(int x, int y) { gererTirJoueur(radar, oceanRef, x, y); }
            @Override public void onCaseRightClick(int x, int y) { /* On ne l'utilise pas */ }
            @Override public void onDragOver(int x, int y, String nomNavire, boolean estHorizontal) { /* On ne l'utilise pas */ }
            @Override public void onDragDropped(int x, int y, String nomNavire, boolean estHorizontal) { /* On ne l'utilise pas */ }
            @Override public void onDragExited() { /* On ne l'utilise pas */ }
            @Override public String onDragStart(int x, int y) { return null; }
        });
        return radar;
    }

    // ==========================================
    // LOGIQUE DE DRAG & DROP EXTRAITE
    // ==========================================

    private String gererDragStartOcean(GrilleUI grille, int x, int y) {
        if (phaseBataille) return null;

        Vaisseau navire = etatJeuBackend.getJoueur1().getGrilleOcean().getVaisseauAt(x, y);
        if (navire == null) return null;

        boolean estHoriz = estVaisseauHorizontal(x, y, navire);

        etatJeuBackend.getJoueur1().getGrilleOcean().retirerVaisseau(navire);
        etatJeuBackend.getJoueur1().getFlotte().remove(navire);
        flotteRestante.add(navire);

        actualiserMenuBateaux();
        grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        btnPret.setDisable(true);

        return navire.getNom() + ";" + estHoriz;
    }

    private void gererDragOverOcean(GrilleUI grille, int x, int y, String nomNavire, boolean estHorizontal) {
        if (phaseBataille) return;
        Vaisseau navire = trouverVaisseauRestant(nomNavire);
        if (navire == null) return;

        grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        boolean valide = etatJeuBackend.getJoueur1().getGrilleOcean().estPlacementValide(navire, x, y, estHorizontal);
        Color couleurApercu = valide ? Color.color(0, 1, 0, 0.6) : Color.color(1, 0, 0, 0.6);

        dessinerApercuPlacement(grille, navire, x, y, estHorizontal, couleurApercu);
    }

    private void gererDragDroppedOcean(GrilleUI grille, int x, int y, String nomNavire, boolean estHorizontal) {
        if (phaseBataille) return;
        Vaisseau navire = trouverVaisseauRestant(nomNavire);
        if (navire == null) return;

        boolean success = etatJeuBackend.getJoueur1().getGrilleOcean().placerVaisseau(navire, x, y, estHorizontal);
        if (success) {
            etatJeuBackend.getJoueur1().getFlotte().add(navire);
            flotteRestante.remove(navire);
            zoneSelectionBateaux.retirerVaisseau(nomNavire);
            grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());

            if (flotteRestante.isEmpty()) btnPret.setDisable(false);
        }
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private Vaisseau trouverVaisseauRestant(String nom) {
        return flotteRestante.stream().filter(v -> v.getNom().equals(nom)).findFirst().orElse(null);
    }

    private boolean estVaisseauHorizontal(int x, int y, Vaisseau navire) {
        GrilleOcean ocean = etatJeuBackend.getJoueur1().getGrilleOcean();
        return (x + 1 < 10 && ocean.getVaisseauAt(x + 1, y) == navire) ||
                (x - 1 >= 0 && ocean.getVaisseauAt(x - 1, y) == navire);
    }

    private void dessinerApercuPlacement(GrilleUI grille, Vaisseau navire, int startX, int startY, boolean horizontal, Color couleur) {
        for (int i = 0; i < navire.getTaille(); i++) {
            int curX = horizontal ? startX + i : startX;
            int curY = !horizontal ? startY + i : startY;
            if (curX < 10 && curY < 10) grille.colorierCase(curX, curY, couleur);
        }
    }

    // ==========================================
    // LOGIQUE DE BATAILLE
    // ==========================================

    private void passerEnModeBataille() {
        placerBateauxCPU();
        phaseBataille = true;

        layoutPrincipal.setLeft(null);
        HBox zoneBataille = new HBox(60, conteneurOcean, conteneurRadar, sideBar);
        zoneBataille.setAlignment(Pos.CENTER);
        layoutPrincipal.setCenter(zoneBataille);
        layoutPrincipal.setRight(sideBar);

        sideBar.setPhase("DEBUT DE LA PHASE DE COMBAT : À VOUS !");
        sideBar.ajouterLog("Flotte en position. La bataille commence !", "ALERTE");
        notificationBox.afficherAlerte("BATAILLE IMMINENTE", "#ff3333");
    }

    private void gererTirJoueur(GrilleUI radar, GrilleUI oceanRef, int x, int y) {
        if (!phaseBataille) {
            notificationBox.afficherAlerte("PHASE DE DÉPLOIEMENT", "#ffaa00");
            return;
        }
        if (etatJeuBackend.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] != null) return;

        Vaisseau cibleAdverse = etatJeuBackend.getJoueur2().getGrilleOcean().getVaisseauAt(x, y);
        ResultatTir resultat = etatJeuBackend.getJoueur2().getGrilleOcean().recevoirTir(x, y);
        etatJeuBackend.getJoueur1().getGrilleRadar().enregistrerTir(x, y, resultat);

        afficherResultatTirJoueur(radar, x, y, resultat, cibleAdverse);

        verifierFinDePartie();
        if (!phaseBataille) return;

        sideBar.setPhase("TOUR DU CPU...");
        FXGL.getGameTimer().runOnceAfter(() -> {
            riposteDuCPU(oceanRef);
            etatJeuBackend.setTourCourant(etatJeuBackend.getTourCourant() + 1);
            sideBar.setPhase("PHASE DE COMBAT : À VOUS !");
            verifierFinDePartie();
        }, javafx.util.Duration.seconds(1.0));
    }

    private void afficherResultatTirJoueur(GrilleUI radar, int x, int y, ResultatTir resultat, Vaisseau cible) {
        if (resultat == ResultatTir.RATE) {
            radar.colorierCase(x, y, Color.WHITE);
            sideBar.ajouterLog("Tir allié en " + (char)('A' + y) + "-" + (x + 1) + " : Raté", "RATE");
        } else {
            radar.colorierCase(x, y, Color.RED);
            if (resultat == ResultatTir.TOUCHE) {
                sideBar.ajouterLog("Cible ennemie touchée !", "TOUCHE");
            } else {
                sideBar.ajouterLog("BOUM ! " + cible.getNom() + " ennemi coulé !", "ALERTE");
                notificationBox.afficherAlerte("NAVIRE ENNEMI DÉTRUIT", "#ffaa00");
            }
        }
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

    private void verifierFinDePartie() {
        if (etatJeuBackend.getJoueur2().aPerdu()) {
            phaseBataille = false;
            afficherEcranFin("VICTOIRE !\nVous avez détruit la flotte ennemie !", Color.LIMEGREEN);
        } else if (etatJeuBackend.getJoueur1().aPerdu()) {
            phaseBataille = false;
            afficherEcranFin("DÉFAITE...\nLe CPU a coulé votre flotte.", Color.RED);
        }
    }

    private void afficherEcranFin(String message, Color couleur) {
        Rectangle voileObscur = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.color(0, 0, 0, 0.8));
        Text texteFin = new Text(message);
        texteFin.setFont(Font.font(TEXT_FONT, 40));
        texteFin.setFill(couleur);
        texteFin.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Button btnQuitter = styleBouton("QUITTER", "#ff0000");
        btnQuitter.setOnAction(_ -> FXGL.getGameController().exit());
        VBox ecranFin = new VBox(40, texteFin, btnQuitter);
        ecranFin.setAlignment(Pos.CENTER);

        racineVisuelle.getChildren().addAll(voileObscur, ecranFin);
    }
}