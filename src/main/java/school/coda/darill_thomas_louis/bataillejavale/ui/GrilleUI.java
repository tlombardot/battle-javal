package school.coda.darill_thomas_louis.bataillejavale.ui;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.function.BiConsumer;

public class GrilleUI extends GridPane {

    private static final int TAILLE_CASE = 40;
    private final Rectangle[][] rectangles = new Rectangle[10][10];

    private BiConsumer<Integer, Integer> onCaseClicked;

    public GrilleUI() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(1);
        this.setVgap(1);

        for (int i = 0; i < 10; i++) {
            Text textChiffre = new Text(String.valueOf(i + 1));
            textChiffre.setFont(Font.font("Arial", 16));
            StackPane conteneurChiffre = new StackPane(textChiffre);
            conteneurChiffre.setPrefSize(TAILLE_CASE, TAILLE_CASE / 2.0);
            this.add(conteneurChiffre, i + 1, 0);

            char lettre = (char) ('A' + i);
            Text textLettre = new Text(String.valueOf(lettre));
            textLettre.setFont(Font.font("Arial", 16));
            StackPane conteneurLettre = new StackPane(textLettre);
            conteneurLettre.setPrefSize(TAILLE_CASE / 2.0, TAILLE_CASE);
            this.add(conteneurLettre, 0, i + 1);
        }

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Rectangle caseMer = new Rectangle(TAILLE_CASE, TAILLE_CASE);
                caseMer.setFill(Color.LIGHTBLUE);
                caseMer.setStroke(Color.BLACK);

                rectangles[x][y] = caseMer;

                final int mapX = x;
                final int mapY = y;
                caseMer.setOnMouseClicked(_ -> {
                    if (onCaseClicked != null) {
                        onCaseClicked.accept(mapX, mapY);
                    }
                });

                this.add(caseMer, x + 1, y + 1);
            }
        }
    }

    public void setOnCaseClicked(BiConsumer<Integer, Integer> action) {
        this.onCaseClicked = action;
    }

    public void colorierCase(int x, int y, Color couleur) {
        rectangles[x][y].setFill(couleur);
    }
}