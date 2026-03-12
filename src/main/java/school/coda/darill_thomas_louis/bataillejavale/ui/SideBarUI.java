package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

public class SideBarUI extends VBox {

    private final Text texteTour;
    private final Text texteManche;
    private final VBox logContainer;
    private final ScrollPane scrollPane;

    public SideBarUI() {
        setPrefWidth(340);
        setPrefHeight(720);
        setPadding(new Insets(25));
        setSpacing(25);
        setAlignment(Pos.TOP_CENTER);

        setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(15, 25, 35, 0.95), rgba(5, 10, 15, 0.95)); " +
                "-fx-border-color: #00ffff; " +
                "-fx-border-width: 0 0 0 3; " +
                "-fx-border-style: solid;");


        texteManche = creerTexteStylise("MANCHE : 1", 28, "#ffffff", true);
        texteTour = creerTexteStylise("PHASE DE PLACEMENT", 18, "#00ffff", true);

        VBox headerBox = new VBox(10, texteManche, texteTour);
        headerBox.setAlignment(Pos.CENTER);

        Rectangle separateur = new Rectangle(280, 2, Color.web("#00ffff", 0.6));
        Text logTitre = creerTexteStylise("HISTORIQUE DE COMBAT_", 16, "#a0a0a0", false);

        logContainer = new VBox(8);
        logContainer.setPadding(new Insets(5, 5, 20, 5));

        scrollPane = new ScrollPane(logContainer);
        scrollPane.setPrefHeight(550);
        scrollPane.setFitToWidth(true);

        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 0;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        getChildren().addAll(headerBox, separateur, logTitre, scrollPane);
    }

    /**
     * Crée un texte stylisé avec la police du jeu et un effet optionnel de néon
     */
    private Text creerTexteStylise(String contenu, int taille, String couleurHex, boolean avecGlow) {
        Text t = new Text(contenu);
        t.setFont(FontUtils.getPolice(taille));
        t.setFill(Color.web(couleurHex));

        if (avecGlow) {
            DropShadow glow = new DropShadow(15, Color.web(couleurHex, 0.5));
            t.setEffect(glow);
        }
        return t;
    }

    public void setPhase(String phase) {
        texteTour.setText(phase);
    }

    public void setTexteManche(int numeroManche) {
        texteManche.setText("MANCHE : " + numeroManche);
    }

    public void ajouterLog(String message, String type) {
        Text logText = new Text("» " + message);
        logText.setFont(FontUtils.getPolice(14));
        logText.setWrappingWidth(270);

        switch (type.toUpperCase()) {
            case "INFO": logText.setFill(Color.web("#b0c4de")); break;
            case "TOUCHE": logText.setFill(Color.web("#ff3333")); break;
            case "RATE": logText.setFill(Color.web("#708090")); break;
            case "ALERTE": logText.setFill(Color.web("#ffcc00")); break;
            default: logText.setFill(Color.web("#ffffff"));
        }

        logContainer.getChildren().add(logText);

        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
}