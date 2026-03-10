package school.coda.darill_thomas_louis.bataillejavale.ui;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import school.coda.darill_thomas_louis.bataillejavale.core.model.GrilleOcean;

public class GrilleUI extends GridPane {
    /**
     *
     */
    private static final int TAILLE_CASE = 40;
    private final Rectangle[][] rectangles = new Rectangle[10][10];

    public interface GrilleListener {
        void onCaseLeftClick(int x, int y);
        void onCaseRightClick(int x, int y);
        void onCaseHoverEnter(int x, int y);
        void onCaseHoverExit(int x, int y);
    }

    private GrilleListener listener;

    public GrilleUI() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(1);
        this.setVgap(1);

        for (int i = 0; i < 10; i++) {
            this.add(createChiffre(i), i + 1, 0);
            this.add(createLettres(i), 0, i + 1);
        }

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                this.add(createCaseMer(x,y), x + 1, y + 1);
            }
        }
    }

    @NotNull
    private Rectangle createCaseMer(int x, int y) {
        Rectangle caseMer = new Rectangle(TAILLE_CASE, TAILLE_CASE);
        caseMer.setFill(Color.LIGHTBLUE);
        caseMer.setStroke(Color.BLACK);
        rectangles[x][y] = caseMer;

        final int mapX = x;
        final int mapY = y;

        caseMer.setOnMouseClicked(event -> {
            if (listener != null) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    listener.onCaseLeftClick(mapX, mapY);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    listener.onCaseRightClick(mapX, mapY);
                }
            }
        });

        caseMer.setOnMouseEntered(_ -> {
            if (listener != null) listener.onCaseHoverEnter(mapX, mapY);
        });

        caseMer.setOnMouseExited(_ -> {
            if (listener != null) listener.onCaseHoverExit(mapX, mapY);
        });
        return caseMer;
    }

    @NotNull
    private static StackPane createLettres(int i) {
        char lettre = (char) ('A' + i);
        Text textLettre = new Text(String.valueOf(lettre));
        textLettre.setFont(Font.font("Arial", 16));
        StackPane conteneurLettre = new StackPane(textLettre);
        conteneurLettre.setPrefSize(TAILLE_CASE / 2.0, TAILLE_CASE);
        return conteneurLettre;
    }

    @NotNull
    private static StackPane createChiffre(int i) {
        Text textChiffre = new Text(String.valueOf(i + 1));
        textChiffre.setFont(Font.font("Arial", 16));
        StackPane conteneurChiffre = new StackPane(textChiffre);
        conteneurChiffre.setPrefSize(TAILLE_CASE, TAILLE_CASE / 2.0);
        return conteneurChiffre;
    }

    public void setListener(GrilleListener listener) {
        this.listener = listener;
    }

    public void rafraichir(GrilleOcean ocean) {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (ocean.getPlateau()[x][y] != null) {
                    rectangles[x][y].setFill(Color.DARKGRAY); // Bateau officiellement placé
                } else {
                    rectangles[x][y].setFill(Color.LIGHTBLUE); // Eau
                }
            }
        }
    }

    // Pour colorier temporairement (vert ou rouge) la prévisualisation
    public void colorierCase(int x, int y, Color couleur) {
        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            rectangles[x][y].setFill(couleur);
        }
    }
}