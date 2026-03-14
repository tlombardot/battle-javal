package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import school.coda.darill_thomas_louis.bataillejavale.core.model.AppPreferences;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.config.PreferencesManager;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

public class PreGamePopupUI extends StackPane {

    private final AppPreferences prefsGlobales;

    public PreGamePopupUI(Pane menuParent, Runnable actionFermeture, Runnable actionCreerPartie) {
        this.prefsGlobales = PreferencesManager.getInstance().getPreferences();

        Rectangle fondGris = new Rectangle(1280, 720, Color.color(0, 0, 0, 0.8));
        fondGris.setOnMouseClicked(_ -> fermerPopup(menuParent, actionFermeture));
        this.getChildren().add(fondGris);

        VBox boiteCentrale = new VBox(25);
        boiteCentrale.setAlignment(Pos.TOP_CENTER);
        boiteCentrale.setPadding(new Insets(30, 50, 30, 50));
        boiteCentrale.setMaxSize(500, 450); // Taille fixe de la fenêtre modale

        boiteCentrale.setStyle("-fx-background-color: #050810; -fx-border-color: #00ffff; -fx-border-width: 2px;");
        boiteCentrale.setEffect(new DropShadow(20, Color.color(0, 1, 1, 0.3)));

        Text titre = new Text("HOST MATCH CONFIGURATION");
        titre.setStyle("-fx-font-size: 20px; -fx-fill: white; -fx-letter-spacing: 2px;");
        Line separateur = new Line(0, 0, 400, 0);
        separateur.setStroke(Color.web("#00ffff", 0.5));

        // pour la taille de la grille
        VBox boxGrille = new VBox(10);
        boxGrille.setAlignment(Pos.CENTER_LEFT);
        Text labelGrille = new Text("TAILLE DE LA GRILLE (GRILLE CARRÉE)");
        labelGrille.setStyle("-fx-font-size: 14px; -fx-fill: #00ffff;");

        ComboBox<String> comboTaille = new ComboBox<>();
        comboTaille.getItems().addAll("10 x 10 (Classique)", "15 x 15 (Tactique)", "20 x 20 (Guerre Totale)");
        comboTaille.getSelectionModel().select(0);
        comboTaille.setPrefWidth(400);
        comboTaille.setStyle("-fx-background-color: #111; -fx-text-fill: white; -fx-border-color: #555;");
        boxGrille.getChildren().addAll(labelGrille, comboTaille);

        VBox boxModules = new VBox(15);
        boxModules.setAlignment(Pos.CENTER_LEFT);
        Text labelModules = new Text("MODULES ACTIFS POUR CETTE PARTIE");
        labelModules.setStyle("-fx-font-size: 14px; -fx-fill: #00ffff;");

        CheckBox checkRavi = creerCheckBoxDesign("MODULE : RAVITAILLEMENT", prefsGlobales.ravitaillementActive);
        CheckBox checkEvents = creerCheckBoxDesign("MODULE : ÉVÉNEMENTS ALÉATOIRES", prefsGlobales.evenementsActive);
        boxModules.getChildren().addAll(labelModules, checkRavi, checkEvents);

        HBox boxBoutons = new HBox(20);
        boxBoutons.setAlignment(Pos.CENTER);
        VBox.setMargin(boxBoutons, new Insets(20, 0, 0, 0));

        Button btnCancel = creerBoutonSecondaire( () -> fermerPopup(menuParent, actionFermeture));

        Button btnCreate = creerBoutonPrincipal(() -> {
            System.out.println("Taille choisie : " + comboTaille.getValue());
            System.out.println("Ravitaillement : " + checkRavi.isSelected());

            fermerPopup(menuParent, actionCreerPartie);
        });

        boxBoutons.getChildren().addAll(btnCancel, btnCreate);

        boiteCentrale.getChildren().addAll(titre, separateur, boxGrille, boxModules, boxBoutons);
        this.getChildren().add(boiteCentrale);

        // animation d'entrée
        this.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), this);
        ft.setToValue(1);
        ft.play();
    }

    private CheckBox creerCheckBoxDesign(String text, boolean selected) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(selected);
        cb.setTextFill(Color.WHITE);
        cb.setFont(FontUtils.getPolice(14));
        cb.setStyle("-fx-cursor: hand;");
        return cb;
    }

    private Button creerBoutonPrincipal(Runnable action) {
        Button btn = new Button("CRÉER LE SALON");
        btn.setPrefSize(200, 40);
        btn.setFont(FontUtils.getPolice(16));
        btn.setStyle("-fx-background-color: #00ffff; -fx-text-fill: #050810; -fx-font-weight: bold; -fx-cursor: hand;");
        btn.setOnAction(_ -> action.run());
        return btn;
    }

    private Button creerBoutonSecondaire(Runnable action) {
        Button btn = new Button("ANNULER");
        btn.setPrefSize(150, 40);
        btn.setFont(FontUtils.getPolice(16));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a0a0a0; -fx-border-color: #a0a0a0; -fx-cursor: hand;");
        btn.setOnMouseEntered(_ -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-border-color: white; -fx-cursor: hand;"));
        btn.setOnMouseExited(_ -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a0a0a0; -fx-border-color: #a0a0a0; -fx-cursor: hand;"));
        btn.setOnAction(_ -> action.run());
        return btn;
    }

    private void fermerPopup(Pane menuParent, Runnable actionApresFermeture) {
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), this);
        ft.setToValue(0);
        ft.setOnFinished(_ -> {
            menuParent.getChildren().remove(this);
            if (actionApresFermeture != null) {
                actionApresFermeture.run();
            }
        });
        ft.play();
    }
}