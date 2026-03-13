package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.stage.Popup;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;
import school.coda.darill_thomas_louis.bataillejavale.utils.FontUtils;

import javax.swing.*;

public class VaisseauUI extends Pane {

    // --- PALETTE DE COULEURS : COQUE BLEUE, DÉTAILS BLANCS ---

    // Bordures principales du navire (Bleu cyan)
    private static final Color BORDER_PRIMARY = Color.web("#4fc3f7");

    // Remplissage de la coque (Bleu profond semi-transparent)
    private static final Color HULL_FILL = Color.web("#0277bd", 0.55);

    // Remplissage des superstructures (Bleu foncé, un peu plus dense)
    private static final Color SUPERSTRUCTURE_FILL = Color.web("#01579b", 0.65);

    // --- NOUVEAU : Détails et accents en BLANC ---
    // Lignes de ponts, écoutilles, contours internes (Blanc à 75% d'opacité)
    private static final Color DETAIL_LINE = Color.web("#ffffff", 0.75);

    // Armement, antennes, centre des radars (Blanc pur éclatant)
    private static final Color ACCENT = Color.web("#ffffff", 1.0);

    private static final double CELL = 40.0;

    private final Vaisseau navire;
    private boolean estHorizontal = true;
    private final DropShadow glowEffect;
    private Group dessinHolographique;

