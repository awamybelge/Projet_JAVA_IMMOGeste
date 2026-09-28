
public class LocataireIntrouvableException extends Exception {

    /**
     * Construit l'exception avec un message explicatif.
     * @param message raison de l'échec de la recherche
     */
    public LocataireIntrouvableException(String message) {
        super(message);
    }
}