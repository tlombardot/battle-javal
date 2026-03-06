package school.coda.darill_thomas_louis.bataillejavale.ui;
//Thomas l'a écrit (à ne pas oublier)
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

public class DisplayGame extends GameApplication {

    @Override
    /*
    * Configuration de la fenêtre d'affichage
    * */
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(800);
        gameSettings.setHeight(600);
        gameSettings.setTitle("Battle-Javale-MaSalive");
        gameSettings.setVersion("0.0.1");
    }

    @Override
    /*
    * Configuration du menu principale
    * */
    protected void initUI() {
        super.initUI();
        // à compléter
    }

    /*
    * Lancement de la fenêtre sur Adam pour le faire chier (pour l'instant)
    * */
    static void main(String[] args){
        launch(args);
    }
}
