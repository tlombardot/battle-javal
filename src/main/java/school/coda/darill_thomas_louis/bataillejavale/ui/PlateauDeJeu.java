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
import school.coda.darill_thomas_louis.bataillejavale.core.event.ResultatTir;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.PartieRepository;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlateauDeJeu {

    private static final String TEXT_FONT = "Impact";

    private boolean tourJoueur;
    private int idPartieCourante = -1;
    private final PartieRepository partieRepository = new PartieRepository();

    private EtatJeu etatJeuBackend;
    private List<Vaisseau> flotteRestante;
    private boolean phaseBataille = false;
    private final Random random = new Random();

    private final StackPane racineVisuelle;
    private final BorderPane layoutPrincipal;

    /**
     * Grille vueOcean pour le placement des bateaux et pour les tirs reçus
     */
    private final GrilleUI vueOcean;

    /**
     * Grille vueRadar pour les tirs envoyés
     */
    private final GrilleUI vueRadar;

    /**
     * Information sur la partie en cours, historique des actions
     */
    private final SideBarUI sideBar;
    private final NotificationUI notificationBox;
    private final GestionnairePlacement gestionnairePlacement;

    private SelectBoard zoneSelectionBateaux;
    private final VBox conteneurOcean;
    private final VBox conteneurRadar;
    private VBox panneauPlacement;
    private Button btnPret;

    private final StackPane voileAttenteRadar;

    public PlateauDeJeu(EtatJeu sauvegarde, int idPartie) {
        this();
        this.etatJeuBackend = sauvegarde;
        this.idPartieCourante = idPartie;

        reparerSauvegarde(etatJeuBackend.getJoueur1(), etatJeuBackend.getJoueur2());
        reparerSauvegarde(etatJeuBackend.getJoueur2(), etatJeuBackend.getJoueur1());

        passerEnModeBataille(false);
        restaurerVisuelBataille();

        sideBar.ajouterLog("Sauvegarde Cloud chargée avec succès (ID: " + idPartie + ")", "INFO");
        sideBar.setTexteManche(etatJeuBackend.getTourCourant());
    }

    public PlateauDeJeu() {
        initialiserDonneesPartie();

        sideBar = new SideBarUI();
        notificationBox = new NotificationUI();
        gestionnairePlacement = new GestionnairePlacement(this);

        vueOcean = creerGrilleOcean();
        vueRadar = creerGrilleRadar(vueOcean);

        voileAttenteRadar = creerVoileAttente();
        StackPane blocRadarEtVoile = new StackPane(vueRadar, voileAttenteRadar);

        conteneurOcean = assemblerConteneurGrille("ZONE ALLIÉE (Océan)", Color.CYAN, vueOcean);
        conteneurRadar = assemblerConteneurGrille("ZONE ENNEMIE (Radar)", Color.RED, blocRadarEtVoile);

        ColorAdjust desaturate = new ColorAdjust();
        desaturate.setBrightness(-0.5);
        conteneurRadar.setEffect(desaturate);

        creerPanneauPlacement();

        Rectangle fondPlateau = creerFondEcran();
        layoutPrincipal = assemblerLayoutPrincipal();

        racineVisuelle = new StackPane();
        racineVisuelle.getChildren().addAll(fondPlateau, layoutPrincipal, notificationBox);

        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        sideBar.ajouterLog("Système initialisé. En attente de déploiement.", "INFO");
        notificationBox.afficherAlerte("DÉPLOIEMENT DE FLOTTE !", "#00ffff");
    }

    // ==========================================
    // GETTERS UTILISÉS PAR LE GESTIONNAIRE
    // ==========================================

    public StackPane getRacineVisuelle() { return racineVisuelle; }
    public EtatJeu getEtatJeuBackend() { return etatJeuBackend; }
    public boolean isPhaseBataille() { return phaseBataille; }
    public List<Vaisseau> getFlotteRestante() { return flotteRestante; }
    public Button getBtnPret() { return btnPret; }

    // ==========================================
    // INITIALISATION ET UI
    // ==========================================

    private void initialiserDonneesPartie() {
        etatJeuBackend = new EtatJeu();
        phaseBataille = false;
        rechargerFlotteRestante();
    }

    public void rechargerFlotteRestante() {
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

    private VBox assemblerConteneurGrille(String titre, Color couleurTexte, Node elementCentral) {
        Text texte = new Text(titre);
        texte.setFont(FontUtils.getPolice(20));
        texte.setFill(couleurTexte);

        VBox conteneur = new VBox(15, texte, elementCentral);
        conteneur.setAlignment(Pos.CENTER);
        return conteneur;
    }

    private StackPane creerVoileAttente() {
        StackPane voile = new StackPane();
        voile.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

        Text texteAttente = new Text("RÉFLEXION ENNEMIE...");
        texteAttente.setFont(FontUtils.getPolice(25));
        texteAttente.setFill(Color.web("#ff0000"));

        voile.getChildren().add(texteAttente);
        voile.setVisible(false);
        voile.setOpacity(0);

        return voile;
    }

    private BorderPane assemblerLayoutPrincipal() {
        BorderPane layout = new BorderPane();
        layout.setPrefWidth(FXGL.getAppWidth());
        layout.setPrefHeight(FXGL.getAppHeight());
        layout.setLeft(panneauPlacement);
        layout.setCenter(conteneurOcean);
        return layout;
    }
    // ... AUTRES METHODES DE PLATEAUDEJEU ...

    private void creerPanneauPlacement() {
        panneauPlacement = new VBox(20); // Espacement ajusté
        panneauPlacement.setAlignment(Pos.TOP_CENTER); // On aligne en haut pour que le titre ne bouge jamais
        panneauPlacement.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-padding: 30 20 30 10; " +
                        "-fx-border-color: #00ffff; " +
                        "-fx-border-width: 0 2px 0 0;" // Haut:0, Droite:2, Bas:0, Gauche:0
        );
        panneauPlacement.setPrefWidth(320);

        // Titre stylisé avec ta police
        Text titre = new Text("DÉPLOIEMENT");
        titre.setFont(FontUtils.getPolice(32));
        titre.setFill(Color.WHITE);
        titre.setEffect(new DropShadow(10, Color.web("#00ffff")));

        Text instructions = new Text("CLIC DROIT : PIVOTER\nGLISSER : PLACER");
        instructions.setFont(FontUtils.getPolice(14));
        instructions.setFill(Color.web("#00ffff", 0.7));
        instructions.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        zoneSelectionBateaux = new SelectBoard(flotteRestante);

        ScrollPane scrollBateaux = new ScrollPane(zoneSelectionBateaux);
        scrollBateaux.setFitToWidth(true);
        scrollBateaux.setPrefHeight(400);
        scrollBateaux.setMinHeight(250);

        scrollBateaux.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollBateaux.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollBateaux.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);

        Button btnAleatoire = styleBouton("PLACEMENT ALÉATOIRE", "#00ffff");
        btnAleatoire.setOnAction(_ -> gestionnairePlacement.placerMesBateauxAleatoirement(vueOcean));

        Button btnVider = styleBouton("VIDER LA GRILLE", "#ffaa00");
        btnVider.setOnAction(_ -> gestionnairePlacement.viderGrille(vueOcean));

        btnPret = styleBouton("DÉMARRER BATAILLE", "#32cd32");
        btnPret.setDisable(true);
        btnPret.setOnAction(_ -> passerEnModeBataille(true));

        panneauPlacement.getChildren().addAll(titre, instructions, scrollBateaux, btnAleatoire, btnVider, btnPret);
    }

    private Button styleBouton(String texte, String couleurHex) {
        Button btn = new Button(texte);
        btn.setFont(FontUtils.getPolice(18));
        btn.setPrefSize(260, 45);

        String styleNormal = "-fx-background-color: #0c121e; -fx-border-color: " + couleurHex + "; -fx-text-fill: " + couleurHex + "; -fx-border-width: 2px; -fx-cursor: hand; -fx-border-radius: 4; -fx-background-radius: 4;";
        String styleHover = "-fx-background-color: " + couleurHex + "; -fx-border-color: " + couleurHex + "; -fx-text-fill: #0c121e; -fx-border-width: 2px; -fx-cursor: hand; -fx-border-radius: 4; -fx-background-radius: 4;";

        btn.setStyle(styleNormal);

        DropShadow glow = new DropShadow(15, Color.web(couleurHex));
        glow.setSpread(0.2);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(styleHover);
            btn.setEffect(glow);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(styleNormal);
            btn.setEffect(null);
        });

        return btn;
    }

    public void actualiserMenuBateaux() {
        javafx.scene.control.ScrollPane scroll = (javafx.scene.control.ScrollPane) panneauPlacement.getChildren().get(2);
        zoneSelectionBateaux = new SelectBoard(flotteRestante);
        scroll.setContent(zoneSelectionBateaux);
    }

    public void retirerVaisseauZoneSelection(String nomNavire) {
        zoneSelectionBateaux.retirerVaisseau(nomNavire);
    }

    // ==========================================
    // CRÉATION DES GRILLES
    // ==========================================

    private GrilleUI creerGrilleOcean() {
        GrilleUI grille = new GrilleUI("#00ffff");
        grille.setListener(new GrilleUI.GrilleListener() {
            @Override public void onCaseLeftClick(int x, int y) {}
            @Override public void onCaseRightClick(int x, int y) {}
            @Override public String onDragStart(int x, int y) {
                return gestionnairePlacement.gererDragStartOcean(grille, x, y);
            }
            @Override public void onDragOver(int x, int y, String nomNavire, boolean estHorizontal) {
                gestionnairePlacement.gererDragOverOcean(grille, x, y, nomNavire, estHorizontal);
            }
            @Override public void onDragExited() {
                if (!phaseBataille) grille.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
            }
            @Override public void onDragDropped(int x, int y, String nomNavire, boolean estHorizontal) {
                gestionnairePlacement.gererDragDroppedOcean(grille, x, y, nomNavire, estHorizontal);
            }
        });
        return grille;
    }

    private GrilleUI creerGrilleRadar(GrilleUI oceanRef) {
        GrilleUI radar = new GrilleUI("#ff0000");
        radar.setListener(new GrilleUI.GrilleListener() {
            @Override public void onCaseLeftClick(int x, int y) {
                gererTirJoueur(radar, oceanRef, x, y);
            }
            @Override public void onCaseRightClick(int x, int y) {}
            @Override public void onDragOver(int x, int y, String nomNavire, boolean estHorizontal) {}
            @Override public void onDragDropped(int x, int y, String nomNavire, boolean estHorizontal) {}
            @Override public void onDragExited() {}
            @Override public String onDragStart(int x, int y) { return null; }
        });
        return radar;
    }

    // ==========================================
    // LOGIQUE DE BATAILLE
    // ==========================================

    private void passerEnModeBataille(boolean estNouvellePartie) {
        if (estNouvellePartie) {
            placerBateauxCPU();
            idPartieCourante = partieRepository.creerNouvellePartie(etatJeuBackend);
            sideBar.ajouterLog("Sauvegarde Cloud OK (ID: " + idPartieCourante + ")", "INFO");
            notificationBox.afficherAlerte("BATAILLE IMMINENTE !", "#ff3333");
        }

        phaseBataille = true;
        tourJoueur = true;

        DropShadow neonGlowRadar = new DropShadow(25, Color.web("#ff0000"));
        conteneurRadar.setEffect(neonGlowRadar);

        layoutPrincipal.setLeft(null);

        HBox zoneBataille = new HBox(30, conteneurOcean, conteneurRadar);
        zoneBataille.setAlignment(Pos.CENTER);

        layoutPrincipal.setCenter(zoneBataille);
        layoutPrincipal.setRight(sideBar);

        sideBar.setPhase("PHASE DE BATAILLE : À VOUS !");
        sideBar.ajouterLog("Systèmes d'armement en ligne.", "ALERTE");
    }

    private void gererTirJoueur(GrilleUI radar, GrilleUI oceanRef, int x, int y) {
        if (!phaseBataille) {
            return;
        }

        if(!tourJoueur){return;}

        if (etatJeuBackend.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] != null) return;

        Vaisseau cibleAdverse = etatJeuBackend.getJoueur2().getGrilleOcean().getVaisseauAt(x, y);
        ResultatTir resultat = etatJeuBackend.getJoueur2().getGrilleOcean().recevoirTir(x, y);
        etatJeuBackend.getJoueur1().getGrilleRadar().enregistrerTir(x, y, resultat);

        afficherResultatTirJoueur(radar, x, y, resultat, cibleAdverse);
        partieRepository.mettreAJourPartie(idPartieCourante, etatJeuBackend);

        verifierFinDePartie();
        if (!phaseBataille) return;

        sideBar.setPhase("TOUR DU CPU...");
        tourJoueur = false;

        conteneurRadar.setEffect(null);
        voileAttenteRadar.setVisible(true);
        FadeTransition fadeOuverture = new FadeTransition(Duration.seconds(0.2), voileAttenteRadar);
        fadeOuverture.setToValue(1.0);
        fadeOuverture.play();

        FXGL.getGameTimer().runOnceAfter(() -> {
            riposteDuCPU(oceanRef);
            etatJeuBackend.setTourCourant(etatJeuBackend.getTourCourant() + 1);

            sideBar.setTexteManche(etatJeuBackend.getTourCourant());
            sideBar.setPhase("PHASE DE COMBAT : À VOUS !");

            verifierFinDePartie();

            if(phaseBataille){
                tourJoueur = true;
                DropShadow neonGlowRadar = new DropShadow(25, Color.web("#ff0000"));
                conteneurRadar.setEffect(neonGlowRadar);

                FadeTransition fadeFermeture = new FadeTransition(Duration.seconds(0.3), voileAttenteRadar);
                fadeFermeture.setToValue(0.0);
                fadeFermeture.setOnFinished(e -> voileAttenteRadar.setVisible(false));
                fadeFermeture.play();
            }

        }, Duration.seconds(1.0));
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
            } else {
                sideBar.ajouterLog("DÉSASTRE ! " + notreCible.getNom() + " allié coulé !", "ALERTE");
                notificationBox.afficherAlerte("NAVIRE ALLIÉ PERDU", "#ff0000");
            }
        }

        partieRepository.mettreAJourPartie(idPartieCourante, etatJeuBackend);
    }

    private void verifierFinDePartie() {
        if (etatJeuBackend.getJoueur2().aPerdu()) {
            phaseBataille = false;
            partieRepository.terminerPartie(idPartieCourante, "TERMINEE_VICTOIRE");
            afficherEcranFin("VICTOIRE !\nVous avez détruit la flotte ennemie !", Color.LIMEGREEN);
        } else if (etatJeuBackend.getJoueur1().aPerdu()) {
            phaseBataille = false;
            partieRepository.terminerPartie(idPartieCourante, "TERMINEE_DEFAITE");
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

    private void restaurerVisuelBataille() {
        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());

        for(int x=0; x<10; x++) {
            for(int y=0; y<10; y++) {
                ResultatTir monTir = etatJeuBackend.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y];
                if(monTir != null) {
                    if (monTir == ResultatTir.RATE) vueRadar.colorierCase(x, y, Color.WHITE);
                    else vueRadar.colorierCase(x, y, Color.RED);
                }

                ResultatTir tirCpu = etatJeuBackend.getJoueur2().getGrilleRadar().getHistoriqueTirs()[x][y];
                if(tirCpu != null) {
                    if (tirCpu == ResultatTir.RATE) vueOcean.colorierCase(x, y, Color.LIGHTCYAN);
                    else vueOcean.colorierCase(x, y, Color.DARKRED);
                }
            }
        }
    }

    private void reparerSauvegarde(school.coda.darill_thomas_louis.bataillejavale.core.model.JoueurPlay defenseur, school.coda.darill_thomas_louis.bataillejavale.core.model.JoueurPlay attaquant) {
        Vaisseau[][] plateau = defenseur.getGrilleOcean().getPlateau();
        List<Vaisseau> flotte = defenseur.getFlotte();

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (plateau[x][y] != null) {
                    for (Vaisseau vraiVaisseau : flotte) {
                        if (vraiVaisseau.getNom().equals(plateau[x][y].getNom())) {
                            plateau[x][y] = vraiVaisseau;
                            break;
                        }
                    }
                }
            }
        }

        for (Vaisseau v : flotte) {
            v.setCasesTouchees(0);
        }

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