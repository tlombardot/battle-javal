package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
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

    public enum ModeJeu { SOLO, MULTI_HOTE, MULTI_INVITE, REPLAY }
    private ModeJeu modeActuel = ModeJeu.SOLO;
    private TimerAction pollingTimer;

    private static final String TEXT_FONT = "Impact";

    private boolean tourJoueur;
    private int idPartieCourante = -1;
    private final PartieRepository partieRepository = new PartieRepository();

    private EtatJeu etatJeuBackend;
    private List<Vaisseau> flotteRestante;
    private boolean phaseBataille = false;
    private final Random random = new Random();

    private StackPane racineVisuelle;
    private BorderPane layoutPrincipal;

    private GrilleUI vueOcean;
    private GrilleUI vueRadar;
    private SideBarUI sideBar;
    private NotificationUI notificationBox;
    private GestionnairePlacement gestionnairePlacement;

    private SelectBoard zoneSelectionBateaux;
    private VBox conteneurOcean;
    private VBox conteneurRadar;
    private VBox panneauPlacement;
    private Button btnPret;

    private StackPane voileAttenteRadar;

    // Pour une NOUVELLE partie (Solo ou Hôte Multi)
    public PlateauDeJeu(ModeJeu mode) {
        this.modeActuel = mode;
        initialiserDonneesPartie();
        this.racineVisuelle = initialiserUI();
    }

    // Pour CHARGER une partie (Replay ou Invité Multi)
    public PlateauDeJeu(ModeJeu mode, EtatJeu sauvegarde, int idPartie) {
        this.modeActuel = mode;
        this.idPartieCourante = idPartie;

        if (mode == ModeJeu.MULTI_INVITE) {
            school.coda.darill_thomas_louis.bataillejavale.core.model.JoueurPlay hote = sauvegarde.getJoueur1();
            sauvegarde.setJoueur1(sauvegarde.getJoueur2());
            sauvegarde.setJoueur2(hote);
        }

        this.etatJeuBackend = sauvegarde;
        rechargerFlotteRestante();
        this.racineVisuelle = initialiserUI();

        if (mode == ModeJeu.REPLAY) {
            reparerSauvegarde(etatJeuBackend.getJoueur1(), etatJeuBackend.getJoueur2());
            reparerSauvegarde(etatJeuBackend.getJoueur2(), etatJeuBackend.getJoueur1());
            passerEnModeBataille(false);
            restaurerVisuelBataille();
            sideBar.ajouterLog("Sauvegarde Cloud chargée avec succès (ID: " + idPartie + ")", "INFO");
            sideBar.setTexteManche(etatJeuBackend.getTourCourant());
        }
    }

    // Constructeur par défaut (Garde ta compatibilité)
    public PlateauDeJeu() {
        this(ModeJeu.SOLO);
    }

    private StackPane initialiserUI() {
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

        StackPane racine = new StackPane();
        racine.getChildren().addAll(fondPlateau, layoutPrincipal, notificationBox);

        vueOcean.rafraichir(etatJeuBackend.getJoueur1().getGrilleOcean());
        sideBar.ajouterLog("Système initialisé. Mode : " + modeActuel, "INFO");
        notificationBox.afficherAlerte("DÉPLOIEMENT DE FLOTTE !", "#00ffff");

        return racine;
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

        Text texteAttente = new Text("EN ATTENTE...");
        texteAttente.setFont(FontUtils.getPolice(25));
        texteAttente.setFill(Color.web("#ff0000"));
        texteAttente.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

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

    private void creerPanneauPlacement() {
        panneauPlacement = new VBox(20);
        panneauPlacement.setAlignment(Pos.TOP_CENTER);
        panneauPlacement.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-padding: 30 20 30 10; " +
                        "-fx-border-color: #00ffff; " +
                        "-fx-border-width: 0 2px 0 0;"
        );
        panneauPlacement.setPrefWidth(320);

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
    // LOGIQUE DE BATAILLE ET RÉSEAU MULTIJOUEUR
    // ==========================================

    // Remet les joueurs dans le bon ordre pour la base de données
    private EtatJeu getEtatJeuPourDB() {
        if (modeActuel == ModeJeu.MULTI_INVITE) {
            EtatJeu dbEtat = new EtatJeu();
            dbEtat.setJoueur1(etatJeuBackend.getJoueur2());
            dbEtat.setJoueur2(etatJeuBackend.getJoueur1());
            dbEtat.setTourCourant(etatJeuBackend.getTourCourant());
            return dbEtat;
        }
        return etatJeuBackend;
    }

    private void passerEnModeBataille(boolean estNouvellePartie) {
        if (modeActuel == ModeJeu.SOLO) {
            if (estNouvellePartie) {
                placerBateauxCPU();
                idPartieCourante = partieRepository.creerNouvellePartie(etatJeuBackend);
                sideBar.ajouterLog("Partie Solo (ID: " + idPartieCourante + ")", "INFO");
            }
            demarrerVisuelBataille(true);

        } else if (modeActuel == ModeJeu.MULTI_HOTE) {
            idPartieCourante = partieRepository.hebergerPartieMulti(getEtatJeuPourDB());
            sideBar.ajouterLog("SALON CRÉÉ : ID " + idPartieCourante, "ALERTE");
            demarrerVisuelBataille(false); // On bloque l'écran en attendant le joueur 2
            demarrerPollingAttenteJoueur2();

        } else if (modeActuel == ModeJeu.MULTI_INVITE) {
            partieRepository.demarrerPartieMulti(idPartieCourante, getEtatJeuPourDB());
            sideBar.ajouterLog("SALON REJOINT : ID " + idPartieCourante, "INFO");
            demarrerVisuelBataille(false); // L'hôte tire en premier, on attend !
            demarrerPollingTourAdversaire();

        } else if (modeActuel == ModeJeu.REPLAY) {
            demarrerVisuelBataille(tourJoueur);
        }
    }

    private void demarrerVisuelBataille(boolean aMoiDeJouer) {
        phaseBataille = true;
        tourJoueur = aMoiDeJouer;

        layoutPrincipal.setLeft(null);
        HBox zoneBataille = new HBox(30, conteneurOcean, conteneurRadar);
        zoneBataille.setAlignment(Pos.CENTER);
        layoutPrincipal.setCenter(zoneBataille);
        layoutPrincipal.setRight(sideBar);

        if (aMoiDeJouer) {
            conteneurRadar.setEffect(new DropShadow(25, Color.web("#ff0000")));
            sideBar.setPhase("PHASE DE BATAILLE : À VOUS !");
            notificationBox.afficherAlerte("BATAILLE IMMINENTE !", "#ff3333");
            voileAttenteRadar.setVisible(false);
        } else {
            conteneurRadar.setEffect(null);
            sideBar.setPhase("ATTENTE DE L'ADVERSAIRE...");
            voileAttenteRadar.setVisible(true);
            voileAttenteRadar.setOpacity(1.0);
            ((Text) voileAttenteRadar.getChildren().get(0)).setText(
                    modeActuel == ModeJeu.MULTI_HOTE ? "DONNEZ L'ID " + idPartieCourante + "\nÀ VOTRE ADVERSAIRE" : "RÉFLEXION ENNEMIE..."
            );
        }
    }

    private void gererTirJoueur(GrilleUI radar, GrilleUI oceanRef, int x, int y) {
        if (!phaseBataille) return;
        if (!tourJoueur) return;

        if (etatJeuBackend.getJoueur1().getGrilleRadar().getHistoriqueTirs()[x][y] != null) return;

        Vaisseau cibleAdverse = etatJeuBackend.getJoueur2().getGrilleOcean().getVaisseauAt(x, y);
        ResultatTir resultat = etatJeuBackend.getJoueur2().getGrilleOcean().recevoirTir(x, y);
        etatJeuBackend.getJoueur1().getGrilleRadar().enregistrerTir(x, y, resultat);

        afficherResultatTirJoueur(radar, x, y, resultat, cibleAdverse);

        // INCREMENT DU TOUR ET SAUVEGARDE DB
        etatJeuBackend.setTourCourant(etatJeuBackend.getTourCourant() + 1);
        partieRepository.mettreAJourPartie(idPartieCourante, getEtatJeuPourDB());

        verifierFinDePartie();
        if (!phaseBataille) return;

        // ON PASSE LA MAIN
        tourJoueur = false;
        sideBar.setPhase("TOUR ADVERSE...");

        conteneurRadar.setEffect(null);
        voileAttenteRadar.setVisible(true);
        FadeTransition fadeOuverture = new FadeTransition(Duration.seconds(0.2), voileAttenteRadar);
        fadeOuverture.setToValue(1.0);
        fadeOuverture.play();
        ((Text) voileAttenteRadar.getChildren().get(0)).setText("RÉFLEXION ENNEMIE...");

        if (modeActuel == ModeJeu.SOLO) {
            FXGL.getGameTimer().runOnceAfter(() -> {
                riposteDuCPU(oceanRef);
                etatJeuBackend.setTourCourant(etatJeuBackend.getTourCourant() + 1);

                sideBar.setTexteManche(etatJeuBackend.getTourCourant());
                sideBar.setPhase("PHASE DE COMBAT : À VOUS !");

                verifierFinDePartie();

                if(phaseBataille){
                    tourJoueur = true;
                    conteneurRadar.setEffect(new DropShadow(25, Color.web("#ff0000")));

                    FadeTransition fadeFermeture = new FadeTransition(Duration.seconds(0.3), voileAttenteRadar);
                    fadeFermeture.setToValue(0.0);
                    fadeFermeture.setOnFinished(_ -> voileAttenteRadar.setVisible(false));
                    fadeFermeture.play();
                }

            }, Duration.seconds(1.0));
        } else {
            // MULTI : On attend que l'adversaire joue
            demarrerPollingTourAdversaire();
        }
    }

    // --- LE MOTEUR RÉSEAU (POLLING) ---

    private void demarrerPollingAttenteJoueur2() {
        pollingTimer = FXGL.getGameTimer().runAtInterval(() -> {
            String statut = partieRepository.getStatutPartie(idPartieCourante);
            if ("EN_COURS".equals(statut)) {
                pollingTimer.expire();

                EtatJeu etatEnBase = partieRepository.chargerPartieActiveOuTerminee(idPartieCourante);
                if (etatEnBase != null) {
                    etatJeuBackend = etatEnBase;
                    reparerSauvegarde(etatJeuBackend.getJoueur1(), etatJeuBackend.getJoueur2());
                    reparerSauvegarde(etatJeuBackend.getJoueur2(), etatJeuBackend.getJoueur1());
                }

                tourJoueur = true;
                conteneurRadar.setEffect(new DropShadow(25, Color.web("#ff0000")));
                sideBar.setPhase("PHASE DE BATAILLE : À VOUS !");
                notificationBox.afficherAlerte("L'ADVERSAIRE A REJOINT !", "#00ffff");

                FadeTransition fadeFermeture = new FadeTransition(Duration.seconds(0.3), voileAttenteRadar);
                fadeFermeture.setToValue(0.0);
                fadeFermeture.setOnFinished(_ -> voileAttenteRadar.setVisible(false));
                fadeFermeture.play();
            }
        }, Duration.seconds(2.0));
    }

    private void demarrerPollingTourAdversaire() {
        pollingTimer = FXGL.getGameTimer().runAtInterval(() -> {
            EtatJeu etatEnBase = partieRepository.chargerPartieActiveOuTerminee(idPartieCourante);

            if (etatEnBase != null && etatEnBase.getTourCourant() > etatJeuBackend.getTourCourant()) {
                pollingTimer.expire();

                // Si on est l'invité, on inverse les joueurs pour notre vue
                if (modeActuel == ModeJeu.MULTI_INVITE) {
                    var temp = etatEnBase.getJoueur1();
                    etatEnBase.setJoueur1(etatEnBase.getJoueur2());
                    etatEnBase.setJoueur2(temp);
                }

                etatJeuBackend = etatEnBase;
                reparerSauvegarde(etatJeuBackend.getJoueur1(), etatJeuBackend.getJoueur2());
                reparerSauvegarde(etatJeuBackend.getJoueur2(), etatJeuBackend.getJoueur1());

                restaurerVisuelBataille();
                verifierFinDePartie();

                if (phaseBataille) {
                    tourJoueur = true;
                    sideBar.setTexteManche(etatJeuBackend.getTourCourant());
                    sideBar.setPhase("PHASE DE BATAILLE : À VOUS !");
                    notificationBox.afficherAlerte("C'EST VOTRE TOUR !", "#00ffff");
                    conteneurRadar.setEffect(new DropShadow(25, Color.web("#ff0000")));

                    FadeTransition fadeFermeture = new FadeTransition(Duration.seconds(0.3), voileAttenteRadar);
                    fadeFermeture.setToValue(0.0);
                    fadeFermeture.setOnFinished(_ -> voileAttenteRadar.setVisible(false));
                    fadeFermeture.play();
                }
            }
        }, Duration.seconds(2.0));
    }

    // ==========================================
    // LOGIQUE DE BASE
    // ==========================================

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
            if (pollingTimer != null) pollingTimer.expire();
            partieRepository.terminerPartie(idPartieCourante, "TERMINEE_VICTOIRE");
            afficherEcranFin("VICTOIRE !\nVous avez détruit la flotte ennemie !", Color.LIMEGREEN);
        } else if (etatJeuBackend.getJoueur1().aPerdu()) {
            phaseBataille = false;
            if (pollingTimer != null) pollingTimer.expire();
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
        Button btnMenu = styleBouton("RETOUR AU MENU", "#00ffff");
        btnMenu.setOnAction(_ -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.addUINode(new MenuUI());
        });
        Button btnQuitter = styleBouton("QUITTER", "#ff0000");
        btnQuitter.setOnAction(_ -> FXGL.getGameController().exit());
        HBox boutonsBox = new HBox(30, btnMenu, btnQuitter);
        boutonsBox.setAlignment(Pos.CENTER);
        VBox ecranFin = new VBox(40, texteFin, boutonsBox);
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