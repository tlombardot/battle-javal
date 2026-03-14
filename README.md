# 🚢 La Bataille Javale

**Projet de Programmation Orientée Objet (POO) avec Java – Coda 1ère année – 2026**

Un simulateur de bataille navale mais avec pas mal de d'ajout comparé au jeu classique en Java avec l'interface graphique **FXGL / JavaFX**.

👨‍💻 **Équipe de développement :**
* Darill
* Thomas
* Louis

---

## 🌟 Fonctionnalités Implémentées

### 🎯 Fonctionnalités de base (Core)
* **Placement dynamique :** Grille interactive, gestion des collisions, pivotement (clic droit), placement manuel (Drag & Drop holographique) ou aléatoire.
* **Bataille au tour par tour :** Grille océan (alliée) et radar (ennemie). Animations de tirs et de destruction en temps réel.
* **Intelligence Artificielle :** Mode Solo contre le CPU avec ripostes intelligentes.

### 🏆 Fonctionnalités Additionnelles (Bonus)
* ☁️ **Multijoueur via Base de Données (Supabase) :** * Système de salon (Host / Join) via un ID unique.
    * Synchronisation des tours en temps réel via Polling optimisé.
    * Reprise de partie (Load Game) via une interface "Archives" listant les sauvegardes (historique des parties `EN_COURS`).
* 👤 **Profils Locaux et Statistiques :** * Connexion automatique via la session OS de la machine.
    * Suivi persistant des Victoires et Défaites enregistré en `JSONB` dans le Cloud et localement.
* ⚙️ **Personnalisation et Préférences (Cloud Sync) :**
    * Menu des paramètres : Configuration de la chaîne de connexion DB, gestion de l'audio et des modules.
    * Les préférences sont sauvegardées localement ET synchronisées dans le profil Cloud du joueur.
* 🎲 **Modules de Gameplay (Désactivables) :**
    * **Événements Aléatoires :** Implémentation d'événements en cours de partie (ex: *Pluie de météores* avec impact visuel et destructions, *Brouillage Radar* via effet de flou `BoxBlur`).
* 🎵 **Musique et Effets Sonores :** Gestionnaire audio complet (`GestionnaireAudio`) avec musique d'ambiance et effets sonores réactifs, modifiables en direct via un *Slider* dans les paramètres.

---

## 🎨 Interface Graphique (UI / UX)
* **Custom Dialogs :** Popups de saisie (`CustomInputDialogUI`), alertes (`CustomMessageBoxUI`), et sélection de partie (`LoadGamePopupUI`).
* **Effets Visuels :** Utilisation des shaders JavaFX (`DropShadow`, `ColorAdjust`, `GaussianBlur`, `BoxBlur`), animations (`FadeTransition`, `RotateTransition`).
* **Menu Pause :** Menu in-game accessible via `ESC` pour reprendre ou quitter la partie proprement.

---

## 🏛️ Architecture Logicielle

La logique métier est totalement isolée de l'interface graphique.

📦 `school.coda.darill_thomas_louis.bataillejavale`
┣ 📂 **`controller`** *(Le Contrôleur)*
┃ ┣ 📜 `PartieControleur` : Chef d'orchestre, gère les tours, le réseau et connecte l'UI au Moteur.
┃ ┣ 📜 `GestionnairePlacement` : Gère le Drag & Drop.
┃ ┗ 📜 `GestionnaireAudio` : Singleton gérant les flux audio.
┃
┣ 📂 **`core`** *(Le Modèle / La Logique Métier)*
┃ ┣ 📂 `engine` > 📜 `MoteurJeu` : L'IA, les règles du jeu, et la résolution des tirs.
┃ ┗ 📂 `model` : Entités POO pures (`Joueur`, `Vaisseau`, `ConfigPartie`, `EtatJeu`).
┃
┣ 📂 **`infrastructure`** *(La Persistance et Configuration)*
┃ ┣ 📂 `config` > 📜 `PreferencesManager` : Gestion du fichier local de configuration.
┃ ┗ 📂 `database` > `PartieRepository`, `JoueurRepository` : Requêtes SQL via JDBC (PostgreSQL).
┃
┗ 📂 **`ui`** *(La Vue)*
┗ 📜 `PlateauDeJeu`, `MenuUI`, Popups : Classes purement graphiques. Ne contiennent aucune logique métier, elles ne font qu'écouter le joueur et obéir au Contrôleur.

---

## 🛠️ Prérequis et Exécution

### Prérequis
* **Java 25** (ou JDK compatible configuré dans le projet)
* **Maven** (Inclus via le wrapper `mvnw`)
* Connexion Internet active (pour la BDD) mais peut être modifier facilement pour base de données local, il créera automatiquement la base de donnée

### Compilation et Exécution

Ouvrez un terminal à la racine du projet (là où se trouve le `pom.xml`) et exécutez la commande suivante :

**Sur Windows :**
```cmd
mvnw.cmd clean javafx:run
```
**Sur Linux / MacOS :**
```bash
./mvnw clean javafx:run
```