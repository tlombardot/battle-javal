package school.coda.darill_thomas_louis.bataillejavale.ui;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GrilleUI extends GridPane {

    private static final int TAILLE_CASE = 40;

    public GrilleUI() {
        this.setHgap(1);
        this.setVgap(1);

        for (int i = 0; i < 10; i++) {
            // Chiffres 1 à 10 (Horizontal, Ligne 0)
            Text textChiffre = new Text(String.valueOf(i + 1));
            textChiffre.setFont(Font.font("Arial", 16));
            StackPane conteneurChiffre = new StackPane(textChiffre);
            conteneurChiffre.setPrefSize(TAILLE_CASE, TAILLE_CASE / 2.0);
            this.add(conteneurChiffre, i + 1, 0);

            // Lettres A à J (Vertical, Colonne 0)
            char lettre = (char) ('A' + i);
            Text textLettre = new Text(String.valueOf(lettre));
            textLettre.setFont(Font.font("Arial", 16));
            StackPane conteneurLettre = new StackPane(textLettre);
            conteneurLettre.setPrefSize(TAILLE_CASE / 2.0, TAILLE_CASE);
            this.add(conteneurLettre, 0, i + 1);
        }

        // Génération des 100 cases de l'océan
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Rectangle caseMer = new Rectangle(TAILLE_CASE, TAILLE_CASE);
                caseMer.setFill(Color.LIGHTBLUE);
                caseMer.setStroke(Color.BLACK);
                // On ajoute la case dans la grille (décalée de +1 pour laisser la place aux en-têtes)
                this.add(caseMer, x + 1, y + 1);
            }
        }
    }
}