

/**
 * Exception métier vérifiée, levée lorsqu'un bail est demandé sur un bien
 * déjà loué pour une période qui chevauche la demande.
 *
 * <p>Elle est vérifiée (hérite de {@link Exception}) : la signature d'un bail
 * sur un bien occupé est un cas métier attendu, pas un bug. L'appelant est
 * donc obligé de décider quoi faire (autre bien, autres dates...).</p>
 */
public class BienIndisponibleException extends Exception {

    /**
     * Construit l'exception avec un message explicatif.
     *
     * @param message raison de l'indisponibilité
     */
    public BienIndisponibleException(String message) {
        super(message);
    }
}