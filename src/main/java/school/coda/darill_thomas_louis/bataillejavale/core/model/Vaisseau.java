package school.coda.darill_thomas_louis.bataillejavale.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Vaisseau {

    private String nom;
    private int taille;
    private int casesTouchees = 0;

    private int x = -1;
    private int y = -1;
    private boolean horizontal = true;

    // Pour la base de données il a besoin d'être vide
    public Vaisseau() {}

    public Vaisseau(String nom, int taille) {
        this.nom = nom;
        this.taille = taille;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getTaille() { return taille; }
    public void setTaille(int taille) { this.taille = taille; }

    public int getCasesTouchees() { return casesTouchees; }
    public void setCasesTouchees(int casesTouchees) { this.casesTouchees = casesTouchees; }


    /**
     * Enregistre la position finale du navire sur la grille.
     */
    public void placer(int x, int y, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    @JsonProperty("horizontal")
    public boolean estHorizontal() { return horizontal; }

    @JsonProperty("horizontal")
    public void setHorizontal(boolean horizontal) { this.horizontal = horizontal; }


    public boolean estCoule(){
        return casesTouchees >= taille;
    }

    public boolean recevoirDegat(int x, int y){
        casesTouchees++;
        return estCoule();
    }
}