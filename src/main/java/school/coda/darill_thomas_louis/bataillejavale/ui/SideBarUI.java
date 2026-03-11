package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SideBarUI extends VBox {
    private Text textTour;
    private VBox logContainer;

    public SideBarUI() {
        setPrefWidth(300);
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #1a2230; -fx-border-color: #00ffff; -fx-border-width: 2px;");

        Text titre = new Text("HISTORIQUE");
        titre.setFont(Font.font("Consolas", 24));
        titre.setFill(Color.WHITE);

        textTour = new Text("Tour courant : 1");
        textTour.setFont(Font.font("Consolas", 18));
        textTour.setFill(Color.CYAN);

        logContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(logContainer);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background: #1a2230; -fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        getChildren().addAll(titre, textTour, scrollPane);
    }

    public void setTour(int tour) {
        textTour.setText("Tour courant : " + tour);
    }

    public void ajouterLog(String message, Color couleur) {
        Text logText = new Text(message);
        logText.setFont(Font.font("Consolas", 14));
        logText.setFill(couleur);
        logText.setWrappingWidth(240);

        logContainer.getChildren().add(0, logText);
    }
}