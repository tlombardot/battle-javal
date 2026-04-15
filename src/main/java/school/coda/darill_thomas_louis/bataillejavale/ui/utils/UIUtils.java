package school.coda.darill_thomas_louis.bataillejavale.ui.utils;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

// 🚨 devrait être dans le package ui
public class UIUtils {

    /**
     * Ferme un composant UI avec un effet de fondu et l'enlève de son parent.
     */
    public static void fermerFenetre(Pane fenetreAFermer, Pane parent, Runnable actionApresFermeture) {
        fenetreAFermer.setOnKeyPressed(null);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), fenetreAFermer);
        ft.setToValue(0);
        ft.setOnFinished(_ -> {
            parent.getChildren().remove(fenetreAFermer);
            if (actionApresFermeture != null) {
                actionApresFermeture.run();
            }
        });
        ft.play();
    }
}