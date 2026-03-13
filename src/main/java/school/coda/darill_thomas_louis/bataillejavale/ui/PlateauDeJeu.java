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
import javafx.util.Duration;

import school.coda.darill_thomas_louis.bataillejavale.controller.GestionnairePlacement;
import school.coda.darill_thomas_louis.bataillejavale.controller.PartieControleur;
import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.ModeJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;


public class PlateauDeJeu {

    // --- CONTRÔLEURS ---
    private final PartieControleur controleur;
    private final GestionnairePlacement gestionnairePlacement;

    // --- COMPOSANTS GRAPHIQUES ---
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
    private StackPane voileAttenteRadar;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================

    public PlateauDeJeu(ModeJeu mode) {
        this.controleur = new PartieControleur(this, mode, null, -1);
        this.gestionnairePlacement = new GestionnairePlacement(controleur, this);
        initialiserUI();
        rafraichirOcean();
        sideBar.ajouterLog("Système initialisé. Mode : " + mode, "INFO");
        notificationBox.afficherAlerte("DÉPLOIEMENT DE FLOTTE !", "#00ffff");
    }

    public PlateauDeJeu(ModeJeu mode, EtatJeu sauvegarde, int idPartie) {
        this.controleur = new PartieControleur(this, mode, sauvegarde, idPartie);
        this.gestionnairePlacement = new GestionnairePlacement(controleur, this);
        initialiserUI();
        controleur.initialiserPartieExistante();
        sideBar.ajouterLog("Sauvegarde chargée (ID: " + idPartie + ")", "INFO");
        sideBar.setTexteManche(controleur.getEtat().getTourCourant());
    }

    // ==========================================
    // DECOUPAGE UI POUR ALLEGER LA COMPLEXITE
    // ==========================================

    private void initialiserUI() {
        sideBar = new SideBarUI();
        notificationBox = new NotificationUI();
        vueOcean = creerGrilleOcean();
        vueRadar = creerGrilleRadar();
        voileAttenteRadar = creerVoileAttente();

        construireConteneursCentraux();
        assemblerRacineVisuelle();
    }

    private void construireConteneursCentraux() {
        StackPane blocRadarEtVoile = new StackPane(vueRadar, voileAttenteRadar);
        conteneurOcean = assemblerConteneurGrille("ZONE ALLIÉE", Color.CYAN, vueOcean);
        conteneurRadar = assemblerConteneurGrille("ZONE ENNEMIE", Color.RED, blocRadarEtVoile);

        ColorAdjust desaturate = new ColorAdjust();
        desaturate.setBrightness(-0.5);
        conteneurRadar.setEffect(desaturate);

        creerPanneauPlacement();
    }

    private void assemblerRacineVisuelle() {
        layoutPrincipal = new BorderPane();
        layoutPrincipal.setPrefWidth(FXGL.getAppWidth());
        layoutPrincipal.setPrefHeight(FXGL.getAppHeight());
        layoutPrincipal.setLeft(panneauPlacement);
        layoutPrincipal.setCenter(conteneurOcean);

        Rectangle fond = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        fond.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#050814")), new Stop(1, Color.web("#0a1526"))));

        racineVisuelle = new StackPane();
        racineVisuelle.getChildren().addAll(fond, layoutPrincipal, notificationBox);
    }

    private void creerPanneauPlacement() {
        panneauPlacement = new VBox(20);
        panneauPlacement.setAlignment(Pos.TOP_CENTER);
        panneauPlacement.setStyle("-fx-background-color: transparent; -fx-padding: 30 20 30 10; -fx-border-color: #00ffff; -fx-border-width: 0 2px 0 0;");
        panneauPlacement.setPrefWidth(320);

        zoneSelectionBateaux = new SelectBoard(controleur.getFlotteRestante());
        ScrollPane scrollBateaux = creerScrollBateaux();

        btnPret = styleBouton("DÉMARRER BATAILLE", "#32cd32");
        btnPret.setDisable(true);
        btnPret.setOnAction(_ -> controleur.passerEnModeBataille(true));

        panneauPlacement.getChildren().addAll(creerTitrePlacement(), creerInstructionPlacement(), scrollBateaux,
                creerBoutonAleatoire(), creerBoutonVider(), btnPret);
    }

