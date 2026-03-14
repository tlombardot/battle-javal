# 🚢 BATAILLE JAVALE : Opération Cyber-Océan 🌊

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-FF0000?style=for-the-badge&logo=java&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![FXGL](https://img.shields.io/badge/FXGL-Game_Engine-blue?style=for-the-badge)

> **"ESTABLISHING SATELLITE CONNECTIONS..."**
> Bataille Javale est une réinvention moderne, dynamique et multijoueur du célèbre jeu de plateau Touché-Coulé (Bataille Navale), développée en Java avec le moteur FXGL. 

---

## 📸 Aperçu du Projet

<p align="center">
  <img src="lien/vers/ton/image_menu_principal.png" alt="Menu Principal Bataille Javale" width="800"/>
  <br>
  <em>Interface satellite principale et gestion de profil cloud.</em>
</p>

---

## 🚀 Fonctionnalités Principales

Notre vision était d'aller plus loin qu'une simple grille cliquable. Nous avons intégré des mécaniques de jeu avancées et une interface immersive :

* **🎙️ Ambiance Sonore Immersive :** Moteur audio personnalisé gérant simultanément les musiques de fond, les bruits d'ambiance (océan, orage) et les effets sonores (SFX) superposables lors des tirs.
* **🤖 Mode Solo vs IA :** Affrontez une intelligence artificielle directement depuis votre poste de commandement.
* **🌐 Multijoueur en Ligne :** Hébergez ou rejoignez des salons (Lobbies) via une base de données cloud (Supabase/PostgreSQL).
* **💾 Sauvegarde Cloud & Reprise :** Sauvegarde de l'état de la partie, des statistiques (Victoires/Défaites) et des préférences utilisateur.
* **⚙️ Modules de Jeu Personnalisables :**
    * *Ravitaillement :* Récupérez des munitions ou des bonus en cours de partie.
    * *Événements Aléatoires :* Des tempêtes ou des pannes radar peuvent survenir et bouleverser la stratégie.

---

## 📋 Consignes et Implémentation

<details>
<summary><strong>👉 Cliquez pour voir le respect du cahier des charges</strong></summary>
<br>

Ce projet a été réalisé dans le cadre d'un cursus de développement. Voici comment nous avons répondu aux exigences :

1.  **Architecture Orientée Objet (POO) :** Code découpé proprement (Modèle, Vue, Contrôleur, Infrastructure) avec l'utilisation de design patterns (ex: Singleton pour le `GestionnaireAudio` et `PreferencesManager`).
2.  **Interface Graphique (GUI) :** Utilisation avancée de JavaFX et FXGL pour des animations fluides (`FadeTransition`, `RotateTransition`), des ombres portées (DropShadow) et un rendu visuel "Tech/Néon".
3.  **Persistance des Données :** Implémentation d'un `PartieRepository` et `JoueurRepository` connectés à une base PostgreSQL distante pour garantir la persistance des salons, des profils et des sauvegardes de jeu.
4.  **Règles du jeu de base :** Placement des navires, gestion des tirs (Touché, Coulé, À l'eau), détection de fin de partie.
5.  **Fonctionnalités Bonus :** Les modules additionnels (Événements, sons, notifcations, multijoeur) ajoutent la rejouabilité demandée par les critères d'excellence.

</details>

---

## 🛠️ Installation et Lancement

### Prérequis
* **Java Development Kit (JDK) 21** (ou version compatible avec FXGL 17/21).
* **Maven** ou **Gradle** (selon la configuration de votre IDE).
* Une connexion internet active (requise pour le Cloud et le Multijoueur).

### Étapes d'installation

1. **Cloner le dépôt :**
   ```bash
   git clone [https://github.com/votre-nom/bataille-javale.git](https://github.com/votre-nom/bataille-javale.git)
   cd bataille-javale

```

2. **Construire le projet (avec Maven) :**
```bash
mvn clean install

```


3. **Lancer le jeu :**
   Exécutez la classe principale (DisplayGame.java dans le dossier ui).

---

## 📡 Guide : Lancer une Partie Multijoueur

Le système multijoueur fonctionne via des "Salons" (Lobbies) synchronisés sur notre base de données cloud.

<details>
<summary><strong>Créer un salon (Héberger)</strong></summary>

1. Dans le menu principal, cliquez sur **`HOST MULTIPLAYER_`**.
2. Configurez les options de votre partie (Taille de grille, modules actifs).
3. Le système va "Établir la connexion satellite" et générer un **ID de Salon**.
4. Communiquez cet ID (ex: `1042`) à votre adversaire.
5. Attendez qu'il rejoigne pour que le placement des flottes commence !

</details>

<details>
<summary><strong>Rejoindre un salon (Invité)</strong></summary>

1. Dans le menu principal, cliquez sur **`JOIN MULTIPLAYER_`**.
2. Une boîte de dialogue s'ouvre : *SATELLITE LINK : JOIN LOBBY*.
3. Entrez l'**ID de Salon** fourni par l'hôte.
4. Si le salon est valide et en attente, vous serez instantanément connecté au plateau de jeu de l'hôte.

</details>

<p align="center">
<img src="lien/vers/ton/image_multijoueur.png" alt="Interface Multijoueur" width="600"/>
</p>

---

## 💡 Astuces de Commandants (Tips & Tricks)

* **Gérez le volume intelligemment :** Si vous jouez avec un ami en vocal (Discord), baissez le volume de la musique dans les **`SETTINGS_`** pour mieux entendre les tirs (SFX), tout en gardant une légère ambiance de fond (l'ambiance océanique s'ajuste automatiquement !).
* **Anticipez les Événements :** Si le module "Événements Aléatoires" est activé, ne regroupez pas tous vos navires au même endroit. Une tempête ciblée pourrait révéler ou endommager un large secteur d'un coup.
* **Tirez parti du Ravitaillement :** Parfois, rater un tir sur une case mystère peut vous octroyer un bonus radar bien plus utile qu'un simple "Touché".

---

## 🧠 Retours d'Expérience (Feedbacks)

La réalisation de Bataille Javale a été un défi technique riche en apprentissages :

* **Intégration Audio avec JavaFX :** Nous avons dû construire un `GestionnaireAudio` sur-mesure pour contourner les limitations strictes des chemins de ressources Java (`.toExternalForm()`) et permettre la superposition d'ambiances en boucle avec des SFX instantanés.
* **Base de données distante :** L'utilisation de PostgreSQL (via Supabase) pour lister les parties en temps réel a demandé une gestion fine des exceptions et des chargements asynchrones pour ne pas bloquer l'interface (d'où l'écran de chargement *Establishing Satellite Connections*).
* **Design UI/UX :** Créer des composants graphiques (boutons, sliders) full custom en Java pur (sans CSS lourd) via des `StackPane` et des `DropShadow` nous a permis d'obtenir exactement l'esthétique "Terminal Militaire Sci-Fi" souhaitée, rendant l'expérience unique.

---

## 👨‍💻 Équipe de Développement

Créé avec passion par :

* **KING_DARILL_** (Darill)
* **CYBER080THOMAS_** (Thomas)
* **JAVA_LOUIS_** (Louis)

*Projet académique réalisé pour [Coda].*
