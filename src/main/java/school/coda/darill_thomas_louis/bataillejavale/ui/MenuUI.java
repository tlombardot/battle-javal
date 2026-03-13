package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Session;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.PartieRepository;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuUI extends Pane {

    private final List<TechButton> menuButtons = new ArrayList<>();
    private int currentSelection = 0;
    private boolean isActive = true;

    public MenuUI() {
        buildBackground();
        buildLogo();
        buildButtons();
        buildCredits();
        updateSelection();
    }

    private void buildBackground() {
        try {
            String path = Objects.requireNonNull(getClass().getResource("/assets/textures/naval_ocean.gif")).toExternalForm();
            Image gif = new Image(path);
            ImageView background = new ImageView(gif);
            background.setFitWidth(1280);
            background.setFitHeight(720);
            getChildren().add(background);
        } catch (NullPointerException e) {
            System.out.println("Fichier interface introuvable : " + e.getMessage());
            Rectangle fond = new Rectangle(1280, 720, Color.web("#0a0f18"));
            getChildren().add(fond);
        }
    }

    private void buildLogo() {
        try {
            String pathLogo = Objects.requireNonNull(getClass().getResource("/assets/textures/main_logo.png")).toExternalForm();
            ImageView logoTitle = new ImageView(new Image(pathLogo));
            logoTitle.setFitWidth(520);
            logoTitle.setPreserveRatio(true);
            logoTitle.setTranslateX(40);
            logoTitle.setTranslateY(5);
            getChildren().add(logoTitle);
        } catch (Exception _) {
            System.out.println("Logo non trouvé.");
        }
    }

    private void buildButtons() {
        VBox completeMenuBox = new VBox(20);
        completeMenuBox.setTranslateX(100);
        completeMenuBox.setTranslateY(320);

        menuButtons.add(new TechButton("START GAME_", this::lancerEcranLoading));
        menuButtons.add(new TechButton("LOAD LAST GAME_", () -> {
            FXGL.getDialogService().showInputBox("Entrez l'ID de la partie à reprendre :", input -> {
                try {
                    int id = Integer.parseInt(input);
                    PartieRepository repo = new PartieRepository();
                    EtatJeu sauvegarde = repo.chargerPartie(id);

                    if (sauvegarde != null) {
                        FXGL.getGameScene().clearUINodes();
                        PlateauDeJeu plateau = new PlateauDeJeu(sauvegarde, id);
                        FXGL.addUINode(plateau.getRacineVisuelle());
                    } else {
                        FXGL.getDialogService().showMessageBox("Partie introuvable ou déjà terminée !");
                    }
                } catch (NumberFormatException _) {
                    FXGL.getDialogService().showMessageBox("Veuillez entrer un numéro valide !");
                }
            });
        }));

        menuButtons.add(new TechButton("HOST MULTIPLAYER_", () -> {
            System.out.println("Création d'un salon multijoueur...");
        }));

        menuButtons.add(new TechButton("JOIN MULTIPLAYER_", () -> {
            FXGL.getDialogService().showInputBox("Entrez l'ID du Salon :", input -> {
                try {
                    int idSalon = Integer.parseInt(input);
                    PartieRepository repo = new PartieRepository();
                    EtatJeu salon = repo.chargerSalonAttente(idSalon);

                    if (salon != null) {
                        System.out.println("Salon trouvé ! À toi de placer tes bateaux.");
                    } else {
                        FXGL.getDialogService().showMessageBox("Salon introuvable ou déjà lancé !");
                    }
                } catch (NumberFormatException _) {
                    FXGL.getDialogService().showMessageBox("ID invalide !");
                }
            });
        }));

        menuButtons.add(new TechButton("SETTINGS_", () -> System.out.println("Ouverture paramètres.")));
        menuButtons.add(new TechButton("EXIT_", () -> FXGL.getGameController().exit()));

        for (TechButton btn : menuButtons) {
            completeMenuBox.getChildren().add(btn.visual);
        }
        getChildren().add(completeMenuBox);
    }

    private void buildCredits() {
        VBox creditsBox = new VBox(5);
        creditsBox.setAlignment(Pos.CENTER_RIGHT);
        creditsBox.setTranslateX(800);
        creditsBox.setTranslateY(660);

        Text creditsText = new Text("CREATED BY KING_DARILL_ CYBER080THOMAS_ JAVA_LOUIS_");
        creditsText.setFont(FontUtils.getPolice(14));
        creditsText.setFill(Color.web("#cccccc"));
        creditsText.setEffect(new DropShadow(5, Color.BLACK));

        creditsBox.getChildren().add(creditsText);
        getChildren().add(creditsBox);

        StackPane profilBox = new StackPane();
        profilBox.setTranslateX(1020);
        profilBox.setTranslateY(30);

        Rectangle fondProfil = new Rectangle(230, 45, Color.web("#0a0f18", 0.15));
        fondProfil.setArcWidth(5);
        fondProfil.setArcHeight(5);
        fondProfil.setStroke(Color.web("#00ffff", 0.6));
        fondProfil.setStrokeWidth(2);
        fondProfil.setStrokeType(StrokeType.INSIDE);
        fondProfil.setEffect(new DropShadow(15, Color.web("#00ffff", 0.3)));

        Text txtProfil = new Text("PROFIL : " + Session.pseudo);
        txtProfil.setFont(FontUtils.getPolice(20));
        txtProfil.setFill(Color.web("#ffffff"));
        txtProfil.setEffect(new DropShadow(10, Color.web("#00ffff")));

        profilBox.getChildren().addAll(fondProfil, txtProfil);
        getChildren().add(profilBox);
    }

    public void selectUp() {
        if (!isActive) return;
        currentSelection = (currentSelection == 0) ? (menuButtons.size() - 1) : (currentSelection - 1);
        updateSelection();
    }

    public void selectDown() {
        if (!isActive) return;
        currentSelection = (currentSelection == menuButtons.size() - 1) ? 0 : (currentSelection + 1);
        updateSelection();
    }

    public void triggerCurrentSelection() {
        if (!isActive) return;
        menuButtons.get(currentSelection).action.run();
    }

    private void updateSelection() {
        for (int i = 0; i < menuButtons.size(); i++) {
            menuButtons.get(i).setActive(i == currentSelection);
        }
    }

    private void lancerEcranLoading() {
        isActive = false;
        getChildren().clear();

        Rectangle blackScreen = new Rectangle(1280, 720, Color.web("#05080c"));
        getChildren().add(blackScreen);

        StackPane loadingPane = new StackPane();
        loadingPane.setPrefSize(1280, 720);

        Text loadingText = new Text("ESTABLISHING SATELLITE CONNECTIONS...");
        loadingText.setFont(FontUtils.getPolice(28));
        loadingText.setFill(Color.web("#00ffff"));
        loadingText.setEffect(new DropShadow(20, Color.web("#00ffff", 0.6)));

        FadeTransition blink = new FadeTransition(Duration.seconds(0.6), loadingText);
        blink.setFromValue(1.0);
        blink.setToValue(0.3);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();

        loadingPane.getChildren().add(loadingText);
        StackPane.setAlignment(loadingText, Pos.BOTTOM_CENTER);
        loadingText.setTranslateY(-50);

        getChildren().add(loadingPane);

        FXGL.getGameTimer().runOnceAfter(() -> {
            FXGL.getGameScene().clearUINodes();
            System.out.println("Lancement de la partie.");
            PlateauDeJeu plateau = new PlateauDeJeu();
            FXGL.addUINode(plateau.getRacineVisuelle());
        }, Duration.seconds(3.0));
    }

    private class TechButton {
        StackPane visual;
        Polygon techBand;
        Text textNode;
        Runnable action;
        FadeTransition pulseAnimation;

        public TechButton(String text, Runnable action) {
            this.action = action;
            visual = new StackPane();
            visual.setAlignment(Pos.CENTER_LEFT);

            techBand = new Polygon(0, 0, 420, 0, 390, 45, 0, 45);

            Stop[] stops = new Stop[] {
                    new Stop(0, Color.web("#00ffff", 0.15)),
                    new Stop(1, Color.web("#00ffff", 0.45))
            };
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            techBand.setFill(gradient);

            DropShadow glow = new DropShadow(30, Color.web("#00ffff"));
            glow.setSpread(0.2);
            techBand.setEffect(glow);
            techBand.setVisible(false);

            pulseAnimation = new FadeTransition(Duration.seconds(0.8), techBand);
            pulseAnimation.setFromValue(0.5);
            pulseAnimation.setToValue(1.0);
            pulseAnimation.setCycleCount(Animation.INDEFINITE);
            pulseAnimation.setAutoReverse(true);

            textNode = new Text(text);
            textNode.setFont(FontUtils.getPolice(24));
            textNode.setFill(Color.WHITE);
            textNode.setTranslateX(20);

            visual.getChildren().addAll(techBand, textNode);

            visual.setOnMouseEntered(_ -> {
                if (isActive) {
                    currentSelection = menuButtons.indexOf(this);
                    updateSelection();
                }
            });

            visual.setOnMouseClicked(_ -> {
                if (isActive) action.run();
            });
        }

        public void setActive(boolean active) {
            techBand.setVisible(active);
            textNode.setFill(active ? Color.WHITE : Color.web("#cccccc"));

            if (active) {
                textNode.setTranslateX(40);
                textNode.setEffect(new DropShadow(10, Color.BLACK));
                pulseAnimation.play();
            } else {
                textNode.setTranslateX(20);
                textNode.setEffect(null);
                pulseAnimation.stop();
            }
        }
    }
}