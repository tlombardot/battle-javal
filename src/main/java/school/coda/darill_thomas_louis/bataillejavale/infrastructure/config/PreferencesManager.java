package school.coda.darill_thomas_louis.bataillejavale.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import school.coda.darill_thomas_louis.bataillejavale.core.model.AppPreferences;

import java.io.File;
import java.io.IOException;


public final class PreferencesManager {

    private static PreferencesManager instance;
    private AppPreferences preferences;
    private final ObjectMapper mapper =  new ObjectMapper();
    private final File fichierPrefs;

    private PreferencesManager() {
        String userHome = System.getProperty("user.home");
        File dossierJeu = new File(userHome, ".bataillejavale");

        if(!dossierJeu.exists()) dossierJeu.mkdirs();

        fichierPrefs = new File(dossierJeu, "bataillejavale");
        chargerPreferences();
    }

    public static PreferencesManager getInstance() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    private void chargerPreferences() {
        if (fichierPrefs.exists()) {
            try {
                preferences = mapper.readValue(fichierPrefs, AppPreferences.class);
            } catch (IOException _) {
                IO.println("Erreur de lecture, chargement par défaut.");
                preferences = new AppPreferences();
            }
        } else {
            preferences = new AppPreferences();
            sauvegarderPreferences();
        }
    }

    public void sauvegarderPreferences() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(fichierPrefs, preferences);
        } catch (IOException e) {
            IO.println("Erreur de sauvegarde : " + e.getMessage());
        }
    }
}

