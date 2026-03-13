package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

public class NotificationUI extends StackPane {
    private final Text messageText;
    private final Rectangle fondAlerte;
    private SequentialTransition animationActuelle;

    public NotificationUI() {
        setVisible(false);
        setMouseTransparent(true);
        setMaxSize(600, 65);
        StackPane.setAlignment(this, Pos.TOP_CENTER);
//        setTranslateX(-100);
        setTranslateY(-100);

        fondAlerte = new Rectangle(600, 65);
        fondAlerte.setArcWidth(8);
        fondAlerte.setArcHeight(8);
        fondAlerte.setStrokeType(StrokeType.INSIDE);
        fondAlerte.setStrokeWidth(3);

        messageText = new Text();
        messageText.setFont(FontUtils.getPolice(23));
        messageText.setFill(Color.WHITE);

        getChildren().addAll(fondAlerte, messageText);
    }

    public void afficherAlerte(String message, String couleurHex) {
        if (animationActuelle != null && animationActuelle.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            animationActuelle.stop();
        }

        Color couleurBase = Color.web(couleurHex);
        messageText.setText(message.toUpperCase());
        messageText.setEffect(new DropShadow(15, Color.BLACK));

        LinearGradient stylePlaque = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#11151c", 0.95)),
                new Stop(0.5, couleurBase.deriveColor(0, 1, 0.35, 0.85)),
                new Stop(1, Color.web("#080b0f", 0.95))
        );

        fondAlerte.setFill(stylePlaque);
        fondAlerte.setStroke(couleurBase.brighter());

        DropShadow neonGlow = new DropShadow(10, couleurBase.deriveColor(0, 1, 1, 0.8));
        neonGlow.setSpread(0.25);
        fondAlerte.setEffect(neonGlow);

        setVisible(true);

        TranslateTransition slideDown = new TranslateTransition(Duration.seconds(0.35), this);
        slideDown.setFromY(-100);
        slideDown.setToY(15);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.35), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition popDown = new ParallelTransition(slideDown, fadeIn);
        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));

        TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.35), this);
        slideUp.setToY(-100);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.35), this);
        fadeOut.setToValue(0);

        ParallelTransition popUp = new ParallelTransition(slideUp, fadeOut);

        animationActuelle = new SequentialTransition(popDown, pause, popUp);
        animationActuelle.setOnFinished(_ -> setVisible(false));

        animationActuelle.play();
    }
}