package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import school.coda.darill_thomas_louis.bataillejavale.core.model.GrilleOcean;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

import java.util.ArrayList;
import java.util.List;

public class GrilleUI extends StackPane {

    private static final int TAILLE_CASE = 40;

    private final Rectangle[][] rectangles = new Rectangle[10][10];
    private final String couleurThemeHex;
    private AnimationTimer effetPulsation;

    private final GridPane grilleCases = new GridPane();
    private final Pane coucheBateaux = new Pane();

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

        grilleCases.setAlignment(Pos.CENTER);
        grilleCases.setHgap(1);
        grilleCases.setVgap(1);

        for (int i = 0; i < 10; i++) {
            grilleCases.add(createChiffre(i), i + 1, 0);
            grilleCases.add(createLettres(i), 0, i + 1);
        }

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                grilleCases.add(createCaseMer(x,y), x + 1, y + 1);
            }
        }

        coucheBateaux.setTranslateX((TAILLE_CASE / 2.0) + 1);
        coucheBateaux.setTranslateY((TAILLE_CASE / 2.0) + 1);
        coucheBateaux.setPickOnBounds(false);


        coucheBateaux.setManaged(false);
        this.getChildren().add(new Group(grilleCases, coucheBateaux));

        demarrerAnimationInterne();
    }



    private void demarrerAnimationInterne() {
        if (effetPulsation != null) effetPulsation.stop();

        effetPulsation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double t = now / 1_000_000_000.0 * 1.5;

                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 10; y++) {
                        if ("VIDE".equals(rectangles[x][y].getUserData()) || "BATEAU".equals(rectangles[x][y].getUserData())) {
                            double u = x * 0.4;
                            double v = y * 0.4;

                            double onde1 = Math.sin(u + t);
                            double onde2 = Math.cos(v + t * 0.8);
                            double onde3 = Math.sin(u + v - t * 1.2);

                            double surfaceEau = (onde1 + onde2 + onde3 + 3) / 6.0;
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
                if (!"TIR".equals(rectangles[x][y].getUserData())) {
                    rectangles[x][y].setUserData("VIDE");
                    rectangles[x][y].setFill(Color.web("#000000", 0.4));
                    rectangles[x][y].setStrokeWidth(1.5);
                    rectangles[x][y].setStroke(Color.web(couleurThemeHex, 0.8));
                }
            }
        }

        // Vider la couche des dessins de bateaux
        coucheBateaux.getChildren().clear();

        // Extraire les navires
        List<Vaisseau> naviresPlaces = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Vaisseau v = ocean.getVaisseauAt(x, y);
                if (v != null && !naviresPlaces.contains(v)) {
                    naviresPlaces.add(v);
                }
            }
        }

        // Et ensute je les place
        for (Vaisseau navire : naviresPlaces) {

            if (navire.getX() < 0 || navire.getY() < 0) {
                continue; //si un bateau est pas placé je passe au suivant
            }

            VaisseauUI dessinBateau = new VaisseauUI(navire);
            dessinBateau.forcerOrientation(navire.estHorizontal());

            double pixelX = navire.getX() * (TAILLE_CASE + 1);
            double pixelY = navire.getY() * (TAILLE_CASE + 1);

            dessinBateau.setLayoutX(pixelX);
            dessinBateau.setLayoutY(pixelY);
//            dessinBateau.setMouseTransparent(true);

            coucheBateaux.getChildren().add(dessinBateau);

            for(int i = 0; i < navire.getTaille(); i++){
                int cx = navire.estHorizontal() ? navire.getX() + i : navire.getX();
                int cy = navire.estHorizontal() ? navire.getY() : navire.getY() + i;
                if(cx < 10 && cy < 10 && !"TIR".equals(rectangles[cx][cy].getUserData())) {
                    rectangles[cx][cy].setUserData("BATEAU");
                    rectangles[cx][cy].setFill(Color.TRANSPARENT);
                }
            }
        }
    }

    /**
     *Gère la position et la couleur d'une case
     * @param x
     * Gère la position de la case sur l'axe des abscisses
     * @param y
     * Gère la position de la case sur l'axe des ordonné
     * @param couleur
     * Charge la couleur de la case
     */
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