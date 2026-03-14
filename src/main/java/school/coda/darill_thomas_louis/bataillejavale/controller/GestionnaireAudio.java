package school.coda.darill_thomas_louis.bataillejavale.controller;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import school.coda.darill_thomas_louis.bataillejavale.core.model.ConfigPartie;

import java.net.URL;

public class GestionnaireAudio {

    private static GestionnaireAudio instance;
    private ConfigPartie config;

    private MediaPlayer lecteurMusique;
    private MediaPlayer lecteurAmbiance;

    private String derniereMusique;
    private String derniereAmbiance;

    private GestionnaireAudio() {}

    public static GestionnaireAudio getInstance() {
        if (instance == null) {
            instance = new GestionnaireAudio();
        }
        return instance;
    }

    public void setConfig(ConfigPartie config) {
        this.config = config;

        if (config != null && !config.isSonActif()) {
            stopperTout();
        } else if (config != null && config.isSonActif()) {
            if (lecteurMusique == null && derniereMusique != null) {
                if (derniereAmbiance != null) {
                    jouerMusiqueEtAmbiance(derniereMusique, derniereAmbiance);
                } else {
                    jouerMusique(derniereMusique);
                }
            } else {
                actualiserVolumes();
            }
        }
    }

    // ==========================================
    // MUSIQUES ET AMBIANCES (En boucle)
    // ==========================================

    public void jouerMusique(String nomFichier) {
        this.derniereMusique = nomFichier;
        this.derniereAmbiance = null;

        if (config != null && !config.isSonActif()) return;

        stopperTout();
        lecteurMusique = creerLecteur("/assets/audio/musiques/" + nomFichier);

        if (lecteurMusique != null) {
            lecteurMusique.play();
            actualiserVolumes();
        }
    }

    public void jouerMusiqueEtAmbiance(String nomMusique, String nomAmbiance) {
        this.derniereMusique = nomMusique;
        this.derniereAmbiance = nomAmbiance;

        if (config != null && !config.isSonActif()) return;

        stopperTout();

        lecteurMusique = creerLecteur("/assets/audio/musiques/" + nomMusique);
        lecteurAmbiance = creerLecteur("/assets/audio/musiques/" + nomAmbiance);

        if (lecteurMusique != null) lecteurMusique.play();
        if (lecteurAmbiance != null) lecteurAmbiance.play();

        actualiserVolumes();
    }

    private MediaPlayer creerLecteur(String cheminInterne) {
        try {
            URL url = getClass().getResource(cheminInterne);
            if (url != null) {
                MediaPlayer lecteur = new MediaPlayer(new Media(url.toExternalForm()));
                lecteur.setCycleCount(MediaPlayer.INDEFINITE);
                return lecteur;
            } else {
                System.err.println("[AUDIO] Fichier introuvable : " + cheminInterne);
            }
        } catch (Exception e) {
            System.err.println("[AUDIO] Erreur création MediaPlayer : " + e.getMessage());
        }
        return null;
    }

    public void actualiserVolumes() {
        if (config != null) {
            double volMusique = config.getVolumeMusique();

            if (lecteurMusique != null) lecteurMusique.setVolume(volMusique);
            if (lecteurAmbiance != null) lecteurAmbiance.setVolume(volMusique * 0.7);
        }
    }

    public void stopperTout() {
        if (lecteurMusique != null) {
            lecteurMusique.stop();
            lecteurMusique = null;
        }
        if (lecteurAmbiance != null) {
            lecteurAmbiance.stop();
            lecteurAmbiance = null;
        }
    }

    // ==========================================
    // BRUITAGES - SFX (Superposables)
    // ==========================================

    public void jouerSon(String nomFichier) {
        if (config != null && !config.isSonActif()) return;

        try {
            URL cheminSon = getClass().getResource("/assets/audio/sfx/" + nomFichier);
            if (cheminSon != null) {
                AudioClip clip = new AudioClip(cheminSon.toExternalForm());
                clip.setVolume(config.getVolumeEffets());
                clip.play();
            } else {
                System.err.println("[AUDIO] SFX introuvable : /assets/audio/sfx/" + nomFichier);
            }
        } catch (Exception e) {
            System.err.println("[AUDIO] Erreur effet sonore : " + e.getMessage());
        }
    }
}