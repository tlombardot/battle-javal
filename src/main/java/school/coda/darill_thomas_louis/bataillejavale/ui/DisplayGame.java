package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.Cursor;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.CreateDB;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class DisplayGame extends GameApplication {

    @Override
    /*
     * Configuration de la fenêtre d'affichage
     * */
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(800);
        gameSettings.setHeight(600);
        gameSettings.setTitle("Battle-Javale-MaSalive");
        gameSettings.setVersion("");
    }

    @Override
    /*
     * Configuration du menu principale
     * */
    protected void initUI() {
        getGameScene().getRoot().setCursor(Cursor.DEFAULT);
        // à compléter
    }

    /*
     * Lancement de la fenêtre (pour l'instant)
     * */
    static void main(String[] args) {
        new CreateDB();
        launch(args);
    }
}
