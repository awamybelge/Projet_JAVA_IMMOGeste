
public class Gestionnaire {

    private String nom;
    private String matricule;

    /**
     * Construit un gestionnaire.
     *
     * @param nom nom du gestionnaire
     * @param matricule identifiant interne, non vide
     * @throws IllegalArgumentException si matricule est vide/null
     */
    public Gestionnaire(String nom, String matricule) {
        if (matricule == null || matricule.isBlank()) {
            throw new IllegalArgumentException("Le matricule ne peut pas être vide.");
        }
        this.nom = nom;
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        if (matricule == null || matricule.isBlank()) {
            throw new IllegalArgumentException("Le matricule ne peut pas être vide.");
        }
        this.matricule = matricule;
    }

    @Override
    public String toString() {
        return "Gestionnaire{nom='" + nom + "', matricule='" + matricule + "'}";
    }
}