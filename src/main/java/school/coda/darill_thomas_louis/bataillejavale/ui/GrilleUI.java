package school.coda.darill_thomas_louis.bataillejavale.ui;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
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

    private final String couleurThemeHex;
    private AnimationTimer effetPulsation;

    public interface GrilleListener {
        void onCaseLeftClick(int x, int y);
        void onCaseRightClick(int x, int y);
        void onDragOver(int x, int y, String nomNavire, boolean estHorizontal);
        void onDragDropped(int x, int y, String nomNavire, boolean estHorizontal);
        void onDragExited();
        String onDragStart(int x, int y);
    }

    private GrilleListener listener;

    public GrilleUI(String couleurThemeHex) {
        this.couleurThemeHex = couleurThemeHex;
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

        demarrerAnimationInterne();
    }

    private void demarrerAnimationInterne() {
        if (effetPulsation != null) effetPulsation.stop();

        effetPulsation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Temps ralenti pour un effet liquide naturel
                double t = now / 1_000_000_000.0 * 1.5;

                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 10; y++) {
                        if ("VIDE".equals(rectangles[x][y].getUserData())) {

                            // Coordonnées ajustées pour la taille des vagues
                            double u = x * 0.4;
                            double v = y * 0.4;

                            // On croise 3 ondes mathématiques (horizontale, verticale, diagonale)
                            double onde1 = Math.sin(u + t);
                            double onde2 = Math.cos(v + t * 0.8);
                            double onde3 = Math.sin(u + v - t * 1.2);

                            // On mélange les vagues et on normalise entre 0.0 et 1.0
                            double surfaceEau = (onde1 + onde2 + onde3 + 3) / 6.0;

                            // Math.pow accentue les pics : ça simule les reflets brillants de l'eau (effet 2.5D)
                            double cretesLumineuses = Math.pow(surfaceEau, 2.5);

                            double opacite = 0.04 + (cretesLumineuses * 0.35);

                            rectangles[x][y].setFill(Color.web(couleurThemeHex, Math.min(opacite, 1.0)));
                        }
                    }
                }
            }
        };
        effetPulsation.start();
    }

    @NotNull
    private Rectangle createCaseMer(int x, int y) {
        Rectangle caseMer = new Rectangle(TAILLE_CASE, TAILLE_CASE);
        caseMer.setUserData("VIDE");
        caseMer.setFill(Color.web("#000000", 0.4));
        caseMer.setStroke(Color.web(couleurThemeHex, 0.8));
        caseMer.setStrokeWidth(1.5);
        caseMer.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        caseMer.setArcWidth(4);
        caseMer.setArcHeight(4);
        rectangles[x][y] = caseMer;

        final int mapX = x;
        final int mapY = y;

        caseMer.setOnMouseClicked(event -> {
            if (listener != null) {
                if (event.getButton() == MouseButton.PRIMARY) listener.onCaseLeftClick(mapX, mapY);
                else if (event.getButton() == MouseButton.SECONDARY) listener.onCaseRightClick(mapX, mapY);
            }
        });
        caseMer.setOnDragDetected(event -> {
            if (listener != null) {
                String dragData = listener.onDragStart(mapX, mapY);

                if (dragData != null) {
                    javafx.scene.input.Dragboard db = caseMer.startDragAndDrop(TransferMode.MOVE);
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(dragData);
                    db.setContent(content);
                    event.consume();
                }
            }
        });

        caseMer.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                if (listener != null) {
                    String[] data = event.getDragboard().getString().split(";");
                    listener.onDragOver(mapX, mapY, data[0], Boolean.parseBoolean(data[1]));
                }
            }
            event.consume();
        });

        caseMer.setOnDragExited(event -> {
            if (listener != null) listener.onDragExited();
            event.consume();
        });

        caseMer.setOnDragDropped(event -> {
            boolean success = false;
            if (event.getDragboard().hasString()) {
                String[] data = event.getDragboard().getString().split(";");
                if (listener != null) {
                    listener.onDragDropped(mapX, mapY, data[0], Boolean.parseBoolean(data[1]));
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return caseMer;
    }

    @NotNull
    private StackPane createLettres(int i) {
        char lettre = (char) ('A' + i);
        Text textLettre = new Text(String.valueOf(lettre));
        textLettre.setFont(Font.font("Consolas", 16));
        textLettre.setFill(Color.web(couleurThemeHex));
        StackPane conteneurLettre = new StackPane(textLettre);
        conteneurLettre.setPrefSize(TAILLE_CASE / 2.0, TAILLE_CASE);
        return conteneurLettre;
    }

    @NotNull
    private StackPane createChiffre(int i) {
        Text textChiffre = new Text(String.valueOf(i + 1));
        textChiffre.setFont(Font.font("Consolas", 16));
        textChiffre.setFill(Color.web(couleurThemeHex));
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
                    rectangles[x][y].setUserData("BATEAU");
                    rectangles[x][y].setFill(Color.web("#00ffff", 0.3));
                    rectangles[x][y].setStroke(Color.web("#00FFFF", 1.0));
                    rectangles[x][y].setStrokeWidth(2);
                } else {
                    if (!"TIR".equals(rectangles[x][y].getUserData())) {
                        rectangles[x][y].setUserData("VIDE");
                        rectangles[x][y].setStrokeWidth(1.5);
                    }
                }
            }
        }
    }

    public void colorierCase(int x, int y, Color couleur) {
        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            rectangles[x][y].setUserData("TIR");
            rectangles[x][y].setFill(couleur);
            rectangles[x][y].setArcWidth(8);
            rectangles[x][y].setArcHeight(8);
        }
    }

    public void dessinerApercu(int x, int y, Color couleur) {
        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            rectangles[x][y].setUserData("APERÇU");
            rectangles[x][y].setFill(couleur);
        }
    }
}