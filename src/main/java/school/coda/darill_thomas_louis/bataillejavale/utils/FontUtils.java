package school.coda.darill_thomas_louis.bataillejavale.utils;

import javafx.scene.text.Font;

public class FontUtils {

    private static final String FONT_PATH = "/assets/ui/fonts/Cinzel-Medium.ttf";
    private static final String DEFAULT_FONT = "Consolas";

    public static Font getPolice(double taille){
        Font customFont = Font.loadFont(FontUtils.class.getResourceAsStream(FONT_PATH), taille);

        if(customFont == null){
            System.err.println("ERREUR : Police introuvable à " + FONT_PATH + ", chargement de la police par défaut.");
            return Font.font(DEFAULT_FONT, taille);
        }

        return customFont;
    }

}
