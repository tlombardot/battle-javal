package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;
import school.coda.darill_thomas_louis.bataillejavale.utils.UIUtils;

import java.util.function.Consumer;

public class CustomInputDialogUI extends StackPane {

    public CustomInputDialogUI(Pane menuParent, String titreDialog, String promptMsg, Runnable actionAnnuler, Consumer<String> actionValider) {

        Rectangle fondOverlay = new Rectangle(1280, 720, Color.color(0, 0, 0, 0.85));
        this.getChildren().add(fondOverlay);

        VBox boiteCentrale = new VBox(25);
        boiteCentrale.setAlignment(Pos.CENTER);
        boiteCentrale.setPadding(new Insets(30, 40, 30, 40));
        boiteCentrale.setMaxSize(500, 250);
        boiteCentrale.setStyle("-fx-background-color: #0c121e; -fx-border-color: #00ffff; -fx-border-width: 2px; -fx-border-radius: 5; -fx-background-radius: 5;");
        boiteCentrale.setEffect(new DropShadow(20, Color.web("#00ffff", 0.4)));

        Text titre = new Text(titreDialog);
        titre.setFont(FontUtils.getPolice(22));
        titre.setFill(Color.web("#00ffff"));

        // le champ de texte
        TextField inputField = new TextField();
        inputField.setPromptText(promptMsg);
        inputField.setPrefWidth(350);
        inputField.setPrefHeight(40);
        inputField.setStyle("-fx-background-color: #05080c; -fx-text-fill: white; -fx-border-color: #555555; -fx-border-width: 1px; -fx-font-family: 'Consolas'; -fx-font-size: 16px;");

        inputField.focusedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                inputField.setStyle("-fx-background-color: #05080c; -fx-text-fill: white; -fx-border-color: #00ffff; -fx-border-width: 1px; -fx-font-family: 'Consolas'; -fx-font-size: 16px;");
            } else {
                inputField.setStyle("-fx-background-color: #05080c; -fx-text-fill: white; -fx-border-color: #555555; -fx-border-width: 1px; -fx-font-family: 'Consolas'; -fx-font-size: 16px;");
            }
        });

        HBox boxBoutons = new HBox(20);
        boxBoutons.setAlignment(Pos.CENTER);

        Button btnCancel = creerBouton("ANNULER", "#ff3333", () -> fermerDialog(menuParent, actionAnnuler));
        Button btnConfirm = creerBouton("CONFIRMER", "#00ffcc", () -> {
            String resultat = inputField.getText().trim();
            if (!resultat.isEmpty()) {
                fermerDialog(menuParent, () -> actionValider.accept(resultat));
            }
        });

        boxBoutons.getChildren().addAll(btnCancel, btnConfirm);
        boiteCentrale.getChildren().addAll(titre, inputField, boxBoutons);
        this.getChildren().add(boiteCentrale);

        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fermerDialog(menuParent, actionAnnuler);
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                String resultat = inputField.getText().trim();
                if (!resultat.isEmpty()) {
                    fermerDialog(menuParent, () -> actionValider.accept(resultat));
                }
                event.consume();
            }
        });

        this.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), this);
        ft.setToValue(1);
        ft.play();
        //focus auto sur notre champ
        Platform.runLater(inputField::requestFocus);
    }

    private Button creerBouton(String texte, String couleur, Runnable action) {
        Button btn = new Button(texte);
        btn.setFont(FontUtils.getPolice(14));
        btn.setPrefSize(150, 40);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + couleur + "; -fx-border-color: " + couleur + "; -fx-border-width: 1px; -fx-cursor: hand;");

        btn.setOnMouseEntered(_ -> btn.setStyle("-fx-background-color: " + couleur + "33; -fx-text-fill: " + couleur + "; -fx-border-color: " + couleur + "; -fx-border-width: 1px; -fx-cursor: hand;"));
        btn.setOnMouseExited(_ -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + couleur + "; -fx-border-color: " + couleur + "; -fx-border-width: 1px; -fx-cursor: hand;"));
        btn.setOnAction(_ -> action.run());
        return btn;
    }

    private void fermerDialog(Pane menuParent, Runnable actionApresFermeture) {
        UIUtils.fermerFenetre(this, menuParent, actionApresFermeture);
    }
}