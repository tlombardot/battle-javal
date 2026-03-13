package school.coda.darill_thomas_louis.bataillejavale.ui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import school.coda.darill_thomas_louis.bataillejavale.core.model.Vaisseau;

/**
 * Représentation graphique d'un navire en vue de dessus.
 * Style "Blueprint Holographique" — cyan néon sur fond transparent.
 *
 * Chaque taille de navire possède son propre dessin détaillé :
 *   taille 5 → Porte-avions
 *   taille 4 → Cuirassé
 *   taille 3 → Croiseur ou Sous-marin (selon nom)
 *   taille 2 → Destroyer ou Patrouilleur (selon nom)
 *   taille 1 → Torpilleur
 */
public class VaisseauUI extends StackPane {

    // ── Constantes de style ────────────────────────────────────────────────
    private static final Color NEON         = Color.web("#00dcff");
    private static final Color NEON_MID     = Color.web("#00dcff", 0.55);
    private static final Color NEON_DIM     = Color.web("#00dcff", 0.35);
    private static final Color NEON_FILL    = Color.web("#00dcff", 0.10);
    private static final Color NEON_FILL2   = Color.web("#00dcff", 0.20);
    private static final double CELL        = 30.0;

    // ── État ───────────────────────────────────────────────────────────────
    private final Vaisseau navire;
    private boolean estHorizontal = true;
    private final DropShadow glowEffect;
    private Group dessinHolographique;

