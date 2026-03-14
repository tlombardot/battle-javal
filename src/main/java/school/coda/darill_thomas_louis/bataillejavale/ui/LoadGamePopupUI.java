package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import school.coda.darill_thomas_louis.bataillejavale.core.model.EtatJeu;
import school.coda.darill_thomas_louis.bataillejavale.core.model.ModeJeu;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.PartieRepository;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;
import school.coda.darill_thomas_louis.bataillejavale.utils.UIUtils;

import java.util.List;
import java.util.function.BiConsumer;

public class LoadGamePopupUI extends StackPane {

    private final BiConsumer<EtatJeu, Integer> actionChargerPartie;

    public LoadGamePopupUI(Pane menuParent, Runnable actionAnnuler, BiConsumer<EtatJeu, Integer> actionChargerPartie) {
        this.actionChargerPartie = actionChargerPartie;

        Rectangle bg = new Rectangle(1280, 720, Color.color(0, 0, 0, 0.85));
        this.getChildren().add(bg);

        VBox conteneur = new VBox(20);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setMaxSize(650, 550);
        conteneur.setStyle("-fx-background-color: #0c121e; -fx-border-color: #00ffff; -fx-border-width: 2px; -fx-padding: 30; -fx-border-radius: 8; -fx-background-radius: 8;");
        conteneur.setEffect(new DropShadow(30, Color.web("#00ffff", 0.5)));

        Text titre = new Text("ARCHIVES SATELLITES");
        titre.setFont(FontUtils.getPolice(32));
        titre.setFill(Color.web("#00ffff"));

        //listes des saves
        VBox listeSaves = new VBox(15);
        listeSaves.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(listeSaves);
        scroll.setPrefHeight(400);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Sécurité supplémentaire

        chargerSauvegardes(listeSaves);

        Button btnFermer = new Button("FERMER LES ARCHIVES");
        btnFermer.setFont(FontUtils.getPolice(18));
        btnFermer.setPrefSize(250, 45);
        btnFermer.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3333; -fx-border-color: #ff3333; -fx-border-width: 2px; -fx-cursor: hand;");

        btnFermer.setOnMouseEntered(_ -> btnFermer.setStyle("-fx-background-color: #ff3333; -fx-text-fill: #eeeeee; -fx-border-color: #ff3333; -fx-border-width: 2px; -fx-cursor: hand;"));
        btnFermer.setOnMouseExited(_ -> btnFermer.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3333; -fx-border-color: #ff3333; -fx-border-width: 2px; -fx-cursor: hand;"));

        btnFermer.setOnAction(_ -> UIUtils.fermerFenetre(this, menuParent, actionAnnuler));

        conteneur.getChildren().addAll(titre, scroll, btnFermer);
        this.getChildren().addAll(conteneur);

        this.setFocusTraversable(true);
        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                UIUtils.fermerFenetre(this, menuParent, actionAnnuler);
                event.consume();
            }
        });

        this.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.3), this);
        ft.setToValue(1);
        ft.play();

        javafx.application.Platform.runLater(this::requestFocus);
    }

    private void chargerSauvegardes(VBox listeSaves) {
        PartieRepository repo = new PartieRepository();
        List<PartieRepository.PartieInfo> saves = repo.getListePartiesJoueur();

        if (saves.isEmpty()) {
            Text vide = new Text("AUCUNE DONNÉE TROUVÉE DANS LE CLOUD.");
            vide.setFill(Color.web("#a0a0a0"));
            vide.setFont(FontUtils.getPolice(18));
            listeSaves.getChildren().add(vide);
            return;
        }

        for (PartieRepository.PartieInfo info : saves) {
            StackPane carte = new StackPane();
            Rectangle fondCarte = new Rectangle(550, 70, Color.web("#11151c"));
            fondCarte.setStroke(Color.web(info.statut().equals("EN_COURS") ? "#4fc3f7" : "#555555"));
            fondCarte.setStrokeWidth(2);
            fondCarte.setArcWidth(8); fondCarte.setArcHeight(8);

            Text txtHaut = new Text("MISSION #" + info.id() + "  |  TOUR : " + info.tour());
            txtHaut.setFont(FontUtils.getPolice(20));
            txtHaut.setFill(Color.WHITE);
            txtHaut.setTranslateY(-12);

            Text txtBas = new Text("STATUT : " + info.statut() + "  |  DATE : " + info.date());
            txtBas.setFont(FontUtils.getPolice(14));
            txtBas.setFill(info.statut().equals("EN_COURS") ? Color.web("#00ffcc") : Color.web("#ffaa00"));
            txtBas.setTranslateY(15);

            carte.getChildren().addAll(fondCarte, txtHaut, txtBas);
            carte.setStyle("-fx-cursor: hand;");

            carte.setOnMouseEntered(_ -> fondCarte.setFill(Color.web("#1e2b40")));
            carte.setOnMouseExited(_ -> fondCarte.setFill(Color.web("#11151c")));

            carte.setOnMouseClicked(_ -> {
                EtatJeu sauvegarde = repo.chargerPartieActiveOuTerminee(info.id());
                if (sauvegarde != null) {
                    actionChargerPartie.accept(sauvegarde, info.id());
                }
            });

            listeSaves.getChildren().add(carte);
        }
    }
}