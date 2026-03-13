package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

import java.util.List;

public class SelectBoard extends VBox {

    public SelectBoard(List<Vaisseau> flotteRestante) {
        this.setSpacing(20); // Un peu plus d'espace entre les vaisseaux
        this.setAlignment(Pos.CENTER);

        // On ne fixe PLUS la hauteur ici, c'est le ScrollPane dans PlateauDeJeu qui va gérer la limite !

        for (Vaisseau v : flotteRestante) {
            this.getChildren().add(new VaisseauUI(v));
        }
    }

    public void retirerVaisseau(String nomVaisseau) {
        this.getChildren().removeIf(node ->
                node instanceof VaisseauUI && ((VaisseauUI) node).getNavire().getNom().equals(nomVaisseau)
        );
    }
}