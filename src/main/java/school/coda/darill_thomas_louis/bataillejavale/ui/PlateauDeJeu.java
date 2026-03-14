package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import school.coda.darill_thomas_louis.bataillejavale.controller.GestionnairePlacement;
import school.coda.darill_thomas_louis.bataillejavale.controller.PartieControleur;
import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.ModeJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

public class PlateauDeJeu {

    // ==========================================
    // CONSTANTES DE DESIGN
    // ==========================================
    private static final String COLOR_CYAN_HEX = "#00ffff";
    private static final String COLOR_RED_HEX  = "#ff0000";
    private static final String COLOR_WARN_HEX = "#ffaa00";
    private static final String COLOR_BTN_BG   = "#0c121e";
    private static final String COLOR_PAUSE_IDLE = "#a0aab5";

    private static final int PANEL_LEFT_WIDTH = 320;
    private static final int NOTIF_OFFSET_X = 160;

    // ==========================================
    // CONTRÔLEURS
    // ==========================================
    private final PartieControleur controleur;
    private final GestionnairePlacement gestionnairePlacement;

    // ==========================================
    // COMPOSANTS GRAPHIQUES
    // ==========================================
    private StackPane racineVisuelle;
    private BorderPane layoutPrincipal;

    private GrilleUI vueOcean;
    private GrilleUI vueRadar;
    private SideBarUI sideBar;
    private NotificationUI notificationBox;
    private SelectBoard zoneSelectionBateaux;
    private VBox conteneurOcean;
    private VBox conteneurRadar;
    private VBox panneauPlacement;

    private Button btnPret;
    private Node boutonPause;
    private StackPane voileAttenteRadar;
    private PauseMenuUI menuPause;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================

    public PlateauDeJeu(ModeJeu mode) {
        this.controleur = new PartieControleur(this, mode, null, -1);
        this.gestionnairePlacement = new GestionnairePlacement(controleur, this);
        initialiserBaseUI(mode.toString());
        rafraichirOcean();
        notificationBox.afficherAlerte("DÉPLOIEMENT DE FLOTTE !", COLOR_CYAN_HEX);
    }

    public PlateauDeJeu(ModeJeu mode, EtatJeu sauvegarde, int idPartie) {
        this.controleur = new PartieControleur(this, mode, sauvegarde, idPartie);
        this.gestionnairePlacement = new GestionnairePlacement(controleur, this);
        initialiserBaseUI("Sauvegarde (ID: " + idPartie + ")");
        controleur.initialiserPartieExistante();
        sideBar.setTexteManche(controleur.getEtat().getMancheActuelle());
    }

    private void initialiserBaseUI(String logInfo) {
        initialiserComposants();
        construireConteneursCentraux();
        assemblerRacineVisuelle();
        sideBar.ajouterLog("Système initialisé. " + logInfo, "INFO");
    }

    // ==========================================
    // 1. CRÉATION DES COMPOSANTS
    // ==========================================

    private void initialiserComposants() {
        sideBar = new SideBarUI();
        notificationBox = new NotificationUI();
        vueOcean = creerGrilleOcean();
        vueRadar = creerGrilleRadar();
        voileAttenteRadar = creerVoileAttente();
        boutonPause = creerBoutonPause();
    }

    private void construireConteneursCentraux() {
        // Zone Radar
        StackPane blocRadarEtVoile = new StackPane(vueRadar, voileAttenteRadar);
        conteneurRadar = assemblerConteneurGrille("ZONE ENNEMIE", Color.RED, blocRadarEtVoile);

        ColorAdjust desaturate = new ColorAdjust();
        desaturate.setBrightness(-0.5);
        conteneurRadar.setEffect(desaturate);

        // Zone Océan
        conteneurOcean = assemblerConteneurGrille("ZONE ALLIÉE", Color.CYAN, vueOcean);

        creerPanneauPlacement();
    }