    // ══════════════════════════════════════════════════════════════════════
    public VaisseauUI(Vaisseau navire) {
        this.navire = navire;
        this.setAlignment(Pos.CENTER);

        glowEffect = new DropShadow(15, NEON);
        glowEffect.setSpread(0.5);

        dessinerBlueprint();

        // Clic droit → rotation
        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                estHorizontal = !estHorizontal;
                dessinHolographique.setRotate(estHorizontal ? 0 : 90);
                if (!estHorizontal) {
                    this.setPrefSize(CELL, navire.getTaille() * CELL);
                } else {
                    this.setPrefSize(navire.getTaille() * CELL, CELL);
                }
            }
        });

        this.setOnMouseEntered(e -> this.setEffect(glowEffect));
        this.setOnMouseExited(e -> this.setEffect(null));

        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(navire.getNom() + ";" + estHorizontal);
            db.setContent(content);
            event.consume();
        });

        this.setStyle("-fx-cursor: hand;");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Point d'entrée : choisit le dessin selon la taille (et le nom)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerBlueprint() {
        this.getChildren().clear();
        dessinHolographique = new Group();

        switch (navire.getTaille()) {
            case 5 -> dessinerPorteAvions();
            case 4 -> dessinerCuirasse();
            case 3 -> {
                String nom = navire.getNom().toLowerCase();
                if (nom.contains("sous") || nom.contains("sub"))
                    dessinerSousMarin();
                else
                    dessinerCroiseur();
            }
            case 2 -> {
                String nom = navire.getNom().toLowerCase();
                if (nom.contains("patrol") || nom.contains("patrouil"))
                    dessinerPatrouilleur();
                else
                    dessinerDestroyer();
            }
            default -> dessinerTorpilleur();
        }

        dessinHolographique.setRotate(estHorizontal ? 0 : 90);
        this.setPrefSize(navire.getTaille() * CELL, CELL);
        this.getChildren().add(dessinHolographique);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PORTE-AVIONS — taille 5  (150 × 30 px)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerPorteAvions() {
        double L = 5 * CELL; // 150
        double H = CELL;     // 30
        double cy = H / 2;   // 15

        // Coque extérieure profilée
        Polygon coque = new Polygon(
                0, cy - 12,
                L - 40, cy - 14,
                L,       cy,
                L - 40, cy + 14,
                0, cy + 12
        );
        styleHull(coque);

        // Pont d'envol (rectangle intérieur)
        Rectangle pont = new Rectangle(4, cy - 10, L - 20, 20);
        pont.setArcWidth(4); pont.setArcHeight(4);
        styleInner(pont);

        // Axe central (ligne pointillée)
        Line axe = axeDash(5, cy, L - 15, cy);

        // Lignes de séparation piste
        Group sections = new Group();
        for (double x : new double[]{30, 60, 90, 120}) {
            Line s = new Line(x, cy - 9, x, cy + 9);
            styleDetail(s);
            sections.getChildren().add(s);
        }

        // Catapultes (2 lignes épaisses)
        Line cat1 = new Line(5, cy - 6, 100, cy - 6);
        Line cat2 = new Line(5, cy + 6, 100, cy + 6);
        cat1.setStroke(NEON); cat1.setStrokeWidth(1.2);
        cat2.setStroke(NEON); cat2.setStrokeWidth(1.2);

        // Île de commandement (droite)
        Rectangle ile = new Rectangle(105, cy - 14, 32, 18);
        ile.setArcWidth(3); ile.setArcHeight(3);
        styleInnerFill(ile);
        Rectangle ileDetail1 = new Rectangle(108, cy - 11, 10, 6);
        ileDetail1.setArcWidth(2); ileDetail1.setArcHeight(2);
        styleDetail(ileDetail1);
        Rectangle ileDetail2 = new Rectangle(122, cy - 11, 10, 6);
        ileDetail2.setArcWidth(2); ileDetail2.setArcHeight(2);
        styleDetail(ileDetail2);

        // Ascenseurs
        Rectangle asc1 = new Rectangle(6,  cy - 9, 12, 9);
        Rectangle asc2 = new Rectangle(88, cy - 9, 12, 9);
        Rectangle asc3 = new Rectangle(88, cy,     12, 9);
        for (Rectangle r : new Rectangle[]{asc1, asc2, asc3}) {
            styleDetail(r);
        }

        // Antennes
        Line ant1 = makeLine(112, cy - 14, 122, cy - 22);
        Line ant2 = makeLine(118, cy - 14, 130, cy - 25);
        styleDetail(ant1); styleDetail(ant2);

        dessinHolographique.getChildren().addAll(
                coque, pont, axe, sections,
                cat1, cat2,
                ile, ileDetail1, ileDetail2,
                asc1, asc2, asc3,
                ant1, ant2
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CUIRASSÉ — taille 4  (120 × 30 px)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerCuirasse() {
        double L = 4 * CELL; // 120
        double cy = CELL / 2;

        // Coque profilée
        Polygon coque = new Polygon(
                0, cy - 14,
                L - 26, cy - 18,
                L,       cy,
                L - 26, cy + 18,
                0, cy + 14
        );
        styleHull(coque);

        // Pont intérieur
        Polygon pont = new Polygon(
                4,      cy - 10,
                L - 28, cy - 14,
                L - 6,  cy,
                L - 28, cy + 14,
                4,      cy + 10
        );
        styleInner(pont);

        // Axe
        Line axe = axeDash(2, cy, L - 4, cy);

        // Tourelle avant (double canon)
        Group tav = tourelleDual(L - 20, cy, 13, true);

        // Tourelle milieu-avant (simple)
        Group tmav = tourelleSimple(L * 0.55, cy - 8, 10, true, -20);

        // Tourelle milieu-arrière (simple)
        Group tmar = tourelleSimple(L * 0.55, cy + 8, 10, true, 20);

        // Tourelle arrière (double canon)
        Group tar = tourelleDual(20, cy, 12, false);

        // Cheminées
        Ellipse ch1 = makeEllipse(L * 0.38, cy, 6, 4);
        Ellipse ch2 = makeEllipse(L * 0.28, cy, 6, 4);
        styleDetail(ch1); styleDetail(ch2);

        // Superstructure
        Rectangle super1 = new Rectangle(L * 0.28, cy - 8, 34, 16);
        super1.setArcWidth(3); super1.setArcHeight(3);
        styleDetail(super1);

        // Antennes
        Line ant1 = makeLine(L - 18, cy - 2, L - 3, cy - 10);
        Line ant2 = makeLine(3,       cy - 2, -4,    cy - 8);
        styleDetail(ant1); styleDetail(ant2);

        dessinHolographique.getChildren().addAll(
                coque, pont, axe,
                tav, tmav, tmar, tar,
                ch1, ch2, super1,
                ant1, ant2
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CROISEUR — taille 3  (90 × 30 px)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerCroiseur() {
        double L = 3 * CELL; // 90
        double cy = CELL / 2;

        Polygon coque = new Polygon(
                0, cy - 12,
                L - 20, cy - 16,
                L,       cy,
                L - 20, cy + 16,
                0, cy + 12
        );
        styleHull(coque);

        Polygon pont = new Polygon(
                4,      cy - 8,
                L - 22, cy - 12,
                L - 5,  cy,
                L - 22, cy + 12,
                4,      cy + 8
        );
        styleInner(pont);

        Line axe = axeDash(3, cy, L - 5, cy);

        // Tourelle avant (double)
        Group tav = tourelleDual(L - 18, cy, 12, true);

        // Tourelle arrière (double)
        Group tar = tourelleDual(18, cy, 11, false);

        // Superstructure centrale
        Rectangle sup = new Rectangle(L * 0.38, cy - 9, 26, 18);
        sup.setArcWidth(3); sup.setArcHeight(3);
        styleDetail(sup);
        Rectangle det1 = new Rectangle(L * 0.38 + 3, cy - 6, 10, 5);
        Rectangle det2 = new Rectangle(L * 0.38 + 3, cy + 1, 10, 5);
        det1.setArcWidth(1); det1.setArcHeight(1);
        det2.setArcWidth(1); det2.setArcHeight(1);
        styleDetail(det1); styleDetail(det2);

        // Cheminée
        Ellipse ch = makeEllipse(L * 0.58, cy, 6, 4);
        styleDetail(ch);

        // Antenne
        Line ant = makeLine(L - 14, cy - 2, L - 3, cy - 8);
        styleDetail(ant);

        dessinHolographique.getChildren().addAll(
                coque, pont, axe,
                tav, tar,
                sup, det1, det2, ch,
                ant
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SOUS-MARIN — taille 3  (90 × 30 px)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerSousMarin() {
        double L = 3 * CELL; // 90
        double cy = CELL / 2;

        // Coque très hydrodynamique (effilée des deux côtés)
        Polygon coque = new Polygon(
                0,      cy,
                8,      cy - 8,
                25,     cy - 14,
                60,     cy - 14,
                L - 6,  cy - 10,
                L,      cy,
                L - 6,  cy + 10,
                60,     cy + 14,
                25,     cy + 14,
                8,      cy + 8
        );
        styleHull(coque);

        // Coque interne
        Polygon interieur = new Polygon(
                6,      cy,
                14,     cy - 5,
                26,     cy - 10,
                60,     cy - 10,
                L - 8,  cy - 7,
                L - 4,  cy,
                L - 8,  cy + 7,
                60,     cy + 10,
                26,     cy + 10,
                14,     cy + 5
        );
        styleInner(interieur);

        // Axe central
        Line axe = axeDash(4, cy, L - 4, cy);

        // Kiosque (rectangle central)
        Rectangle kiosque = new Rectangle(L * 0.40, cy - 14, 18, 28);
        kiosque.setArcWidth(4); kiosque.setArcHeight(4);
        styleInnerFill(kiosque);

        // Écoutille dans le kiosque
        Rectangle ecoutille = new Rectangle(L * 0.40 + 3, cy - 9, 9, 7);
        ecoutille.setArcWidth(2); ecoutille.setArcHeight(2);
        styleDetail(ecoutille);

        // Périscope (cercle + point)
        Circle peri = new Circle(L * 0.40 + 9, cy + 4, 4);
        styleInnerFill(peri);
        Circle periDot = new Circle(L * 0.40 + 9, cy + 4, 1.5);
        periDot.setFill(NEON); periDot.setStroke(Color.TRANSPARENT);

        // Tubes lance-torpilles (avant, 2)
        Circle tt1 = new Circle(L - 16, cy - 7, 4.5);
        Circle tt2 = new Circle(L - 16, cy + 7, 4.5);
        styleInnerFill(tt1); styleInnerFill(tt2);
        Circle c1 = new Circle(L - 16, cy - 7, 2);
        Circle c2 = new Circle(L - 16, cy + 7, 2);
        styleDetail(c1); styleDetail(c2);

        // Hélice arrière
        Arc helice1 = new Arc(6, cy, 5, 8, 30,  120);
        Arc helice2 = new Arc(6, cy, 5, 8, 210, 120);
        helice1.setType(ArcType.OPEN); helice2.setType(ArcType.OPEN);
        styleDetail(helice1); styleDetail(helice2);
        Circle moyeu = new Circle(6, cy, 2.5);
        styleDetail(moyeu);

        // Gouvernails (croix)
        Line gouv1 = makeLine(8, cy - 8, 2, cy - 16);
        Line gouv2 = makeLine(8, cy + 8, 2, cy + 16);
        styleDetail(gouv1); styleDetail(gouv2);

        dessinHolographique.getChildren().addAll(
                coque, interieur, axe,
                kiosque, ecoutille,
                peri, periDot,
                tt1, tt2, c1, c2,
                helice1, helice2, moyeu,
                gouv1, gouv2
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  DESTROYER — taille 2  (60 × 30 px)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerDestroyer() {
        double L = 2 * CELL; // 60
        double cy = CELL / 2;

        // Coque très profilée
        Polygon coque = new Polygon(
                0,      cy,
                4,      cy - 12,
                20,     cy - 14,
                L - 10, cy - 14,
                L,      cy,
                L - 10, cy + 14,
                20,     cy + 14,
                4,      cy + 12
        );
        styleHull(coque);

        Polygon pont = new Polygon(
                5,      cy,
                9,      cy - 8,
                22,     cy - 10,
                L - 12, cy - 10,
                L - 3,  cy,
                L - 12, cy + 10,
                22,     cy + 10,
                9,      cy + 8
        );
        styleInner(pont);

        Line axe = axeDash(3, cy, L - 3, cy);

        // Tourelle avant (double)
        Group tav = tourelleDual(L - 14, cy, 11, true);

        // Tourelle arrière (simple)
        Group tar = tourelleSimple(16, cy, 9, false, 0);

        // Pont central + cheminée
        Rectangle bridge = new Rectangle(L * 0.33, cy - 8, 20, 16);
        bridge.setArcWidth(3); bridge.setArcHeight(3);
        styleDetail(bridge);
        Ellipse ch = makeEllipse(L * 0.46, cy, 5, 4);
        styleDetail(ch);

        // Lance-missiles latéraux
        Rectangle lm1 = new Rectangle(L * 0.25, cy - 14, 7, 4);
        Rectangle lm2 = new Rectangle(L * 0.25, cy + 10, 7, 4);
        lm1.setArcWidth(1); lm1.setArcHeight(1);
        lm2.setArcWidth(1); lm2.setArcHeight(1);
        styleDetail(lm1); styleDetail(lm2);

        dessinHolographique.getChildren().addAll(
                coque, pont, axe,
                tav, tar,
                bridge, ch,
                lm1, lm2
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PATROUILLEUR — taille 2  (60 × 30 px)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerPatrouilleur() {
        double L = 2 * CELL; // 60
        double cy = CELL / 2;

        Polygon coque = new Polygon(
                0,      cy,
                4,      cy - 14,
                L - 16, cy - 16,
                L,      cy,
                L - 16, cy + 16,
                4,      cy + 14
        );
        styleHull(coque);

        Polygon pont = new Polygon(
                6,      cy,
                10,     cy - 10,
                L - 18, cy - 11,
                L - 4,  cy,
                L - 18, cy + 11,
                10,     cy + 10
        );
        styleInner(pont);

        Line axe = axeDash(4, cy, L - 4, cy);

        // Tourelle avant (canon unique, gros)
        Group tav = tourelleSimpleGros(L - 16, cy, 11, true);

        // Hélipad arrière (rectangle avec croix)
        Rectangle heli = new Rectangle(5, cy - 11, 22, 22);
        heli.setArcWidth(2); heli.setArcHeight(2);
        styleInner(heli);
        Line hx = makeLine(7, cy - 9, 25, cy + 9);
        Line hy = makeLine(25, cy - 9, 7, cy + 9);
        styleDetail(hx); styleDetail(hy);

        // Superstructure
        Rectangle sup = new Rectangle(L * 0.43, cy - 9, 20, 18);
        sup.setArcWidth(2); sup.setArcHeight(2);
        styleDetail(sup);
        Rectangle det = new Rectangle(L * 0.43 + 3, cy - 6, 9, 5);
        det.setArcWidth(1); det.setArcHeight(1);
        styleDetail(det);

        // Radar circulaire
        Circle radar = new Circle(L * 0.53 + 2, cy, 5.5);
        styleDetail(radar);
        Line radarLine = makeLine((int)(L * 0.53 + 2), cy - 5.5, (int)(L * 0.53 + 2), cy + 5.5);
        styleDetail(radarLine);

        dessinHolographique.getChildren().addAll(
                coque, pont, axe,
                tav,
                heli, hx, hy,
                sup, det,
                radar, radarLine
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TORPILLEUR — taille 1  (30 × 30 px)
    // ══════════════════════════════════════════════════════════════════════
    private void dessinerTorpilleur() {
        double L = CELL; // 30
        double cy = CELL / 2;

        // Coque torpille
        Polygon coque = new Polygon(
                0,  cy,
                4,  cy - 7,
                18, cy - 8,
                L,  cy,
                18, cy + 8,
                4,  cy + 7
        );
        styleHull(coque);

        // Corps intérieur
        Polygon corps = new Polygon(
                4,      cy,
                7,      cy - 4,
                16,     cy - 5,
                L - 2,  cy,
                16,     cy + 5,
                7,      cy + 4
        );
        styleInner(corps);

        Line axe = axeDash(2, cy, L - 2, cy);

        // Tête de torpille (cercle avant)
        Circle tete = new Circle(L - 5, cy, 4);
        styleInnerFill(tete);

        // Ailettes arrière
        Line ail1 = makeLine(4, cy - 7, 0, cy - 12);
        Line ail2 = makeLine(4, cy + 7, 0, cy + 12);
        styleDetail(ail1); styleDetail(ail2);

        dessinHolographique.getChildren().addAll(coque, corps, axe, tete, ail1, ail2);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HELPERS — Tourelles
    // ══════════════════════════════════════════════════════════════════════

    /** Tourelle à double canon (avant ou arrière). */
    private Group tourelleDual(double cx, double cy, double r, boolean versAvant) {
        Group g = new Group();
        double dir = versAvant ? 1 : -1;

        Circle base = new Circle(cx, cy, r);
        base.setFill(NEON_FILL2);
        base.setStroke(NEON);
        base.setStrokeWidth(1.2);

        Circle pivot = new Circle(cx, cy, r * 0.38);
        pivot.setFill(Color.TRANSPARENT);
        pivot.setStroke(NEON_MID);
        pivot.setStrokeWidth(0.8);

        // 2 canons légèrement écartés
        double canonLen = r * 1.4;
        Line c1 = new Line(cx, cy - r * 0.25,
                cx + dir * canonLen, cy - r * 0.45);
        Line c2 = new Line(cx, cy + r * 0.25,
                cx + dir * canonLen, cy + r * 0.45);
        c1.setStroke(NEON); c1.setStrokeWidth(1.5); c1.setStrokeLineCap(StrokeLineCap.ROUND);
        c2.setStroke(NEON); c2.setStrokeWidth(1.5); c2.setStrokeLineCap(StrokeLineCap.ROUND);

        g.getChildren().addAll(base, pivot, c1, c2);
        return g;
    }

    /** Tourelle simple avec un seul canon. */
    private Group tourelleSimple(double cx, double cy, double r,
                                 boolean versAvant, double angleOffset) {
        Group g = new Group();
        double dir = versAvant ? 1 : -1;

        Circle base = new Circle(cx, cy, r);
        base.setFill(NEON_FILL2);
        base.setStroke(NEON);
        base.setStrokeWidth(1.2);

        Circle pivot = new Circle(cx, cy, r * 0.38);
        pivot.setFill(Color.TRANSPARENT);
        pivot.setStroke(NEON_MID);
        pivot.setStrokeWidth(0.8);

        double rad = Math.toRadians(angleOffset);
        double ex = cx + dir * r * 1.5 * Math.cos(rad) - Math.sin(rad) * 0;
        double ey = cy + dir * r * 1.5 * Math.sin(rad) + r * 1.5 * Math.sin(Math.abs(rad) * 0.3);

        Line c = new Line(cx, cy, ex, ey);
        c.setStroke(NEON); c.setStrokeWidth(1.3); c.setStrokeLineCap(StrokeLineCap.ROUND);

        g.getChildren().addAll(base, pivot, c);
        return g;
    }

    /** Tourelle grosse avec canon unique bien visible (patrouilleur). */
    private Group tourelleSimpleGros(double cx, double cy, double r, boolean versAvant) {
        Group g = new Group();
        double dir = versAvant ? 1 : -1;

        Circle base = new Circle(cx, cy, r);
        base.setFill(NEON_FILL2);
        base.setStroke(NEON);
        base.setStrokeWidth(1.3);

        Circle pivot = new Circle(cx, cy, r * 0.4);
        pivot.setFill(Color.TRANSPARENT);
        pivot.setStroke(NEON_MID);
        pivot.setStrokeWidth(0.8);

        Line c = new Line(cx, cy, cx + dir * r * 1.8, cy);
        c.setStroke(NEON); c.setStrokeWidth(2.0); c.setStrokeLineCap(StrokeLineCap.ROUND);

        g.getChildren().addAll(base, pivot, c);
        return g;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HELPERS — Styles
    // ══════════════════════════════════════════════════════════════════════

    private void styleHull(Shape s) {
        s.setFill(NEON_FILL);
        s.setStroke(NEON);
        s.setStrokeWidth(1.5);
    }

    private void styleInner(Shape s) {
        s.setFill(Color.TRANSPARENT);
        s.setStroke(NEON_MID);
        s.setStrokeWidth(0.8);
    }

    private void styleInnerFill(Shape s) {
        s.setFill(NEON_FILL2);
        s.setStroke(NEON);
        s.setStrokeWidth(1.0);
    }

    private void styleDetail(Shape s) {
        s.setFill(Color.TRANSPARENT);
        s.setStroke(NEON_DIM);
        s.setStrokeWidth(0.7);
    }

    private Line axeDash(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(Color.web("#00dcff", 0.30));
        l.setStrokeWidth(0.8);
        l.getStrokeDashArray().addAll(4.0, 4.0);
        return l;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HELPERS — Primitives
    // ══════════════════════════════════════════════════════════════════════

    private Line makeLine(double x1, double y1, double x2, double y2) {
        return new Line(x1, y1, x2, y2);
    }

    private Ellipse makeEllipse(double cx, double cy, double rx, double ry) {
        return new Ellipse(cx, cy, rx, ry);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Accesseur
    // ══════════════════════════════════════════════════════════════════════
    public Vaisseau getNavire() { return navire; }
}