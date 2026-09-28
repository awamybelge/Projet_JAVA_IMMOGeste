
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class EtatDesLieux {

    private Bien bien;
    private LocalDate dateRealisation;
    private List<String> listeDefauts;

    /**
     * Construit un état des lieux.
     *
     * @param bien bien concerné
     * @param dateRealisation date de réalisation, non future
     * @param listeDefauts liste des défauts constatés (peut être vide, jamais null en interne)
     * @throws IllegalArgumentException si dateRealisation est dans le futur
     */
    public EtatDesLieux(Bien bien, LocalDate dateRealisation, List<String> listeDefauts) {
        if (dateRealisation == null || dateRealisation.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de réalisation ne peut pas être future.");
        }
        this.bien = bien;
        this.dateRealisation = dateRealisation;
        this.listeDefauts = (listeDefauts != null) ? new ArrayList<>(listeDefauts) : new ArrayList<>();
    }

    /**
     * Constructeur surchargé : crée un état des lieux sans défaut constaté.
     *
     * @param bien bien concerné
     * @param dateRealisation date de réalisation, non future
     */
    public EtatDesLieux(Bien bien, LocalDate dateRealisation) {
        this(bien, dateRealisation, new ArrayList<>());
    }

    public Bien getBien() {
        return bien;
    }

    public LocalDate getDateRealisation() {
        return dateRealisation;
    }

    public List<String> getListeDefauts() {
        return new ArrayList<>(listeDefauts);
    }

    public void ajouterDefaut(String defaut) {
        if (defaut == null || defaut.isBlank()) {
            throw new IllegalArgumentException("Un défaut ne peut pas être vide.");
        }
        this.listeDefauts.add(defaut);
    }

    @Override
    public String toString() {
        return "EtatDesLieux{bien=" + bien + ", dateRealisation=" + dateRealisation
                + ", nbDefauts=" + listeDefauts.size() + "}";
    }
}