    private void assemblerRacineVisuelle() {
        layoutPrincipal = new BorderPane();
        layoutPrincipal.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        layoutPrincipal.setLeft(panneauPlacement);
        layoutPrincipal.setCenter(conteneurOcean);

        Rectangle fond = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        fond.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#050814")), new Stop(1, Color.web("#0a1526"))));

        AnchorPane calqueAbsolu = new AnchorPane();
        calqueAbsolu.setPickOnBounds(false);
        AnchorPane.setTopAnchor(boutonPause, 25.0);
        AnchorPane.setLeftAnchor(boutonPause, (double) PANEL_LEFT_WIDTH + 30.0);
        calqueAbsolu.getChildren().add(boutonPause);

        notificationBox.setTranslateX(NOTIF_OFFSET_X);

        racineVisuelle = new StackPane(fond, layoutPrincipal, notificationBox, calqueAbsolu);
        racineVisuelle.setFocusTraversable(true);
        racineVisuelle.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                toggleMenuPause();
                event.consume();
            }
        });

        javafx.application.Platform.runLater(() -> racineVisuelle.requestFocus());
    }

    // ==========================================
    // 2. Builder de nos UI elements
    // ==========================================

    private void creerPanneauPlacement() {
        panneauPlacement = new VBox(20);
        panneauPlacement.setAlignment(Pos.TOP_CENTER);
        panneauPlacement.setStyle("-fx-background-color: transparent; -fx-padding: 30 20 30 10; -fx-border-color: " + COLOR_CYAN_HEX + "; -fx-border-width: 0 2px 0 0;");
        panneauPlacement.setPrefWidth(PANEL_LEFT_WIDTH);

        zoneSelectionBateaux = new SelectBoard(controleur.getFlotteRestante());

        ScrollPane scrollBateaux = new ScrollPane(zoneSelectionBateaux);
        scrollBateaux.setFitToWidth(true);
        scrollBateaux.setPrefHeight(400);
        scrollBateaux.setMinHeight(250);
        scrollBateaux.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollBateaux.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollBateaux.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        btnPret = styleBouton("DÉMARRER BATAILLE", "#32cd32");
        btnPret.setDisable(true);
        btnPret.setOnAction(_ -> controleur.passerEnModeBataille(true));

        panneauPlacement.getChildren().addAll(
                creerTexte("DÉPLOIEMENT", 32, Color.WHITE, new DropShadow(10, Color.web(COLOR_CYAN_HEX))),
                creerTexte("CLIC DROIT : PIVOTER\nGLISSER : PLACER", 14, Color.web(COLOR_CYAN_HEX, 0.7), null),
                scrollBateaux,
                creerBoutonGenerique("PLACEMENT ALÉATOIRE", COLOR_CYAN_HEX, () -> gestionnairePlacement.placerMesBateauxAleatoirement(vueOcean)),
                creerBoutonGenerique("VIDER LA GRILLE", COLOR_WARN_HEX, () -> gestionnairePlacement.viderGrille(vueOcean)),
                btnPret
        );
    }

    private Text creerTexte(String contenu, int taille, Color couleur, DropShadow effet) {
        Text t = new Text(contenu);
        t.setFont(FontUtils.getPolice(taille));
        t.setFill(couleur);
        t.setTextAlignment(TextAlignment.CENTER);
        if (effet != null) t.setEffect(effet);
        return t;
    }

    private Button creerBoutonGenerique(String texte, String couleurHex, Runnable action) {
        Button btn = styleBouton(texte, couleurHex);
        btn.setOnAction(_ -> action.run());
        return btn;
    }

    private Button styleBouton(String texte, String couleur) {
        Button btn = new Button(texte);
        btn.setFont(FontUtils.getPolice(18));
        btn.setPrefSize(260, 45);
        String baseStyle = "-fx-background-color: " + COLOR_BTN_BG + "; -fx-border-color: " + couleur + "; -fx-text-fill: " + couleur + "; -fx-border-width: 2px; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + couleur + "; -fx-border-color: " + couleur + "; -fx-text-fill: " + COLOR_BTN_BG + "; -fx-border-width: 2px; -fx-cursor: hand;";

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(_ -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(_ -> btn.setStyle(baseStyle));
        return btn;
    }

    private Node creerBoutonPause() {
        Text escText = creerTexte("ESC", 14, Color.web(COLOR_PAUSE_IDLE), null);

        Rectangle barre1 = new Rectangle(2, 12, Color.web(COLOR_PAUSE_IDLE));
        Rectangle barre2 = new Rectangle(2, 12, Color.web(COLOR_PAUSE_IDLE));
        HBox iconPause = new HBox(3, barre1, barre2);
        iconPause.setAlignment(Pos.CENTER);

        StackPane iconBox = new StackPane(iconPause);
        iconBox.setPrefSize(28, 28);
        iconBox.setStyle("-fx-border-color: " + COLOR_PAUSE_IDLE + "; -fx-border-width: 1px; -fx-background-color: transparent;");

        HBox boutonGlobal = new HBox(8, escText, iconBox);
        boutonGlobal.setAlignment(Pos.CENTER);
        boutonGlobal.setCursor(javafx.scene.Cursor.HAND);

        boutonGlobal.setOnMouseEntered(_ -> {
            Color cyan = Color.web(COLOR_CYAN_HEX);
            escText.setFill(cyan);
            barre1.setFill(cyan);
            barre2.setFill(cyan);
            iconBox.setStyle("-fx-border-color: " + COLOR_CYAN_HEX + "; -fx-border-width: 1px; -fx-background-color: rgba(0, 255, 255, 0.1);");
            iconBox.setEffect(new DropShadow(10, cyan));
        });

        boutonGlobal.setOnMouseExited(_ -> {
            Color idle = Color.web(COLOR_PAUSE_IDLE);
            escText.setFill(idle);
            barre1.setFill(idle);
            barre2.setFill(idle);
            iconBox.setStyle("-fx-border-color: " + COLOR_PAUSE_IDLE + "; -fx-border-width: 1px; -fx-background-color: transparent;");
            iconBox.setEffect(null);
        });

        boutonGlobal.setOnMouseClicked(event -> {
            toggleMenuPause();
            event.consume();
        });

        return boutonGlobal;
    }

    private VBox assemblerConteneurGrille(String titre, Color couleurTexte, Node elementCentral) {
        VBox conteneur = new VBox(15, creerTexte(titre, 20, couleurTexte, null), elementCentral);
        conteneur.setAlignment(Pos.CENTER);
        return conteneur;
    }

    private StackPane creerVoileAttente() {
        StackPane voile = new StackPane();
        voile.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        Text texteAttente = creerTexte("EN ATTENTE...", 25, Color.RED, null);
        voile.getChildren().add(texteAttente);
        voile.setVisible(false);
        voile.setOpacity(0);
        return voile;
    }

    // ==========================================
    // 3. LOGIQUE DES GRILLES (Listeners)
    // ==========================================

    private GrilleUI creerGrilleOcean() {
        GrilleUI grille = new GrilleUI(COLOR_CYAN_HEX);
        grille.setListener(new GrilleUI.GrilleListener() {
            @Override public void onCaseLeftClick(int x, int y) {}
            @Override public void onCaseRightClick(int x, int y) {}
            @Override public String onDragStart(int x, int y) { return gestionnairePlacement.gererDragStartOcean(grille, x, y); }
            @Override public void onDragOver(int x, int y, String n, boolean h) { gestionnairePlacement.gererDragOverOcean(grille, x, y, n, h); }
            @Override public void onDragExited() { if (!controleur.isPhaseBataille()) rafraichirOcean(); }
            @Override public void onDragDropped(int x, int y, String n, boolean h) { gestionnairePlacement.gererDragDroppedOcean(grille, x, y, n, h); }
        });
        return grille;
    }

    private GrilleUI creerGrilleRadar() {
        GrilleUI radar = new GrilleUI(COLOR_RED_HEX);
        radar.setListener(new GrilleUI.GrilleListener() {
            @Override public void onCaseLeftClick(int x, int y) { controleur.gererTirJoueur(x, y); }
            @Override public void onCaseRightClick(int x, int y) {}
            @Override public void onDragOver(int x, int y, String n, boolean h) {}
            @Override public void onDragDropped(int x, int y, String n, boolean h) {}
            @Override public void onDragExited() {}
            @Override public String onDragStart(int x, int y) { return null; }
        });
        return radar;
    }

    // ==========================================
    // 4. GETTERS & ACTIONS SIMPLES
    // ==========================================

    public StackPane getRacineVisuelle() { return racineVisuelle; }
    public Button getBtnPret() { return btnPret; }

    public void actualiserMenuBateaux() {
        ScrollPane scroll = (ScrollPane) panneauPlacement.getChildren().get(2);
        zoneSelectionBateaux = new SelectBoard(controleur.getFlotteRestante());
        scroll.setContent(zoneSelectionBateaux);
    }

    public void retirerVaisseauZoneSelection(String nomNavire) {
        zoneSelectionBateaux.retirerVaisseau(nomNavire);
    }

    public void rafraichirOcean() {
        vueOcean.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean());
    }

    public void notifierAdversaireRejoint() {
        notificationBox.afficherAlerte("L'ADVERSAIRE A REJOINT !", COLOR_CYAN_HEX);
    }

    // ==========================================
    // 5. GESTION DES PHASES DE JEU
    // ==========================================

    public void afficherModeBataille(boolean aMoi, int idPartie, ModeJeu modeActuel) {
        layoutPrincipal.setLeft(null);
        HBox zoneBataille = new HBox(30, conteneurOcean, conteneurRadar);
        zoneBataille.setAlignment(Pos.CENTER);

        layoutPrincipal.setCenter(zoneBataille);
        layoutPrincipal.setRight(sideBar);

        // Repositionnement dynamique pour le mode bataille
        if (boutonPause != null) {
            AnchorPane.setLeftAnchor(boutonPause, 30.0);
        }
        notificationBox.setTranslateX(-NOTIF_OFFSET_X);

        if (aMoi) activerMonTour(controleur.getEtat().getMancheActuelle());
        else bloquerTour("ATTENTE ADVERSAIRE...", modeActuel == ModeJeu.MULTI_HOTE ? "ID: " + idPartie : "RÉFLEXION ENNEMIE...");
    }

    public void activerMonTour(int tour) {
        sideBar.setTexteManche(tour);
        sideBar.setPhase("PHASE DE BATAILLE : À VOUS !");
        conteneurRadar.setEffect(new DropShadow(25, Color.web(COLOR_RED_HEX)));

        FadeTransition ft = new FadeTransition(Duration.seconds(0.3), voileAttenteRadar);
        ft.setToValue(0.0);
        ft.setOnFinished(_ -> voileAttenteRadar.setVisible(false));
        ft.play();
    }

    public void bloquerTour(String phase, String message) {
        sideBar.setPhase(phase);
        conteneurRadar.setEffect(null);

        voileAttenteRadar.setVisible(true);
        ((Text) voileAttenteRadar.getChildren().get(0)).setText(message);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), voileAttenteRadar);
        ft.setToValue(1.0);
        ft.play();
    }

    public void afficherImpactVisuel(int x, int y, ResultatTir res, Vaisseau cible, boolean moiTire) {
        GrilleUI grille = moiTire ? vueRadar : vueOcean;

        if (res == ResultatTir.RATE) {
            grille.colorierCase(x, y, moiTire ? Color.WHITE : Color.LIGHTCYAN);
            sideBar.ajouterLog((moiTire ? "Tir allié" : "Tir ennemi") + " en " + (char)('A' + y) + "-" + (x + 1) + " : Raté", "RATE");
        } else {
            grille.colorierCase(x, y, moiTire ? Color.RED : Color.DARKRED);
            if (res == ResultatTir.TOUCHE) {
                sideBar.ajouterLog(moiTire ? "Cible touchée !" : "Navire allié touché !", "TOUCHE");
            } else {
                sideBar.ajouterLog(moiTire ? "BOUM ! " + cible.getNom() + " coulé !" : "DÉSASTRE ! " + cible.getNom() + " coulé !", "ALERTE");
                notificationBox.afficherAlerte(moiTire ? "NAVIRE ENNEMI DÉTRUIT" : "NAVIRE ALLIÉ PERDU", moiTire ? COLOR_WARN_HEX : COLOR_RED_HEX);
            }
        }
    }

    public void restaurerVisuelBataille(EtatJeu etat) {
        rafraichirOcean();
        for(int x=0; x<10; x++) {
            for(int y=0; y<10; y++) {
                ResultatTir tirJ1 = etat.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y];
                if(tirJ1 != null) {
                    vueRadar.colorierCase(x, y, tirJ1 == ResultatTir.RATE ? Color.WHITE : Color.RED);
                }

                ResultatTir tirJ2 = etat.getJoueur2().getGrilleRadar().getHistoriqueTirs()[x][y];
                if(tirJ2 != null) {
                    vueOcean.colorierCase(x, y, tirJ2 == ResultatTir.RATE ? Color.LIGHTCYAN : Color.DARKRED);
                }
            }
        }
    }

    private void toggleMenuPause() {
        if (menuPause != null && racineVisuelle.getChildren().contains(menuPause)) {
            racineVisuelle.getChildren().remove(menuPause);
            menuPause = null;
        } else {
            menuPause = new PauseMenuUI(
                    this::toggleMenuPause,
                    () -> {
                        controleur.stopperControleur();
                        FXGL.getGameScene().clearUINodes();
                        FXGL.addUINode(new MenuUI());
                    }
            );
            racineVisuelle.getChildren().add(menuPause);
        }
    }

    public void afficherEcranFin(boolean victoire) {
        Rectangle voile = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.color(0, 0, 0, 0.8));

        Text t = creerTexte(victoire ? "VICTOIRE !\nVous avez détruit la flotte !" : "DÉFAITE...\nVotre flotte a été coulée.",
                40, victoire ? Color.LIMEGREEN : Color.RED, null);
        t.setFont(Font.font("Impact", 40));

        Button bM = creerBoutonGenerique("RETOUR AU MENU", COLOR_CYAN_HEX, () -> {
            controleur.stopperControleur();
            FXGL.getGameScene().clearUINodes();
            FXGL.addUINode(new MenuUI());
        });

        Button bQ = creerBoutonGenerique("QUITTER", COLOR_RED_HEX, () -> FXGL.getGameController().exit());

        VBox box = new VBox(40, t, new HBox(30, bM, bQ));
        box.setAlignment(Pos.CENTER);

        racineVisuelle.getChildren().addAll(voile, box);
    }
}