    // --- SOUS-ÉLÉMENTS DU PANNEAU (Pour alléger le code) ---

    private Text creerTitrePlacement() {
        Text titre = new Text("DÉPLOIEMENT");
        titre.setFont(FontUtils.getPolice(32));
        titre.setFill(Color.WHITE);
        titre.setEffect(new DropShadow(10, Color.web("#00ffff")));
        return titre;
    }

    private Text creerInstructionPlacement() {
        Text inst = new Text("CLIC DROIT : PIVOTER\nGLISSER : PLACER");
        inst.setFont(FontUtils.getPolice(14));
        inst.setFill(Color.web("#00ffff", 0.7));
        inst.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return inst;
    }

    private ScrollPane creerScrollBateaux() {
        ScrollPane scroll = new ScrollPane(zoneSelectionBateaux);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);
        scroll.setMinHeight(250);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    private Button creerBoutonAleatoire() {
        Button btn = styleBouton("PLACEMENT ALÉATOIRE", "#00ffff");
        btn.setOnAction(_ -> gestionnairePlacement.placerMesBateauxAleatoirement(vueOcean));
        return btn;
    }

    private Button creerBoutonVider() {
        Button btn = styleBouton("VIDER LA GRILLE", "#ffaa00");
        btn.setOnAction(_ -> gestionnairePlacement.viderGrille(vueOcean));
        return btn;
    }

    // ==========================================
    // VUES SECONDAIRES & HELPERS
    // ==========================================

    public StackPane getRacineVisuelle() { return racineVisuelle; }
    public Button getBtnPret() { return btnPret; }

    private Button styleBouton(String texte, String couleur) {
        Button btn = new Button(texte);
        btn.setFont(FontUtils.getPolice(18));
        btn.setPrefSize(260, 45);
        btn.setStyle("-fx-background-color: #0c121e; -fx-border-color: " + couleur + "; -fx-text-fill: " + couleur + "; -fx-border-width: 2px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + couleur + "; -fx-border-color: " + couleur + "; -fx-text-fill: #0c121e; -fx-border-width: 2px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #0c121e; -fx-border-color: " + couleur + "; -fx-text-fill: " + couleur + "; -fx-border-width: 2px; -fx-cursor: hand;"));
        return btn;
    }

    private VBox assemblerConteneurGrille(String titre, Color couleurTexte, Node elementCentral) {
        Text texte = new Text(titre); texte.setFont(FontUtils.getPolice(20)); texte.setFill(couleurTexte);
        VBox conteneur = new VBox(15, texte, elementCentral); conteneur.setAlignment(Pos.CENTER);
        return conteneur;
    }

    private StackPane creerVoileAttente() {
        StackPane voile = new StackPane();
        voile.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        Text texteAttente = new Text("EN ATTENTE...");
        texteAttente.setFont(FontUtils.getPolice(25)); texteAttente.setFill(Color.web("#ff0000"));
        voile.getChildren().add(texteAttente); voile.setVisible(false); voile.setOpacity(0);
        return voile;
    }

    public void actualiserMenuBateaux() {
        ScrollPane scroll = (ScrollPane) panneauPlacement.getChildren().get(2);
        zoneSelectionBateaux = new SelectBoard(controleur.getFlotteRestante());
        scroll.setContent(zoneSelectionBateaux);
    }

    public void retirerVaisseauZoneSelection(String nomNavire) { zoneSelectionBateaux.retirerVaisseau(nomNavire); }
    public void rafraichirOcean() { vueOcean.rafraichir(controleur.getEtat().getJoueur1().getGrilleOcean()); }

    // ==========================================
    // GRILLES ET EVENTS
    // ==========================================

    private GrilleUI creerGrilleOcean() {
        GrilleUI grille = new GrilleUI("#00ffff");
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
        GrilleUI radar = new GrilleUI("#ff0000");
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
    // CONTROLEUR
    // ==========================================

    public void afficherModeBataille(boolean aMoi, int idPartie, ModeJeu modeActuel) {
        layoutPrincipal.setLeft(null);
        HBox zoneBataille = new HBox(30, conteneurOcean, conteneurRadar);
        zoneBataille.setAlignment(Pos.CENTER);
        layoutPrincipal.setCenter(zoneBataille);
        layoutPrincipal.setRight(sideBar);

        if (aMoi) activerMonTour(controleur.getEtat().getTourCourant());
        else bloquerTour("ATTENTE ADVERSAIRE...", modeActuel == ModeJeu.MULTI_HOTE ? "ID: " + idPartie : "RÉFLEXION ENNEMIE...");
    }

    public void activerMonTour(int tour) {
        sideBar.setTexteManche(tour);
        sideBar.setPhase("PHASE DE BATAILLE : À VOUS !");
        conteneurRadar.setEffect(new DropShadow(25, Color.web("#ff0000")));
        FadeTransition ft = new FadeTransition(Duration.seconds(0.3), voileAttenteRadar);
        ft.setToValue(0.0); ft.setOnFinished(_ -> voileAttenteRadar.setVisible(false)); ft.play();
    }

    public void bloquerTour(String phase, String message) {
        sideBar.setPhase(phase);
        conteneurRadar.setEffect(null);
        voileAttenteRadar.setVisible(true);
        ((Text) voileAttenteRadar.getChildren().get(0)).setText(message);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), voileAttenteRadar);
        ft.setToValue(1.0); ft.play();
    }

