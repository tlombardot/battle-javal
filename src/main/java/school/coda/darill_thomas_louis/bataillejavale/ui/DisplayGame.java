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
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
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
        // Le Titre du jeu
        Text title = new Text("LA BATAILLE JAVALE");
        title.setFont(Font.font("Arial", 48));
        title.setFill(Color.DARKBLUE);
        title.setTranslateX(150);
        title.setTranslateY(150);


        // Les crédits
        Text credits = new Text("Créé par Darill, Thomas et Louis");
        credits.setFont(Font.font("Arial", 16));
        credits.setFill(Color.GRAY);
        credits.setTranslateX(250);
        credits.setTranslateY(550);

        // Le bouton pour lancer la partie
        Button btnJouer = new Button("Lancer une nouvelle partie");
        btnJouer.setTranslateX(300);
        btnJouer.setTranslateY(300);
        btnJouer.setPrefSize(200, 50);

        // L'action du bouton quand on clique dessus
        btnJouer.setOnAction(_ -> {
            FXGL.getGameScene().clearUINodes();
            GrilleUI grilleOcean = new GrilleUI();
            grilleOcean.setTranslateX(50);
            grilleOcean.setTranslateY(50);
            FXGL.addUINode(grilleOcean);

        });

        // On ajoute tout ça à l'écran
        FXGL.addUINode(title);
        FXGL.addUINode(credits);
        FXGL.addUINode(btnJouer);
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
