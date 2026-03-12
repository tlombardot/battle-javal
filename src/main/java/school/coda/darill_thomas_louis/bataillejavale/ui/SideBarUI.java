package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SideBarUI extends VBox {

    private final Text texteTour;
    private final Text texteManche;
    private final VBox logContainer;
    private final ScrollPane scrollPane;

    /**
     *
     */
    public SideBarUI() {

        setPrefWidth(340);
        setPrefHeight(720);
        setPadding(new Insets(20));
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: rgba(10, 20, 30, 0.85); -fx-border-color: #00ffff; -fx-border-width: 0 0 0 2;");

        texteManche = creerTexteStylise("MANCHE : 1", 24, "#ffffff");
        texteTour = creerTexteStylise("PHASE DE PLACEMENT", 18, "#00ffff");

        VBox headerBox = new VBox(5, texteManche, texteTour);
        headerBox.setAlignment(Pos.CENTER);

        Rectangle separateur = new Rectangle(300, 2, Color.web("#00ffff", 0.5));
        Text logTitre = creerTexteStylise("HISTORIQUE DES SEIGNEURS_", 16, "#a0a0a0");

        logContainer = new VBox(5);
        logContainer.setPadding(new Insets(10));

        scrollPane = new ScrollPane(logContainer);
        scrollPane.setPrefHeight(500);
        scrollPane.setFitToWidth(true);

        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-control-inner-background: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        getChildren().addAll(headerBox, separateur, logTitre, scrollPane);
    }

    /**
     *Ajoute un style au texte
     * @param contenu
     * @param taille
     * @param couleurHex
     * @return
     */
    private Text creerTexteStylise(String contenu, int taille, String couleurHex) {
        Text t = new Text(contenu);
        t.setFont(Font.font("Consolas", FontWeight.BOLD, taille));
        t.setFill(Color.web(couleurHex));
        return t;
    }

    public void setPhase(String phase) {
        texteTour.setText(phase);
    }

    public void setTexteManche(int numeroManche) {
        texteManche.setText("Manche : " + numeroManche);
    }

    public void ajouterLog(String message, String type) {
        Text logText = new Text("» " + message);
        logText.setFont(Font.font("Times New Roman", 14));
        logText.setWrappingWidth(280);

        switch (type.toUpperCase()) {
            case "INFO": logText.setFill(Color.web("#eeeeee")); break;
            case "TOUCHE": logText.setFill(Color.web("#ff3333")); break;
            case "RATE": logText.setFill(Color.web("#a0a0a0")); break;
            case "ALERTE": logText.setFill(Color.web("#ffaa00")); break;
            default: logText.setFill(Color.web("#ffffff"));
        }

        logContainer.getChildren().add(logText);

        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
}