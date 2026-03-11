package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

import java.util.List;

public class SelectBoard extends VBox {

    public SelectBoard(List<Vaisseau> flotteRestante) {
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);
        this.setPrefHeight(300); // Hauteur minimale

        for (Vaisseau v : flotteRestante) {
            this.getChildren().add(new VaisseauUI(v));
        }
    }

    // Permet de retirer visuellement un bateau une fois qu'il est placé sur la grille
    public void retirerVaisseau(String nomVaisseau) {
        this.getChildren().removeIf(node ->
                node instanceof VaisseauUI && ((VaisseauUI) node).getNavire().getNom().equals(nomVaisseau)
        );
    }
}