    public VaisseauUI(Vaisseau navire) {
        this.navire = navire;

        glowEffect = new DropShadow(8, Color.web("#4fc3f7", 0.4));
        glowEffect.setSpread(0.2);

        dessinerBlueprint();

        dessinHolographique.setEffect(glowEffect);

        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                forcerOrientation(!estHorizontal);
            }
        });

        Popup popupInfos = creerBulleInfo(navire.getNom());

        this.setOnMouseEntered(e -> {
            this.setOpacity(1.0);
            popupInfos.show(this.getScene().getWindow(), e.getScreenX(), e.getScreenY());
            popupInfos.setX(e.getScreenX() - popupInfos.getWidth() / 2);
            popupInfos.setY(e.getScreenY() - popupInfos.getHeight() - 10);
        });

        this.setOnMouseMoved(e -> {
            if (popupInfos.isShowing()) {
                popupInfos.setX(e.getScreenX() - popupInfos.getWidth() / 2);
                popupInfos.setY(e.getScreenY() - popupInfos.getHeight() - 10);
            }
        });

        this.setOnMouseExited(e -> {
            this.setOpacity(0.85);
            popupInfos.hide();
        });

        this.setOnDragDetected(event -> {
            popupInfos.hide();
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(navire.getNom() + ";" + estHorizontal);
            db.setContent(content);
            event.consume();
        });

        this.setStyle("-fx-cursor: hand;");
        this.setOpacity(0.90);
    }

    public void forcerOrientation(boolean horizontal) {
        this.estHorizontal = horizontal;
        this.getTransforms().clear();

        if (!horizontal) {
            double pivotX = CELL / 2.0;
            double pivotY = CELL / 2.0;

            this.getTransforms().add(new Rotate(90, pivotX, pivotY));
        }
    }

    private Popup creerBulleInfo(String nom) {
        Popup popup = new Popup();

        VBox conteneur = new VBox();
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setMouseTransparent(true);

        Text texte = new Text(nom.toUpperCase());
        texte.setFont(FontUtils.getPolice(14));
        texte.setFill(Color.WHITE);

        StackPane fond = new StackPane(texte);
        fond.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-background-radius: 4; -fx-border-color: #4fc3f7; -fx-border-radius: 4; -fx-padding: 4 10 4 10;");

        Polygon pointe = new Polygon(0.0, 0.0, 12.0, 0.0, 6.0, 6.0);
        pointe.setFill(Color.web("#4fc3f7"));

        conteneur.getChildren().addAll(fond, pointe);
        popup.getContent().add(conteneur);

        return popup;
    }

    private void dessinerBlueprint() {
        this.getChildren().clear();
        dessinHolographique = new Group();

        switch (navire.getTaille()) {
            case 5 -> dessinerPorteAvions();
            case 4 -> dessinerCuirasse();
            case 3 -> {
                String nom = navire.getNom().toLowerCase();
                if (nom.contains("sous") || nom.contains("sub")) dessinerSousMarin();
                else dessinerCroiseur();
            }
            case 2 -> {
                String nom = navire.getNom().toLowerCase();
                if (nom.contains("patrol") || nom.contains("patrouil")) dessinerPatrouilleur();
                else dessinerDestroyer();
            }
            default -> dessinerTorpilleur();
        }

        this.setPrefSize(navire.getTaille() * CELL, CELL);
        this.setMinSize(navire.getTaille() * CELL, CELL);
        this.setMaxSize(navire.getTaille() * CELL, CELL);
        this.getChildren().add(dessinHolographique);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MÉTHODES DE DESSINS
    // ══════════════════════════════════════════════════════════════════════

    private void dessinerPorteAvions() {
        double L = 5 * CELL; double H = CELL; double cy = H / 2;
        Polygon coque = new Polygon(0, cy - 12, L - 40, cy - 14, L, cy, L - 40, cy + 14, 0, cy + 12); styleHull(coque);
        Rectangle pont = new Rectangle(4, cy - 10, L - 20, 20); pont.setArcWidth(4); pont.setArcHeight(4); styleSuperstructure(pont);
        Line axe = axeDash(5, cy, L - 15, cy);
        Group sections = new Group();
        for (double x : new double[]{30, 60, 90, 120}) { Line s = new Line(x, cy - 9, x, cy + 9); styleDetailLine(s); sections.getChildren().add(s); }
        Line cat1 = new Line(5, cy - 6, 100, cy - 6); Line cat2 = new Line(5, cy + 6, 100, cy + 6); cat1.setStroke(ACCENT); cat1.setStrokeWidth(1.2); cat2.setStroke(ACCENT); cat2.setStrokeWidth(1.2);
        Rectangle ile = new Rectangle(105, cy - 14, 32, 18); ile.setArcWidth(3); ile.setArcHeight(3); styleHull(ile);
        Rectangle ileDetail1 = new Rectangle(108, cy - 11, 10, 6); ileDetail1.setArcWidth(2); ileDetail1.setArcHeight(2); styleSuperstructure(ileDetail1);
        Rectangle ileDetail2 = new Rectangle(122, cy - 11, 10, 6); ileDetail2.setArcWidth(2); ileDetail2.setArcHeight(2); styleSuperstructure(ileDetail2);
        Rectangle asc1 = new Rectangle(6, cy - 9, 12, 9); Rectangle asc2 = new Rectangle(88, cy - 9, 12, 9); Rectangle asc3 = new Rectangle(88, cy, 12, 9);
        styleDetailShape(asc1); styleDetailShape(asc2); styleDetailShape(asc3);
        Line ant1 = makeLine(112, cy - 14, 122, cy - 22); Line ant2 = makeLine(118, cy - 14, 130, cy - 25); styleDetailLine(ant1); styleDetailLine(ant2);
        dessinHolographique.getChildren().addAll(coque, pont, axe, sections, cat1, cat2, ile, ileDetail1, ileDetail2, asc1, asc2, asc3, ant1, ant2);
    }

    private void dessinerCuirasse() {
        double L = 4 * CELL; double cy = CELL / 2;
        Polygon coque = new Polygon(0, cy - 14, L - 26, cy - 18, L, cy, L - 26, cy + 18, 0, cy + 14); styleHull(coque);
        Polygon pont = new Polygon(4, cy - 10, L - 28, cy - 14, L - 6, cy, L - 28, cy + 14, 4, cy + 10); styleSuperstructure(pont);
        Line axe = axeDash(2, cy, L - 4, cy);
        Group tav = tourelleDual(L - 20, cy, 13, true); Group tmav = tourelleSimple(L * 0.55, cy - 8, 10, true, -20);
        Group tmar = tourelleSimple(L * 0.55, cy + 8, 10, true, 20); Group tar = tourelleDual(20, cy, 12, false);
        Ellipse ch1 = makeEllipse(L * 0.38, cy, 6, 4); Ellipse ch2 = makeEllipse(L * 0.28, cy, 6, 4); styleDetailShape(ch1); styleDetailShape(ch2);
        Rectangle super1 = new Rectangle(L * 0.28, cy - 8, 34, 16); super1.setArcWidth(3); super1.setArcHeight(3); styleSuperstructure(super1);
        Line ant1 = makeLine(L - 18, cy - 2, L - 3, cy - 10); Line ant2 = makeLine(3, cy - 2, -4, cy - 8); styleDetailLine(ant1); styleDetailLine(ant2);
        dessinHolographique.getChildren().addAll(coque, pont, axe, super1, ch1, ch2, tav, tmav, tmar, tar, ant1, ant2);
    }

    private void dessinerCroiseur() {
        double L = 3 * CELL; double cy = CELL / 2;
        Polygon coque = new Polygon(0, cy - 12, L - 20, cy - 16, L, cy, L - 20, cy + 16, 0, cy + 12); styleHull(coque);
        Polygon pont = new Polygon(4, cy - 8, L - 22, cy - 12, L - 5, cy, L - 22, cy + 12, 4, cy + 8); styleSuperstructure(pont);
        Line axe = axeDash(3, cy, L - 5, cy);
        Group tav = tourelleDual(L - 18, cy, 12, true); Group tar = tourelleDual(18, cy, 11, false);
        Rectangle sup = new Rectangle(L * 0.38, cy - 9, 26, 18); sup.setArcWidth(3); sup.setArcHeight(3); styleSuperstructure(sup);
        Rectangle det1 = new Rectangle(L * 0.38 + 3, cy - 6, 10, 5); Rectangle det2 = new Rectangle(L * 0.38 + 3, cy + 1, 10, 5);
        det1.setArcWidth(1); det1.setArcHeight(1); det2.setArcWidth(1); det2.setArcHeight(1); styleDetailShape(det1); styleDetailShape(det2);
        Ellipse ch = makeEllipse(L * 0.58, cy, 6, 4); styleDetailShape(ch);
        Line ant = makeLine(L - 14, cy - 2, L - 3, cy - 8); styleDetailLine(ant);
        dessinHolographique.getChildren().addAll(coque, pont, axe, sup, det1, det2, ch, tav, tar, ant);
    }

    private void dessinerSousMarin() {
        double L = 3 * CELL; double cy = CELL / 2;
        Polygon coque = new Polygon(0, cy, 8, cy - 8, 25, cy - 14, 60, cy - 14, L - 6, cy - 10, L, cy, L - 6, cy + 10, 60, cy + 14, 25, cy + 14, 8, cy + 8); styleHull(coque);
        Polygon interieur = new Polygon(6, cy, 14, cy - 5, 26, cy - 10, 60, cy - 10, L - 8, cy - 7, L - 4, cy, L - 8, cy + 7, 60, cy + 10, 26, cy + 10, 14, cy + 5); styleSuperstructure(interieur);
        Line axe = axeDash(4, cy, L - 4, cy);
        Rectangle kiosque = new Rectangle(L * 0.40, cy - 14, 18, 28); kiosque.setArcWidth(4); kiosque.setArcHeight(4); styleHull(kiosque);
        Rectangle ecoutille = new Rectangle(L * 0.40 + 3, cy - 9, 9, 7); ecoutille.setArcWidth(2); ecoutille.setArcHeight(2); styleDetailShape(ecoutille);
        Circle peri = new Circle(L * 0.40 + 9, cy + 4, 4); styleDetailShape(peri);
        Circle periDot = new Circle(L * 0.40 + 9, cy + 4, 1.5); periDot.setFill(ACCENT); periDot.setStroke(Color.TRANSPARENT);
        Circle tt1 = new Circle(L - 16, cy - 7, 4.5); Circle tt2 = new Circle(L - 16, cy + 7, 4.5); styleDetailShape(tt1); styleDetailShape(tt2);
        Circle c1 = new Circle(L - 16, cy - 7, 2); Circle c2 = new Circle(L - 16, cy + 7, 2); styleDetailShape(c1); styleDetailShape(c2);
        Arc helice1 = new Arc(6, cy, 5, 8, 30, 120); Arc helice2 = new Arc(6, cy, 5, 8, 210, 120); helice1.setType(ArcType.OPEN); helice2.setType(ArcType.OPEN); styleDetailLine(helice1); styleDetailLine(helice2);
        Circle moyeu = new Circle(6, cy, 2.5); styleDetailShape(moyeu);
        Line gouv1 = makeLine(8, cy - 8, 2, cy - 16); Line gouv2 = makeLine(8, cy + 8, 2, cy + 16); styleDetailLine(gouv1); styleDetailLine(gouv2);
        dessinHolographique.getChildren().addAll(coque, interieur, axe, kiosque, ecoutille, peri, periDot, tt1, tt2, c1, c2, helice1, helice2, moyeu, gouv1, gouv2);
    }

    private void dessinerDestroyer() {
        double L = 2 * CELL; double cy = CELL / 2;
        Polygon coque = new Polygon(0, cy, 4, cy - 12, 20, cy - 14, L - 10, cy - 14, L, cy, L - 10, cy + 14, 20, cy + 14, 4, cy + 12); styleHull(coque);
        Polygon pont = new Polygon(5, cy, 9, cy - 8, 22, cy - 10, L - 12, cy - 10, L - 3, cy, L - 12, cy + 10, 22, cy + 10, 9, cy + 8); styleSuperstructure(pont);
        Line axe = axeDash(3, cy, L - 3, cy);
        Group tav = tourelleDual(L - 14, cy, 11, true); Group tar = tourelleSimple(16, cy, 9, false, 0);
        Rectangle bridge = new Rectangle(L * 0.33, cy - 8, 20, 16); bridge.setArcWidth(3); bridge.setArcHeight(3); styleSuperstructure(bridge);
        Ellipse ch = makeEllipse(L * 0.46, cy, 5, 4); styleDetailShape(ch);
        Rectangle lm1 = new Rectangle(L * 0.25, cy - 14, 7, 4); Rectangle lm2 = new Rectangle(L * 0.25, cy + 10, 7, 4); lm1.setArcWidth(1); lm1.setArcHeight(1); lm2.setArcWidth(1); lm2.setArcHeight(1); styleDetailShape(lm1); styleDetailShape(lm2);
        dessinHolographique.getChildren().addAll(coque, pont, axe, bridge, ch, lm1, lm2, tav, tar);
    }

    private void dessinerPatrouilleur() {
        double L = 2 * CELL; double cy = CELL / 2;
        Polygon coque = new Polygon(0, cy, 4, cy - 14, L - 16, cy - 16, L, cy, L - 16, cy + 16, 4, cy + 14); styleHull(coque);
        Polygon pont = new Polygon(6, cy, 10, cy - 10, L - 18, cy - 11, L - 4, cy, L - 18, cy + 11, 10, cy + 10); styleSuperstructure(pont);
        Line axe = axeDash(4, cy, L - 4, cy);
        Group tav = tourelleSimpleGros(L - 16, cy, 11, true);
        Rectangle heli = new Rectangle(5, cy - 11, 22, 22); heli.setArcWidth(2); heli.setArcHeight(2); styleSuperstructure(heli);
        Line hx = makeLine(7, cy - 9, 25, cy + 9); Line hy = makeLine(25, cy - 9, 7, cy + 9); styleDetailLine(hx); styleDetailLine(hy);
        Rectangle sup = new Rectangle(L * 0.43, cy - 9, 20, 18); sup.setArcWidth(2); sup.setArcHeight(2); styleSuperstructure(sup);
        Rectangle det = new Rectangle(L * 0.43 + 3, cy - 6, 9, 5); det.setArcWidth(1); det.setArcHeight(1); styleDetailShape(det);
        Circle radar = new Circle(L * 0.53 + 2, cy, 5.5); styleDetailShape(radar);
        Line radarLine = makeLine((int)(L * 0.53 + 2), cy - 5.5, (int)(L * 0.53 + 2), cy + 5.5); styleDetailLine(radarLine);
        dessinHolographique.getChildren().addAll(coque, pont, axe, heli, hx, hy, sup, det, radar, radarLine, tav);
    }

    private void dessinerTorpilleur() {
        double L = CELL; double cy = CELL / 2;
        Polygon coque = new Polygon(0, cy, 4, cy - 7, 18, cy - 8, L, cy, 18, cy + 8, 4, cy + 7); styleHull(coque);
        Polygon corps = new Polygon(4, cy, 7, cy - 4, 16, cy - 5, L - 2, cy, 16, cy + 5, 7, cy + 4); styleSuperstructure(corps);
        Line axe = axeDash(2, cy, L - 2, cy);
        Circle tete = new Circle(L - 5, cy, 4); styleDetailShape(tete);
        Line ail1 = makeLine(4, cy - 7, 0, cy - 12); Line ail2 = makeLine(4, cy + 7, 0, cy + 12); styleDetailLine(ail1); styleDetailLine(ail2);
        dessinHolographique.getChildren().addAll(coque, corps, axe, tete, ail1, ail2);
    }

    // --- SOUS-ÉLÉMENTS ---

    private Group tourelleDual(double cx, double cy, double r, boolean versAvant) {
        Group g = new Group(); double dir = versAvant ? 1 : -1;

        Circle base = new Circle(cx, cy, r);
        base.setFill(SUPERSTRUCTURE_FILL); base.setStroke(DETAIL_LINE); base.setStrokeWidth(1.0); // Bordure blanche

        Circle pivot = new Circle(cx, cy, r * 0.4);
        pivot.setFill(ACCENT); pivot.setStroke(Color.TRANSPARENT); // Point central blanc pur

        double canonLen = r * 1.5;
        Line c1 = new Line(cx, cy - r * 0.3, cx + dir * canonLen, cy - r * 0.3);
        Line c2 = new Line(cx, cy + r * 0.3, cx + dir * canonLen, cy + r * 0.3);
        c1.setStroke(ACCENT); c1.setStrokeWidth(1.5); c1.setStrokeLineCap(StrokeLineCap.ROUND); // Canons blancs
        c2.setStroke(ACCENT); c2.setStrokeWidth(1.5); c2.setStrokeLineCap(StrokeLineCap.ROUND);

        g.getChildren().addAll(base, pivot, c1, c2); return g;
    }

    private Group tourelleSimple(double cx, double cy, double r, boolean versAvant, double angleOffset) {
        Group g = new Group(); double dir = versAvant ? 1 : -1;

        Circle base = new Circle(cx, cy, r);
        base.setFill(SUPERSTRUCTURE_FILL); base.setStroke(DETAIL_LINE); base.setStrokeWidth(1.0);

        Circle pivot = new Circle(cx, cy, r * 0.4);
        pivot.setFill(ACCENT); pivot.setStroke(Color.TRANSPARENT);

        double rad = Math.toRadians(angleOffset); double ex = cx + dir * r * 1.6 * Math.cos(rad); double ey = cy + dir * r * 1.6 * Math.sin(rad);
        Line c = new Line(cx, cy, ex, ey); c.setStroke(ACCENT); c.setStrokeWidth(1.5); c.setStrokeLineCap(StrokeLineCap.ROUND);

        g.getChildren().addAll(base, pivot, c); return g;
    }

    private Group tourelleSimpleGros(double cx, double cy, double r, boolean versAvant) {
        Group g = new Group(); double dir = versAvant ? 1 : -1;

        Circle base = new Circle(cx, cy, r);
        base.setFill(SUPERSTRUCTURE_FILL); base.setStroke(DETAIL_LINE); base.setStrokeWidth(1.0);

        Circle pivot = new Circle(cx, cy, r * 0.45);
        pivot.setFill(ACCENT); pivot.setStroke(Color.TRANSPARENT);

        Line c = new Line(cx, cy, cx + dir * r * 2.0, cy); c.setStroke(ACCENT); c.setStrokeWidth(2.0); c.setStrokeLineCap(StrokeLineCap.ROUND);

        g.getChildren().addAll(base, pivot, c); return g;
    }

    // --- MÉTHODES DE STYLE CENTRALISÉES ---

    private void styleHull(Shape s) {
        s.setFill(HULL_FILL);
        s.setStroke(BORDER_PRIMARY);
        s.setStrokeWidth(1.5);
    }

    private void styleSuperstructure(Shape s) {
        s.setFill(SUPERSTRUCTURE_FILL);
        s.setStroke(DETAIL_LINE); // Contour blanc
        s.setStrokeWidth(1.0);
    }

    private void styleDetailShape(Shape s) {
        s.setFill(Color.TRANSPARENT); // Transparence pour voir la coque
        s.setStroke(DETAIL_LINE);     // Contour blanc
        s.setStrokeWidth(0.8);
    }

    private void styleDetailLine(Shape s) {
        s.setFill(Color.TRANSPARENT);
        s.setStroke(DETAIL_LINE);     // Ligne blanche
        s.setStrokeWidth(0.8);
    }

    private Line axeDash(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(DETAIL_LINE);     // Axe central blanc
        l.setStrokeWidth(0.5);
        l.getStrokeDashArray().addAll(6.0, 4.0);
        return l;
    }

    private Line makeLine(double x1, double y1, double x2, double y2) { return new Line(x1, y1, x2, y2); }
    private Ellipse makeEllipse(double cx, double cy, double rx, double ry) { return new Ellipse(cx, cy, rx, ry); }
    public Vaisseau getNavire() { return navire; }
}