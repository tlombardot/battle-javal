package school.coda.darill_thomas_louis.bataillejavale.core.model;

public class Vaisseau {

    private final String nom;
    private final int taille;
    private int casesTouchees = 0;

    public Vaisseau(String nom, int taille) {
        this.nom = nom;
        this.taille = taille;
    }

    public String getNom() { return nom; }
    public int getTaille() { return taille; }

    public boolean estCoule(){
        return casesTouchees >= taille;
    }
    public boolean recevoirDegat(int x, int y){
        casesTouchees++;
        return estCoule();
    }
}
