
import immogest.modele.Bail;
import immogest.modele.Bien;
import immogest.modele.Quittance;
import java.time.LocalDate;

/**
 * Service de quittancement. Ses méthodes acceptent le type le plus général,
 * {@link Bien}, et fonctionnent avec n'importe quel sous-type grâce au
 * polymorphisme : aucun {@code instanceof} n'est utilisé.
 */
public class ServiceQuittancement {

    /**
     * Calcule le montant mensuel (loyer + charges) d'un bien quel que soit
     * son type réel : c'est la redéfinition de
     * {@link Bien#calculerLoyerCharges()} dans chaque fille qui est appelée.
     *
     * @param bien bien concerné, non null
     * @return le loyer charges comprises
     * @throws IllegalArgumentException si bien est null
     */
    public double calculerMontantMensuel(Bien bien) {
        if (bien == null) {
            throw new IllegalArgumentException("Le bien ne peut pas être null.");
        }
        return bien.calculerLoyerCharges();
    }

    /**
     * Génère la quittance d'un bail à une ligne « Loyer et charges ».
     *
     * @param bail bail concerné, non null
     * @param dateEmission date d'émission, non null
     * @return la quittance générée
     * @throws IllegalArgumentException si un paramètre est null
     */
    public Quittance genererQuittance(Bail bail, LocalDate dateEmission) {
        if (bail == null || dateEmission == null) {
            throw new IllegalArgumentException("Le bail et la date d'émission sont obligatoires.");
        }
        double montant = calculerMontantMensuel(bail.getBien());
        return new Quittance(bail, dateEmission, "Loyer et charges", montant);
    }
}