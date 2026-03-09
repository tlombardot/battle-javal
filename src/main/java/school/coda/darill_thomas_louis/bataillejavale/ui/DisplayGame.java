package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.CreateDB;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class DisplayGame extends GameApplication {

    @Override
    /*
     * Configuration de la fenêtre d'affichage
     * */
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);
        gameSettings.setTitle("Javale-Battle");
        gameSettings.setVersion("0.1");
//        gameSettings.setStageStyle(StageStyle.UNDECORATED);
    }

    @Override
    /*
     * Configuration du menu principale
     * */
    protected void initUI() {
        getGameScene().getRoot().setCursor(Cursor.DEFAULT);

        Rectangle fond = new Rectangle(1280, 720, Color.web("#0a0f18"));
        FXGL.addUINode(fond);


        //Notre Titre
        Text title = new Text("JAVALE\nBATTLE");
        title.setFont(Font.font("Impact", 80));
        title.setFill(Color.WHITE);
        title.setTranslateX(100);
        title.setTranslateY(150);
        FXGL.addUINode(title);

        //Notre Menu
        VBox completeMenuBox = new VBox(20);
        completeMenuBox.setTranslateX(100);
        completeMenuBox.setTranslateY(350);

        //Utiliser notre fonction pour créer les buttons
        Node btnPlay = createButtonMenu("Start Game", ()-> {
            IO.println("Lancement de la partie.");
            FXGL.getGameScene().clearUINodes();
            GrilleUI grilleOcean = new GrilleUI();
            grilleOcean.setTranslateX(50);
            grilleOcean.setTranslateY(50);
            FXGL.addUINode(grilleOcean);
        });

        Node btnMultiPlay = createButtonMenu("Multiparty game", ()-> {
            IO.println("Lancement de la partie multijoueur.");
        });

        Node btnOptions = createButtonMenu("Settings", ()-> {
            IO.println("Ouverture des paramètres.");
        });

        Node btnQuit = createButtonMenu("Exit", ()-> {
            FXGL.getGameController().exit();
        });

        completeMenuBox.getChildren().addAll(btnPlay, btnMultiPlay, btnOptions, btnQuit);
        FXGL.addUINode(completeMenuBox);

        // Les crédits
        Text credits = new Text("Created by KING_Darill | CYBER080Thomas | SMART_Louis");
        credits.setFont(Font.font("Arial", 12));
        credits.setFill(Color.GRAY);
        credits.setTranslateX(100);
        credits.setTranslateY(680);
        FXGL.addUINode(credits);
    }

    private Node createButtonMenu(String texte, Runnable action) {

        Text textNode = new Text(texte);
        textNode.setFont(Font.font("Impact", 30));
        textNode.setFill(Color.web("#a0a0a0"));

        Polygon curseur = new Polygon(
                0.0, 0.0,
                15.0, 7.5,
                0.0, 15.0
        );
        curseur.setFill(Color.CYAN);
        curseur.setVisible(false);

        HBox menuBox = new HBox(15, textNode, curseur);
        menuBox.setAlignment(Pos.CENTER_LEFT);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(15);
        glow.setSpread(0.2);

        //Animations

        menuBox.setOnMouseEntered(e-> {
            textNode.setFill(Color.WHITE);
            textNode.setEffect(glow);
            curseur.setVisible(true);
        });

        menuBox.setOnMouseExited(e-> {
            textNode.setFill(Color.web("#a0a0a0"));
            textNode.setEffect(null);
            curseur.setVisible(false);
        });

        menuBox.setOnMouseClicked(e->{
            action.run();
        });

        return menuBox;
    }

    /*
     * Lancement de la fenêtre (pour l'instant)
     * */
    static void main(String[] args) {
        new CreateDB();
        launch(args);
    }
}
