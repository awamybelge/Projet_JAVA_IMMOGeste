import java.time.LocalDate;

public class Bail {

    private Bien bien;
    private Locataire locataire;
    private Gestionnaire gestionnaire;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean cloture = false;

    /**
     * Construit un bail actif (non clôturé).
     *
     * @param bien bien loué (agrégation)
     * @param locataire locataire signataire (agrégation)
     * @param gestionnaire gestionnaire responsable (association)
     * @param dateDebut date de début
     * @param dateFin date de fin, strictement postérieure à dateDebut
     * @throws IllegalArgumentException si dateFin n'est pas strictement après dateDebut
     *      (cas limite : dateFin == dateDebut est rejeté)
     */
    public Bail(Bien bien, Locataire locataire, Gestionnaire gestionnaire,
                LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null || !dateFin.isAfter(dateDebut)) {
            throw new IllegalArgumentException(
                    "La date de fin doit être strictement postérieure à la date de début.");
        }
        this.bien = bien;
        this.locataire = locataire;
        this.gestionnaire = gestionnaire;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Bien getBien() {
        return bien;
    }

    public void setBien(Bien bien) {
        this.bien = bien;
    }

    public Locataire getLocataire() {
        return locataire;
    }

    public void setLocataire(Locataire locataire) {
        this.locataire = locataire;
    }

    public Gestionnaire getGestionnaire() {
        return gestionnaire;
    }

    public void setGestionnaire(Gestionnaire gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    /**
     * Modifie les dates du bail en revalidant l'invariant.
     *
     * @param dateDebut nouvelle date de début
     * @param dateFin nouvelle date de fin, strictement postérieure à dateDebut
     * @throws IllegalArgumentException si l'invariant n'est pas respecté
     */
    public void setDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null || !dateFin.isAfter(dateDebut)) {
            throw new IllegalArgumentException(
                    "La date de fin doit être strictement postérieure à la date de début.");
        }
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    /**
     * Indique si le bail est clôturé.
     *
     * @return true si {@link #cloturer()} a déjà été appelée
     */
    public boolean isCloture() {
        return cloture;
    }

    /**
     * Indique si ce bail occupe encore le bien sur une partie de la période
     * donnée (bornes incluses). Un bail clôturé ne bloque plus le bien.
     *
     * @param debut début de la période testée, non null
     * @param fin fin de la période testée, non null
     * @return true si le bail est actif et chevauche la période
     */
    public boolean chevauche(LocalDate debut, LocalDate fin) {
        if (cloture) {
            return false;
        }
        return !fin.isBefore(dateDebut) && !debut.isAfter(dateFin);
    }

    /**
     * Clôture le bail.
     *
     * <p><b>Précondition</b> : le bail n'est pas déjà clôturé.
     * <b>Postcondition</b> : {@link #isCloture()} renvoie true.</p>
     *
     * @throws IllegalStateException si le bail est déjà clôturé (cas limite :
     *      double clôture). C'est un état incohérent de l'objet, pas un
     *      paramètre invalide : d'où IllegalStateException.
     */
    public void cloturer() {
        if (cloture) {
            throw new IllegalStateException("Ce bail est déjà clôturé.");
        }
        cloture = true;
    }

    @Override
    public String toString() {
        return "Bail{bien=" + bien + ", locataire=" + locataire
                + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", cloture=" + cloture + "}";
    }
}