
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Quittance {

    private Bail bail;
    private LocalDate dateEmission;
    private List<LigneQuittance> ligneQuittances;

    /**
     * Construit une quittance.
     *
     * @param bail bail concerné
     * @param dateEmission date d'émission
     * @param ligneQuittances lignes de la quittance, au moins une ligne requise
     * @throws IllegalArgumentException si aucune ligne n'est fournie
     */
    public Quittance(Bail bail, LocalDate dateEmission, List<LigneQuittance> ligneQuittances) {
        if (ligneQuittances == null || ligneQuittances.isEmpty()) {
            throw new IllegalArgumentException("Une quittance doit contenir au moins une ligne.");
        }
        this.bail = bail;
        this.dateEmission = dateEmission;
        this.ligneQuittances = new ArrayList<>(ligneQuittances);
    }

    /**
     * Constructeur surchargé : crée une quittance avec une seule ligne (le cas le plus courant,
     * le loyer de base).
     *
     * @param bail bail concerné
     * @param dateEmission date d'émission
     * @param designation libellé de l'unique ligne
     * @param montant montant de l'unique ligne
     */
    public Quittance(Bail bail, LocalDate dateEmission, String designation, double montant) {
        this(bail, dateEmission, List.of(new LigneQuittance(designation, montant)));
    }

    public Bail getBail() {
        return bail;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public List<LigneQuittance> getLigneQuittances() {
        return new ArrayList<>(ligneQuittances);
    }

    /**
     * Calcule le montant total de la quittance en sommant toutes ses lignes.
     *
     * @return la somme des montants de toutes les lignes
     */
    public double getMontantTotal() {
        double total = 0.0;
        for (LigneQuittance ligne : ligneQuittances) {
            total += ligne.getMontant();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Quittance{dateEmission=" + dateEmission + ", montantTotal=" + getMontantTotal() + "}";
    }

    /**
     * Classe interne représentant une ligne de quittance (par exemple : loyer, charges).
     *
     * <p>N'a de sens qu'au sein d'une {@link Quittance} : composition assumée
     * en la déclarant comme classe interne plutôt qu'indépendante.</p>
     */
    public static class LigneQuittance {

        private String designation;
        private double montant;

        /**
         * Construit une ligne de quittance.
         *
         * @param designation libellé de la ligne, non vide
         * @param montant montant de la ligne, strictement positif
         * @throws IllegalArgumentException si designation est vide ou montant <= 0
         */
        public LigneQuittance(String designation, double montant) {
            if (designation == null || designation.isBlank()) {
                throw new IllegalArgumentException("La désignation ne peut pas être vide.");
            }
            if (montant <= 0) {
                throw new IllegalArgumentException("Le montant doit être strictement positif.");
            }
            this.designation = designation;
            this.montant = montant;
        }

        public String getDesignation() {
            return designation;
        }

        public double getMontant() {
            return montant;
        }

        @Override
        public String toString() {
            return designation + ": " + montant;
        }
    }
}