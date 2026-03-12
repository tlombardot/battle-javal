package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuUI extends Pane {

    private final List<TechButton> menuButtons = new ArrayList<>();
    private int currentSelection = 0;
    private boolean isActive = true;

    public  MenuUI() {
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
        }catch (NullPointerException e){
            IO.println("Ficher interface introuvable : " + e.getMessage());
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
            logoTitle.setTranslateY(10);
            getChildren().add(logoTitle);
        } catch (Exception _) {
            System.out.println("Logo non trouvé.");
        }
    }
    /**
     * Construction des boutons dans le menu principal
     */
    private void buildButtons() {
        VBox completeMenuBox = new VBox(20);
        completeMenuBox.setTranslateX(100);
        completeMenuBox.setTranslateY(350);

        menuButtons.add(new TechButton("START GAME_", this::lancerEcranLoading));
        menuButtons.add(new TechButton("MULTIPLAYER MODE_", () -> System.out.println("Mode en développement.")));
        menuButtons.add(new TechButton("SETTINGS_", () -> System.out.println("Ouverture paramètres.")));
        menuButtons.add(new TechButton("EXIT_", () -> FXGL.getGameController().exit()));

        for (TechButton btn : menuButtons) {
            completeMenuBox.getChildren().add(btn.visual);
        }
        getChildren().add(completeMenuBox);
    }
    /**
     * Construction des crédits
     */
    private void buildCredits() {
        Text credits = new Text("Created by KING_Darill | CYBER080Thomas | SMART_Louis");
        credits.setFont(Font.font("Arial", 15));
        credits.setFill(Color.web("#dddddd"));
        credits.setTranslateX(100);
        credits.setTranslateY(680);
        getChildren().add(credits);
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

        Rectangle blackScreen = new Rectangle(1280, 720, Color.BLACK);
        getChildren().add(blackScreen);

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

        FadeTransition blink = new FadeTransition(Duration.seconds(0.5), loadingText);
        blink.setFromValue(1.0);
        blink.setToValue(0.2);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();

        getChildren().add(loadingText);

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

        public TechButton(String text, Runnable action) {
            this.action = action;
            visual = new StackPane();
            visual.setAlignment(Pos.CENTER_LEFT);

            techBand = new Polygon(0, 0, 400, 0, 370, 40, 0, 40);

            Stop[] stops = new Stop[] {
                    new Stop(0, Color.web("#00ffff", 0.15)),
                    new Stop(1, Color.web("#00ffff", 0.35))
            };
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            techBand.setFill(gradient);

            DropShadow glow = new DropShadow(15, Color.web("#00e6e6"));
            techBand.setEffect(glow);
            techBand.setVisible(false);

            textNode = new Text(text);
            textNode.setFont(Font.font("Times New Romans", 30));
            textNode.setFill(Color.web("#a0a0a0"));
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
            textNode.setFill(active ? Color.WHITE : Color.web("#a0a0a0"));
            if (active) {
                textNode.setTranslateX(35);
            } else {
                textNode.setTranslateX(20);
            }
        }
    }


}
