
/**
 * Local commercial : loué nu par nature, il n'implémente donc pas
 * {@link Meublable}. Charges spécifiques de 20 %.
 */
public class LocalCommercial extends Bien {

    private static final double TAUX_CHARGES_SPECIFIQUES = 0.20;

    /**
     * Construit un local commercial.
     *
     * @param adresse adresse du bien
     * @param surfaceM2 surface en m², strictement positive
     * @param loyerBase loyer de base, strictement positif
     */
    public LocalCommercial(String adresse, double surfaceM2, double loyerBase) {
        super(adresse, surfaceM2, loyerBase);
    }

    /**
     * Constructeur surchargé avec loyer de base par défaut.
     *
     * @param adresse adresse du bien
     * @param surfaceM2 surface en m², strictement positive
     */
    public LocalCommercial(String adresse, double surfaceM2) {
        super(adresse, surfaceM2, LOYER_PAR_DEFAUT);
    }

    /** {@inheritDoc} Ajoute 20 % de charges spécifiques au loyer de base. */
    @Override
    public double calculerLoyerCharges() {
        return getLoyerBase() * (1 + TAUX_CHARGES_SPECIFIQUES);
    }

    @Override
    public String getTypeLibelle() {
        return "LocalCommercial";
    }
}