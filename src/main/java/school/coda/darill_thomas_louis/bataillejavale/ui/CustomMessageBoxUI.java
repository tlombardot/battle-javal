package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;
import school.coda.darill_thomas_louis.bataillejavale.utils.UIUtils;

public class CustomMessageBoxUI extends StackPane {

    public CustomMessageBoxUI(Pane menuParent, String titreDialog, String message, Runnable actionApresOk) {

        Rectangle fondOverlay = new Rectangle(1280, 720, Color.color(0, 0, 0, 0.85));
        this.getChildren().add(fondOverlay);

        VBox boiteCentrale = new VBox(25);
        boiteCentrale.setAlignment(Pos.CENTER);
        boiteCentrale.setPadding(new Insets(30, 40, 30, 40));
        boiteCentrale.setMaxSize(450, 250);
        boiteCentrale.setStyle("-fx-background-color: #0c121e; -fx-border-color: #ff3333; -fx-border-width: 2px; -fx-border-radius: 5; -fx-background-radius: 5;");
        boiteCentrale.setEffect(new DropShadow(20, Color.web("#ff3333", 0.4)));

        Text titre = new Text(titreDialog);
        titre.setFont(FontUtils.getPolice(22));
        titre.setFill(Color.web("#ff3333"));

        Text texteMessage = new Text(message);
        texteMessage.setFont(FontUtils.getPolice(16));
        texteMessage.setFill(Color.WHITE);
        texteMessage.setWrappingWidth(380);
        texteMessage.setTextAlignment(TextAlignment.CENTER);

        Button btnOk = new Button("OK");
        btnOk.setFont(FontUtils.getPolice(14));
        btnOk.setPrefSize(120, 40);
        btnOk.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3333; -fx-border-color: #ff3333; -fx-border-width: 1px; -fx-cursor: hand;");
        btnOk.setOnMouseEntered(e -> btnOk.setStyle("-fx-background-color: #ff333333; -fx-text-fill: #ff3333; -fx-border-color: #ff3333; -fx-border-width: 1px; -fx-cursor: hand;"));
        btnOk.setOnMouseExited(e -> btnOk.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3333; -fx-border-color: #ff3333; -fx-border-width: 1px; -fx-cursor: hand;"));

        btnOk.setOnAction(_ -> UIUtils.fermerFenetre(this, menuParent, actionApresOk));


        boiteCentrale.getChildren().addAll(titre, texteMessage, btnOk);
        this.getChildren().add(boiteCentrale);

        this.setFocusTraversable(true);
        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                UIUtils.fermerFenetre(this, menuParent, actionApresOk);
                event.consume();
            }
        });

        this.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), this);
        ft.setToValue(1);
        ft.play();

        javafx.application.Platform.runLater(this::requestFocus);
    }
}