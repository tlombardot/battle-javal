package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;

public class SideBarUI extends VBox {
    private Text textManche;
    private Text textTour;
    private VBox logContainer;
    private ScrollPane scrollPane;

    public SideBarUI() {
        setPrefWidth(350);
        setPrefHeight(720);
        setPadding(new Insets(20));
        setSpacing(20);
    }

}