    public void afficherImpactVisuel(int x, int y, ResultatTir res, Vaisseau cible, boolean moiTire) {
        GrilleUI grille = moiTire ? vueRadar : vueOcean;
        if (res == ResultatTir.RATE) {
            grille.colorierCase(x, y, moiTire ? Color.WHITE : Color.LIGHTCYAN);
            sideBar.ajouterLog((moiTire ? "Tir allié" : "Tir ennemi") + " en " + (char)('A' + y) + "-" + (x + 1) + " : Raté", "RATE");
        } else {
            grille.colorierCase(x, y, moiTire ? Color.RED : Color.DARKRED);
            if (res == ResultatTir.TOUCHE) sideBar.ajouterLog(moiTire ? "Cible touchée !" : "Navire allié touché !", "TOUCHE");
            else {
                sideBar.ajouterLog(moiTire ? "BOUM ! " + cible.getNom() + " coulé !" : "DÉSASTRE ! " + cible.getNom() + " coulé !", "ALERTE");
                notificationBox.afficherAlerte(moiTire ? "NAVIRE ENNEMI DÉTRUIT" : "NAVIRE ALLIÉ PERDU", moiTire ? "#ffaa00" : "#ff0000");
            }
        }
    }

    public void notifierAdversaireRejoint() {
        notificationBox.afficherAlerte("L'ADVERSAIRE A REJOINT !", "#00ffff");
    }

    public void afficherEcranFin(boolean victoire) {
        Rectangle voile = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.color(0, 0, 0, 0.8));
        Text t = new Text(victoire ? "VICTOIRE !\nVous avez détruit la flotte !" : "DÉFAITE...\nVotre flotte a été coulée.");
        t.setFont(Font.font("Impact", 40)); t.setFill(victoire ? Color.LIMEGREEN : Color.RED); t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button bM = styleBouton("RETOUR AU MENU", "#00ffff"); bM.setOnAction(_ -> {
            controleur.stopperControleur(); FXGL.getGameScene().clearUINodes(); FXGL.addUINode(new MenuUI());
        });
        Button bQ = styleBouton("QUITTER", "#ff0000"); bQ.setOnAction(_ -> FXGL.getGameController().exit());

        VBox box = new VBox(40, t, new HBox(30, bM, bQ)); box.setAlignment(Pos.CENTER);
        racineVisuelle.getChildren().addAll(voile, box);
    }

    public void restaurerVisuelBataille(EtatJeu etat) {
        rafraichirOcean();
        for(int x=0; x<10; x++) {
            for(int y=0; y<10; y++) {
                if(etat.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] != null)
                    vueRadar.colorierCase(x, y, etat.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] == ResultatTir.RATE ? Color.WHITE : Color.RED);
                if(etat.getJoueur2().getGrilleRadar().getHistoriqueTirs()[x][y] != null)
                    vueOcean.colorierCase(x, y, etat.getJoueur2().getGrilleRadar().getHistoriqueTirs()[x][y] == ResultatTir.RATE ? Color.LIGHTCYAN : Color.DARKRED);
            }
        }
    }
}