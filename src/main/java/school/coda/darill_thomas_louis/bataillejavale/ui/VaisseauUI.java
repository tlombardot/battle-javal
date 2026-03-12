package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Pos;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
/**
 *Gère le postionnement des navires ainsi que le drag & drop
 */
public class VaisseauUI extends VBox {

    private final Vaisseau navire;
    private boolean estHorizontal = true;


    public VaisseauUI(Vaisseau navire) {
        this.navire = navire;
        this.setAlignment(Pos.CENTER);
        dessinerVaisseau();

        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                estHorizontal = !estHorizontal;
                dessinerVaisseau();
            }
        });

        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString(navire.getNom() + ";" + estHorizontal);
            db.setContent(content);
            event.consume();
        });

        // Le curseur indique qu'on peut l'attraper
        this.setStyle("-fx-cursor: hand;");
    }

    /**
     * Crée des bâteaux à la verticale ou à l'horizontale
     */
    private void dessinerVaisseau() {
        this.getChildren().clear();

        if (estHorizontal) {
            HBox ligne = new HBox(2);
            for (int i = 0; i < navire.getTaille(); i++) ligne.getChildren().add(creerCarre());
            this.getChildren().add(ligne);
        } else {
            VBox colonne = new VBox(2);
            for (int i = 0; i < navire.getTaille(); i++) colonne.getChildren().add(creerCarre());
            this.getChildren().add(colonne);
        }
    }

    private Rectangle creerCarre() {
        Rectangle r = new Rectangle(30, 30);
        r.setFill(Color.web("#00ffff", 0.8));
        r.setStroke(Color.WHITE);
        r.setArcWidth(5);
        r.setArcHeight(5);
        return r;
    }

    public Vaisseau getNavire() { return navire; }
}