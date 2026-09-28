

/**
 * Représente un bien immobilier géré par l'agence.
 *
 * <p>Cette classe est abstraite : {@code new Bien(...)} est rejeté à la
 * compilation. Seules les classes filles concrètes peuvent être instanciées.</p>
 *
 * <p><b>Contrat de substitution (LSP)</b> : toute classe fille peut être
 * utilisée partout où un {@code Bien} est attendu. Aucune fille ne restreint
 * les préconditions du constructeur (surface et loyer strictement positifs)
 * et toutes garantissent que {@link #calculerLoyerCharges()} renvoie un
 * montant strictement positif.</p>
 *
 * <p>Invariants : surfaceM2 et loyerBase strictement positifs.</p>
 */
public abstract class Bien {

    /** Loyer de base utilisé par les constructeurs surchargés des filles. */
    protected static final double LOYER_PAR_DEFAUT = 500.0;

    private String adresse;
    private double surfaceM2;
    private double loyerBase;

    /**
     * Construit un bien avec toutes ses caractéristiques.
     *
     * @param adresse adresse du bien
     * @param surfaceM2 surface en m², strictement positive
     * @param loyerBase loyer de base mensuel, strictement positif
     * @throws IllegalArgumentException si surfaceM2 <= 0 ou loyerBase <= 0
     */
    protected Bien(String adresse, double surfaceM2, double loyerBase) {
        if (surfaceM2 <= 0) {
            throw new IllegalArgumentException("La surface doit être strictement positive.");
        }
        if (loyerBase <= 0) {
            throw new IllegalArgumentException("Le loyer de base doit être strictement positif.");
        }
        this.adresse = adresse;
        this.surfaceM2 = surfaceM2;
        this.loyerBase = loyerBase;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public double getSurfaceM2() {
        return surfaceM2;
    }

    public void setSurfaceM2(double surfaceM2) {
        if (surfaceM2 <= 0) {
            throw new IllegalArgumentException("La surface doit être strictement positive.");
        }
        this.surfaceM2 = surfaceM2;
    }

    public double getLoyerBase() {
        return loyerBase;
    }

    public void setLoyerBase(double loyerBase) {
        if (loyerBase <= 0) {
            throw new IllegalArgumentException("Le loyer de base doit être strictement positif.");
        }
        this.loyerBase = loyerBase;
    }

    /**
     * Calcule le loyer charges comprises. Chaque sous-type applique sa
     * propre règle de charges.
     *
     * @return le loyer charges comprises, toujours strictement positif
     */
    public abstract double calculerLoyerCharges();

    /**
     * Renvoie le libellé du type concret (pour l'affichage), sans que
     * l'appelant ait besoin de {@code instanceof}.
     *
     * @return le nom du type de bien
     */
    public abstract String getTypeLibelle();

    @Override
    public String toString() {
        return getTypeLibelle() + "{adresse='" + adresse + "', surfaceM2=" + surfaceM2
                + ", loyerBase=" + loyerBase + "}";
    }
}