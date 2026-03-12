package school.coda.darill_thomas_louis.bataillejavale.core.model;

public class Vaisseau {

    private String nom;
    private int taille;
    private int casesTouchees = 0;

    
    // Pour la base de donnèes il a besoin d'être vide
    public Vaisseau() {}

    public Vaisseau(String nom, int taille) {
        this.nom = nom;
        this.taille = taille;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; } // Nouveau

    public int getTaille() { return taille; }
    public void setTaille(int taille) { this.taille = taille; } // Nouveau

    public int getCasesTouchees() { return casesTouchees; } // Nouveau
    public void setCasesTouchees(int casesTouchees) { this.casesTouchees = casesTouchees; } // Nouveau

    public boolean estCoule(){
        return casesTouchees >= taille;
    }
    public boolean recevoirDegat(int x, int y){
        casesTouchees++;
        return estCoule();
    }
}