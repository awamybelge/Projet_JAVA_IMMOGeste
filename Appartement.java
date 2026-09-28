
import java.util.ArrayList;
import java.util.List;


public class Appartement extends Bien implements Meublable {

    private static final double TAUX_CHARGES_COPROPRIETE = 0.15;

    private final List<String> equipements = new ArrayList<>();

    /**
     * Construit un appartement.
     *
     * @param adresse adresse du bien
     * @param surfaceM2 surface en m², strictement positive
     * @param loyerBase loyer de base, strictement positif
     */
    public Appartement(String adresse, double surfaceM2, double loyerBase) {
        super(adresse, surfaceM2, loyerBase);
    }

    /**
     * Constructeur surchargé avec loyer de base par défaut.
     *
     * @param adresse adresse du bien
     * @param surfaceM2 surface en m², strictement positive
     */
    public Appartement(String adresse, double surfaceM2) {
        super(adresse, surfaceM2, LOYER_PAR_DEFAUT);
    }

    /** {@inheritDoc} Ajoute 15 % de charges de copropriété au loyer de base. */
    @Override
    public double calculerLoyerCharges() {
        return getLoyerBase() * (1 + TAUX_CHARGES_COPROPRIETE);
    }

    @Override
    public String getTypeLibelle() {
        return "Appartement";
    }

    @Override
    public void ajouterEquipement(String designation) {
        if (designation == null || designation.isBlank()) {
            throw new IllegalArgumentException("La désignation de l'équipement ne peut pas être vide.");
        }
        equipements.add(designation);
    }

    @Override
    public boolean estMeuble() {
        return !equipements.isEmpty();
    }

    public List<String> getEquipements() {
        return new ArrayList<>(equipements);
    }
}