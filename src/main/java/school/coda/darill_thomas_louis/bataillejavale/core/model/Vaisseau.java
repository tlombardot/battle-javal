package school.coda.darill_thomas_louis.bataillejavale.core.model;

public class Vaisseau {

    private final String nom;
    private final int taille;

    public Vaisseau(String nom, int taille) {
        this.nom = nom;
        this.taille = taille;
    }

    public boolean estCoule(){
        return true;//TODO : To Change
    }
    public boolean recevoirDegat(int x, int y){
        return true;//TODO : To Change
    }
}
