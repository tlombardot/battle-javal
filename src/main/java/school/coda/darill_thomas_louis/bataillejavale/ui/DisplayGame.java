package school.coda.darill_thomas_louis.bataillejavale.ui;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import school.coda.darill_thomas_louis.bataillejavale.infrastructure.database.CreateDB;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class DisplayGame extends GameApplication {

    private MenuUI mainMenu;

    /**
     * Configure tout ce qui les paramètres de l'application
     * @param gameSettings
     * les paramètres de l'app
     */
    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(730);
        gameSettings.setTitle("Javale-Battle");
        gameSettings.setVersion("Test-Version");
    }

    /**
     *Gère la "communication" entre le clavier, les périphériques externe et l'interface utilisateur
     */
    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Up") {
            @Override
            protected void onActionBegin() {
                if (mainMenu != null) mainMenu.selectUp();
            }
        }, KeyCode.UP);

        FXGL.getInput().addAction(new UserAction("Down") {
            @Override
            protected void onActionBegin() {
                if (mainMenu != null) mainMenu.selectDown();
            }
        }, KeyCode.DOWN);

        FXGL.getInput().addAction(new UserAction("Select") {
            @Override
            protected void onActionBegin() {
                if (mainMenu != null) mainMenu.triggerCurrentSelection();
            }
        }, KeyCode.ENTER);
    }

    /**
     *Crée l'interface du menu avec l'arrière-plan, le logo, les boutons et les crédits
     */
    @Override
    protected void initUI() {
        getGameScene().getRoot().setCursor(Cursor.DEFAULT);
        mainMenu = new MenuUI();
        FXGL.addUINode(mainMenu);
    }

    /**
     * Création de le Database et lancement du jeu
     * @param args
     */
    static void main(String[] args) {
        new CreateDB();
        launch(args);
    }
}