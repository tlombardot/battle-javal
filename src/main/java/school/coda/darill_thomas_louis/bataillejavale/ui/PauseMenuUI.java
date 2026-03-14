package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

public class PauseMenuUI extends StackPane {

    public PauseMenuUI(Runnable actionReprendre, Runnable actionRetourMenu) {
        this.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        Rectangle fondNoir = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.color(0, 0, 0, 0.85));

        VBox menuBox = new VBox(25);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 350);
        menuBox.setStyle("-fx-background-color: #0a0f18; -fx-border-color: #00ffff; -fx-border-width: 1px; -fx-padding: 30;");
        menuBox.setEffect(new DropShadow(25, Color.web("#00ffff", 0.3)));

        Text titre = new Text("MENU SYSTÈME");
        titre.setFont(FontUtils.getPolice(32));
        titre.setFill(Color.web("#00ffff"));
        titre.setTranslateY(-10);

        // 4. BOUTONS
        Button btnReprendre = creerBouton("REPRENDRE", "#00ffff", actionReprendre);
        Button btnMenu = creerBouton("RETOUR AU MENU", "#ffaa00", actionRetourMenu);
        Button btnQuitter = creerBouton("QUITTER LE JEU", "#ff0000", () -> FXGL.getGameController().exit());

        menuBox.getChildren().addAll(titre, btnReprendre, btnMenu, btnQuitter);
        this.getChildren().addAll(fondNoir, menuBox);

        this.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), this);
        ft.setToValue(1);
        ft.play();

        this.setOnMouseClicked(e -> e.consume());
    }


    private Button creerBouton(String texte, String couleurBase, Runnable action) {
        Button btn = new Button(texte);
        btn.setFont(FontUtils.getPolice(18));
        btn.setPrefSize(300, 45);

        String styleNormal = "-fx-background-color: transparent; -fx-text-fill: " + couleurBase + "; -fx-border-color: " + couleurBase + "; -fx-border-width: 2px; -fx-cursor: hand;";
        String styleHover = "-fx-background-color: " + couleurBase + "; -fx-text-fill: #0a0f18; -fx-border-color: " + couleurBase + "; -fx-border-width: 2px; -fx-cursor: hand;";

        btn.setStyle(styleNormal);
        btn.setOnMouseEntered(_ -> btn.setStyle(styleHover));
        btn.setOnMouseExited(_ -> btn.setStyle(styleNormal));
        btn.setOnAction(_ -> action.run());

        return btn;
    }
}