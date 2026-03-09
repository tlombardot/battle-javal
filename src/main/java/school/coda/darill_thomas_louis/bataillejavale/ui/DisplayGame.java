package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.CreateDB;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class DisplayGame extends GameApplication {

    private final List<TechButton> menuButtons = new ArrayList<>();
    private int currentSelection = 0;
    private boolean inMenu = true;

    private EtatJeu etatJeuBackend;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);
        gameSettings.setTitle("Javale-Battle");
        gameSettings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Up") {
            @Override
            protected void onActionBegin() {
                if(!inMenu) return;
                currentSelection = (currentSelection == 0) ? (menuButtons.size() - 1) : (currentSelection - 1);
                updateSelection();
            }
        }, KeyCode.UP);

        FXGL.getInput().addAction(new UserAction("Down") {
            @Override
            protected void onActionBegin() {
                if(!inMenu) return;
                currentSelection = (currentSelection == menuButtons.size() - 1) ? 0 : (currentSelection + 1);
                updateSelection();
            }
        }, KeyCode.DOWN);

        FXGL.getInput().addAction(new UserAction("Select") {
            @Override
            protected void onActionBegin() {
                if(!inMenu) return;
                menuButtons.get(currentSelection).action.run();
            }
        }, KeyCode.ENTER);
    }

    @Override
    protected void initUI() {
        getGameScene().getRoot().setCursor(Cursor.DEFAULT);

        try {
            String path = getClass().getResource("/assets/textures/naval_ocean.gif").toExternalForm();
            Image gif = new javafx.scene.image.Image(path);
            ImageView background = new javafx.scene.image.ImageView(gif);

            background.setFitWidth(1280);
            background.setFitHeight(720);
            FXGL.addUINode(background);

            IO.println("SUCCÈS : Le GIF est chargé !");
        } catch (NullPointerException e) {

            IO.println("ERREUR CRITIQUE : Fichier introuvable à l'adresse /assets/textures/naval_ocean.gif");
            Rectangle fond = new Rectangle(1280, 720, javafx.scene.paint.Color.web("#0a0f18"));
            FXGL.addUINode(fond);

        } catch (Exception e) {
            IO.println("AUTRE ERREUR lors du chargement de l'image : " + e.getMessage());
        }

        Text title = new Text("JAVALE\nBATTLE");
        title.setFont(Font.font("Impact", 80));
        title.setFill(Color.WHITE);
        title.setTranslateX(100);
        title.setTranslateY(100);
        FXGL.addUINode(title);

        VBox completeMenuBox = new VBox(20);
        completeMenuBox.setTranslateX(100);
        completeMenuBox.setTranslateY(350);

        menuButtons.add(new TechButton("Start Game", this::lancerEcranLoading));
        menuButtons.add(new TechButton("Multiparty game", () -> IO.println("Mode en développement.")));
        menuButtons.add(new TechButton("Settings", () -> IO.println("Ouverture paramètres.")));
        menuButtons.add(new TechButton("Exit", () -> FXGL.getGameController().exit()));

        for(TechButton btn : menuButtons){
            completeMenuBox.getChildren().add(btn.visual);
        }

        FXGL.addUINode(completeMenuBox);

        Text credits = new Text("Created by KING_Darill | CYBER080Thomas | SMART_Louis");
        credits.setFont(Font.font("Arial", 12));
        credits.setFill(Color.GRAY);
        credits.setTranslateX(100);
        credits.setTranslateY(680);
        FXGL.addUINode(credits);

        updateSelection();
    }

    private void updateSelection() {
        for(int i = 0; i < menuButtons.size(); i++){
            menuButtons.get(i).setActive(i == currentSelection);
        }
    }

    private void lancerEcranLoading() {
        inMenu = false;
        FXGL.getGameScene().clearUINodes();

        Rectangle blackScreen = new Rectangle(1280, 720, Color.BLACK);

        FXGL.addUINode(blackScreen);

        /*
        try {
            ImageView techGif = new ImageView(FXGL.image("loading_tech.gif"));
            techGif.setTranslateX(400);
            techGif.setTranslateY(200);
            FXGL.addUINode(techGif);
        } catch (Exception e) {}
        */

        Text loadingText = new Text("ESTABLISHING CONTROL BOARD SATELLITE CONNECTIONS...");
        loadingText.setFont(Font.font("Consolas", 24));
        loadingText.setFill(Color.web("#00ffff"));
        loadingText.setTranslateX(100);
        loadingText.setTranslateY(650);

        FadeTransition blink = new FadeTransition(javafx.util.Duration.seconds(0.5), loadingText);
        blink.setFromValue(1.0);
        blink.setToValue(0.2);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();

        FXGL.addUINode(loadingText);

        FXGL.getGameTimer().runOnceAfter(() -> {
            FXGL.getGameScene().clearUINodes();
            IO.println("Lancement de la partie.");
            demarrerPhasePlacement();
        }, Duration.seconds(3.0));
    }

    private class TechButton {
        StackPane visual;
        Polygon techBand;
        Text textNode;
        Runnable action;

        public TechButton(String text, Runnable action) {
            this.action = action;
            visual = new StackPane();
            visual.setAlignment(Pos.CENTER_LEFT);

            techBand = new Polygon(
                    0, 0,
                    400, 0,
                    370, 40,
                    0, 40
            );

            Stop[] stops = new Stop[] {
                    new Stop(0, Color.web("#001a1a", 0.7)),
                    new Stop(1, Color.web("#00e6e6", 0.9))
            };
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            techBand.setFill(gradient);

            DropShadow glow = new DropShadow(15, Color.web("#00e6e6"));
            techBand.setEffect(glow);
            techBand.setVisible(false);

            textNode = new Text(text);
            textNode.setFont(Font.font("Times", 30));
            textNode.setFill(Color.web("#a0a0a0"));
            textNode.setTranslateX(20);

            visual.getChildren().addAll(techBand, textNode);

            visual.setOnMouseEntered(_ -> {
                if (inMenu) {
                    currentSelection = menuButtons.indexOf(this);
                    updateSelection();
                }
            });
            visual.setOnMouseClicked(_ -> {
                if (inMenu) action.run();
            });
        }

        public void setActive(boolean active) {
            techBand.setVisible(active);
            textNode.setFill(active ? Color.WHITE : Color.web("#a0a0a0"));
            if (active) {
                textNode.setTranslateX(35);
            } else {
                textNode.setTranslateX(20);
            }
        }
    }

    private void demarrerPhasePlacement() {
        etatJeuBackend = new EtatJeu();

        GrilleUI vueOcean = new GrilleUI();

        vueOcean.setOnCaseClicked((x, y) -> {
            IO.println("Clic sur l'Océan aux coordonnées : " + x + ", " + y);

            Vaisseau navire = new Vaisseau("Patrouilleur", 2);
            boolean success = etatJeuBackend.getOceanJoueur1().placerVaisseau(navire, x, y, true);

            if (success) {
                for (int i = 0; i < navire.getTaille(); i++) {
                    vueOcean.colorierCase(x + i, y, Color.DARKGRAY);
                }
            } else {
                IO.println("Placement invalide (Chevauchement ou hors grille) !");
            }
        });


        GrilleUI vueRadar = new GrilleUI();
        vueRadar.setOnCaseClicked((x, y) -> IO.println("Clic sur le Radar aux coordonnées : " + x + ", " + y));

        VBox conteneurOcean = new VBox(10, new Text("Grille Océan (Mes bateaux)"), vueOcean);
        conteneurOcean.setAlignment(Pos.CENTER);

        VBox conteneurRadar = new VBox(10, new Text("Grille Radar (Mes tirs)"), vueRadar);
        conteneurRadar.setAlignment(Pos.CENTER);

        HBox plateauDeJeu = new HBox(50, conteneurOcean, conteneurRadar);
        plateauDeJeu.setAlignment(Pos.CENTER);

        plateauDeJeu.setTranslateX((1280 - 850) / 2.0);
        plateauDeJeu.setTranslateY((720 - 400) / 2.0);

        FXGL.addUINode(plateauDeJeu);
    }

    static void main(String[] args) {
        new CreateDB();
        launch(args);
    }
}