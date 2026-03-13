package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.core.model.AppPreferences;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.config.PreferencesManager;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

public class ParametresUI extends StackPane {

    public ParametresUI(Pane menuParent, Runnable actionFermeture) {
        // 1. Charger les préférences
        AppPreferences prefs = PreferencesManager.getInstance().getPreferences();

        // 2. Créer l'overlay sombre (fond)
        setPrefSize(1280, 720);
        Rectangle fondNoir = new Rectangle(1280, 720, Color.color(0, 0, 0, 0.85));

        // 3. Conteneur principal de la fenêtre des paramètres
        VBox conteneur = new VBox(20);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setMaxSize(650, 550);
        conteneur.setStyle("-fx-background-color: #0c121e; -fx-border-color: #00ffff; -fx-border-width: 2px; -fx-padding: 30; -fx-border-radius: 8; -fx-background-radius: 8;");
        conteneur.setEffect(new DropShadow(30, Color.web("#00ffff", 0.5)));

        // Titre
        Text titre = new Text("SYSTEM SETTINGS");
        titre.setFont(FontUtils.getPolice(32));
        titre.setFill(Color.web("#00ffff"));

        // 4. Formulaire (Grille)
        GridPane grille = new GridPane();
        grille.setVgap(20);
        grille.setHgap(20);
        grille.setAlignment(Pos.CENTER);

        // --- BDD ---
        Text txtDb = new Text("BASE DE DONNÉES (JDBC) :");
        txtDb.setFill(Color.WHITE);
        txtDb.setFont(FontUtils.getPolice(14));
        TextField champDb = new TextField(prefs.dbConnectionString);
        champDb.setPrefWidth(350);
        champDb.setStyle("-fx-background-color: #11151c; -fx-text-fill: #00ffff; -fx-border-color: #4fc3f7;");

        // --- AUDIO ---
        CheckBox checkSon = new CheckBox("ACTIVER LE SYSTÈME AUDIO");
        checkSon.setTextFill(Color.WHITE);
        checkSon.setSelected(prefs.sonActive);

        Text txtVol = new Text("VOLUME MUSIQUE :");
        txtVol.setFill(Color.WHITE);
        txtVol.setFont(FontUtils.getPolice(14));
        Slider sliderMusique = new Slider(0, 1, prefs.volumeMusique);

        // --- MODULES ---
        CheckBox checkRavitaillement = new CheckBox("MODULE : RAVITAILLEMENT");
        checkRavitaillement.setTextFill(Color.WHITE);
        checkRavitaillement.setSelected(prefs.ravitaillementActive);

        CheckBox checkEvenements = new CheckBox("MODULE : ÉVÉNEMENTS ALÉATOIRES");
        checkEvenements.setTextFill(Color.WHITE);
        checkEvenements.setSelected(prefs.evenementsActive);

        // Ajout à la grille
        grille.add(txtDb, 0, 0); grille.add(champDb, 1, 0);
        grille.add(checkSon, 0, 1, 2, 1);
        grille.add(txtVol, 0, 2); grille.add(sliderMusique, 1, 2);
        grille.add(checkRavitaillement, 0, 3, 2, 1);
        grille.add(checkEvenements, 0, 4, 2, 1);

        // 5. Boutons d'action
        Button btnSauvegarder = creerBouton("SAUVEGARDER", "#00ffcc");
        btnSauvegarder.setOnAction(e -> {
            prefs.dbConnectionString = champDb.getText();
            prefs.sonActive = checkSon.isSelected();
            prefs.volumeMusique = sliderMusique.getValue();
            prefs.ravitaillementActive = checkRavitaillement.isSelected();
            prefs.evenementsActive = checkEvenements.isSelected();

            PreferencesManager.getInstance().sauvegarderPreferences();
            fermerFenetre(menuParent, actionFermeture);
        });

        Button btnAnnuler = creerBouton("ANNULER", "#ff3333");
        btnAnnuler.setOnAction(e -> fermerFenetre(menuParent, actionFermeture));

        HBox boxBoutons = new HBox(20, btnSauvegarder, btnAnnuler);
        boxBoutons.setAlignment(Pos.CENTER);

        conteneur.getChildren().addAll(titre, grille, boxBoutons);
        this.getChildren().addAll(fondNoir, conteneur);

        // Animation d'apparition
        this.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.3), this);
        ft.setToValue(1);
        ft.play();
    }

    private void fermerFenetre(Pane menuParent, Runnable actionFermeture) {
        menuParent.getChildren().remove(this);
        actionFermeture.run(); // Réactive les boutons du menu principal
    }

    private Button creerBouton(String texte, String couleurHex) {
        Button btn = new Button(texte);
        btn.setFont(FontUtils.getPolice(16));
        btn.setPrefSize(200, 40);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + couleurHex + "; -fx-border-color: " + couleurHex + "; -fx-border-width: 2px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + couleurHex + "; -fx-text-fill: #11151c; -fx-border-color: " + couleurHex + "; -fx-border-width: 2px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + couleurHex + "; -fx-border-color: " + couleurHex + "; -fx-border-width: 2px; -fx-cursor: hand;"));
        return btn;
    }
}