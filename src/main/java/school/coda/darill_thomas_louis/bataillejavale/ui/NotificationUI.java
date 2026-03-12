package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class NotificationUI extends StackPane {
    private final Text messageText;
    private final Rectangle fondAlerte;

    public NotificationUI() {
        setVisible(false);
        setMouseTransparent(true);

        fondAlerte = new Rectangle(800, 100, Color.web("#ff3333", 0.8));
        fondAlerte.setEffect(new DropShadow(20, Color.RED));
        fondAlerte.setArcWidth(15);
        fondAlerte.setArcHeight(15);

        messageText = new Text();
        messageText.setFont(Font.font("Impact", FontWeight.NORMAL, 40));
        messageText.setFill(Color.WHITE);

        getChildren().addAll(fondAlerte, messageText);
        setAlignment(Pos.CENTER);
    }

    public void afficherAlerte(String message, String couleurHex) {
        messageText.setText(message);
        fondAlerte.setFill(Color.web(couleurHex, 0.85));
        fondAlerte.setEffect(new DropShadow(20, Color.web(couleurHex)));

        setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        FXGL.getGameTimer().runOnceAfter(() -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), this);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> setVisible(false));
            fadeOut.play();
        }, Duration.seconds(3.0));
    }


}
