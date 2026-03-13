package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.core.model.AppPreferences;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.config.PreferencesManager;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.JoueurRepository;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

import java.util.Objects;

public class ParametresUI extends StackPane {

    private AppPreferences prefs;

    public ParametresUI(Pane menuParent, Runnable actionFermeture) {
        this.prefs = PreferencesManager.getInstance().getPreferences();

        setPrefSize(1280, 720);
        buildBackground();

        VBox conteneurGlobal = new VBox();
        conteneurGlobal.setPadding(new Insets(40, 60, 40, 60));

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        StackPane btnExit = creerBoutonExit(menuParent, actionFermeture);
        topBar.getChildren().add(btnExit);

        HBox titleBox = buildTitreBox("SYSTEM SETTINGS");
        VBox.setMargin(titleBox, new Insets(20, 0, 60, 0));

        HBox splitContent = new HBox(150);
        splitContent.setAlignment(Pos.CENTER);

        VBox colGauche = new VBox(40);
        colGauche.setAlignment(Pos.TOP_LEFT);

        VBox boxDb = buildSection("DATABASE CONNECTION");
        TextField champDb = creerChampTexteDesign(prefs.dbConnectionString);
        boxDb.getChildren().add(champDb);

        VBox boxAudio = buildSection("AUDIO SYSTEM");
        CheckBox checkSon = creerCheckBoxDesign("ACTIVER LE SON", prefs.sonActive);
        VBox sliderVolBox = creerSliderDesign("VOLUME MUSIQUE", prefs.volumeMusique);
        boxAudio.getChildren().addAll(checkSon, sliderVolBox);

        colGauche.getChildren().addAll(boxDb, boxAudio);

        VBox colDroite = new VBox(40);
        colDroite.setAlignment(Pos.TOP_LEFT);

        VBox boxModules = buildSection("GAMEPLAY MODULES");
        CheckBox checkRavi = creerCheckBoxDesign("MODULE : RAVITAILLEMENT", prefs.ravitaillementActive);
        CheckBox checkEvenements = creerCheckBoxDesign("MODULE : ÉVÉNEMENTS ALÉATOIRES", prefs.evenementsActive);
        boxModules.getChildren().addAll(checkRavi, checkEvenements);

        colDroite.getChildren().add(boxModules);

        splitContent.getChildren().addAll(colGauche, colDroite);

        VBox bottomBox = new VBox();
        bottomBox.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setMargin(bottomBox, new Insets(80, 0, 0, 0));
        Button btnSave = creerBoutonSave(() -> {

            prefs.dbConnectionString = champDb.getText();
            prefs.sonActive = checkSon.isSelected();

            prefs.volumeMusique = ((Slider) sliderVolBox.getChildren().get(1)).getValue();
            prefs.ravitaillementActive = checkRavi.isSelected();
            prefs.evenementsActive = checkEvenements.isSelected();

            PreferencesManager.getInstance().sauvegarderPreferences();

            JoueurRepository repo = new JoueurRepository();
            repo.sauvegarderPreferencesDansCloud(prefs);

            fermerFenetre(menuParent, actionFermeture);
        });
        bottomBox.getChildren().add(btnSave);


        conteneurGlobal.getChildren().addAll(topBar, titleBox, splitContent, bottomBox);
        this.getChildren().add(conteneurGlobal);


        this.setOpacity(0);
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.seconds(0.4), this);
        ft.setToValue(1);
        ft.play();
    }

    private void buildBackground() {
        try {
            ImageView bgImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/assets/textures/naval_ocean.jpg")).toExternalForm()));
            bgImage.setFitWidth(1280);
            bgImage.setFitHeight(720);
            bgImage.setEffect(new GaussianBlur(10));
            this.getChildren().add(bgImage);
        } catch (Exception e) {
            this.getChildren().add(new Rectangle(1280, 720, Color.web("#050810")));
        }

        Rectangle filtreNoir = new Rectangle(1280, 720, Color.color(0.02, 0.05, 0.1, 0.75));
        this.getChildren().add(filtreNoir);
    }

    private HBox buildTitreBox(String text) {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        Line ligneGauche = new Line(0, 0, 200, 0);
        ligneGauche.setStroke(Color.web("#ffffff", 0.3));

        Text titre = new Text(text);
        titre.setFont(FontUtils.getPolice(24));
        titre.setFill(Color.web("#ffffff", 0.8));

        Line ligneDroite = new Line(0, 0, 200, 0);
        ligneDroite.setStroke(Color.web("#ffffff", 0.3));

        box.getChildren().addAll(ligneGauche, titre, ligneDroite);
        return box;
    }

    private VBox buildSection(String title) {
        VBox section = new VBox(20);
        Text t = new Text(title);
        t.setFont(FontUtils.getPolice(16));
        t.setFill(Color.web("#00ffff"));

        Line separator = new Line(0, 0, 350, 0);
        separator.setStroke(Color.web("#ffffff", 0.2));

        section.getChildren().addAll(t, separator);
        return section;
    }

    private TextField creerChampTexteDesign(String valeurDefaut) {
        TextField tf = new TextField(valeurDefaut);
        tf.setPrefWidth(350);
        tf.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: transparent transparent #00ffff transparent; -fx-border-width: 0 0 1 0; -fx-font-family: 'Consolas'; -fx-font-size: 14px;");
        return tf;
    }

    private CheckBox creerCheckBoxDesign(String text, boolean selected) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(selected);
        cb.setTextFill(Color.WHITE);
        cb.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px;");
        return cb;
    }

    private VBox creerSliderDesign(String text, double valDefaut) {
        VBox box = new VBox(10);
        Text label = new Text(text);
        label.setFont(FontUtils.getPolice(14));
        label.setFill(Color.WHITE);

        Slider slider = new Slider(0, 1, valDefaut);
        slider.setPrefWidth(350);
        slider.setStyle("-fx-control-inner-background: #111; -fx-accent: #00ffff;");

        box.getChildren().addAll(label, slider);
        return box;
    }

    private StackPane creerBoutonExit(Pane menuParent, Runnable actionFermeture) {
        StackPane conteneurExit = new StackPane();
        conteneurExit.setStyle("-fx-cursor: hand;");

        HBox groupe = new HBox(10);
        groupe.setAlignment(Pos.CENTER);

        Text textEsc = new Text("ESC");
        textEsc.setFont(FontUtils.getPolice(16));
        textEsc.setFill(Color.web("#a0a0a0"));

        StackPane boxCroix = new StackPane();
        Rectangle fondCroix = new Rectangle(30, 30, Color.TRANSPARENT);
        fondCroix.setStroke(Color.web("#a0a0a0"));
        fondCroix.setStrokeWidth(1);

        Line l1 = new Line(-8, -8, 8, 8);
        l1.setStroke(Color.web("#a0a0a0"));
        l1.setStrokeWidth(2);

        Line l2 = new Line(-8, 8, 8, -8);
        l2.setStroke(Color.web("#a0a0a0"));
        l2.setStrokeWidth(2);

        boxCroix.getChildren().addAll(fondCroix, l1, l2);
        groupe.getChildren().addAll(textEsc, boxCroix);
        conteneurExit.getChildren().add(groupe);

        RotateTransition rotAnim = new RotateTransition(Duration.seconds(0.3), boxCroix);
        ScaleTransition scaleAnim = new ScaleTransition(Duration.seconds(0.2), textEsc);

        conteneurExit.setOnMouseEntered(e -> {
            textEsc.setFill(Color.WHITE);
            fondCroix.setStroke(Color.WHITE);
            l1.setStroke(Color.web("#00ffff")); // Devient cyan
            l2.setStroke(Color.web("#00ffff"));

            rotAnim.setToAngle(90);
            rotAnim.play();

            scaleAnim.setToX(1.1); scaleAnim.setToY(1.1);
            scaleAnim.play();
        });

        conteneurExit.setOnMouseExited(e -> {
            textEsc.setFill(Color.web("#a0a0a0"));
            fondCroix.setStroke(Color.web("#a0a0a0"));
            l1.setStroke(Color.web("#a0a0a0"));
            l2.setStroke(Color.web("#a0a0a0"));

            rotAnim.setToAngle(0);
            rotAnim.play();

            scaleAnim.setToX(1.0); scaleAnim.setToY(1.0);
            scaleAnim.play();
        });

        conteneurExit.setOnMouseClicked(e -> fermerFenetre(menuParent, actionFermeture));

        return conteneurExit;
    }

    private Button creerBoutonSave(Runnable action) {
        Button btn = new Button("APPLY SETTINGS");
        btn.setFont(FontUtils.getPolice(18));
        btn.setPrefSize(250, 45);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00ffff; -fx-border-color: #00ffff; -fx-border-width: 1px; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: #00ffff; -fx-text-fill: #0a0f18; -fx-border-color: #00ffff; -fx-border-width: 1px; -fx-cursor: hand;");
            btn.setEffect(new DropShadow(15, Color.web("#00ffff")));
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00ffff; -fx-border-color: #00ffff; -fx-border-width: 1px; -fx-cursor: hand;");
            btn.setEffect(null);
        });

        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void fermerFenetre(Pane menuParent, Runnable actionFermeture) {
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.seconds(0.3), this);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            menuParent.getChildren().remove(this);
            actionFermeture.run();
        });
        ft.play();
    }
}