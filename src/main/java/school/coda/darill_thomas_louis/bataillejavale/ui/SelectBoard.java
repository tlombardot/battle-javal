package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

import java.util.List;

public class SelectBoard extends VBox {

    public SelectBoard(List<Vaisseau> flotteRestante) {
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);

        for (Vaisseau v : flotteRestante) {
            VaisseauUI navireUI = new VaisseauUI(v);
            Group conteneurRotation = new Group(navireUI);
            this.getChildren().add(conteneurRotation);
        }
    }

    public void retirerVaisseau(String nomVaisseau) {
        this.getChildren().removeIf(node -> {
            if (node instanceof Group groupe) {
                if (!groupe.getChildren().isEmpty() && groupe.getChildren().getFirst() instanceof VaisseauUI ui) {
                    return ui.getNavire().getNom().equals(nomVaisseau);
                }
            }
            return false;
        });
    }
}