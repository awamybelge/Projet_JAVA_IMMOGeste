
import immogest.exceptions.BienIndisponibleException;
import immogest.exceptions.LocataireIntrouvableException;
import immogest.modele.Appartement;
import immogest.modele.Bail;
import immogest.modele.Bien;
import immogest.modele.EtatDesLieux;
import immogest.modele.Gestionnaire;
import immogest.modele.LocalCommercial;
import immogest.modele.Locataire;
import immogest.modele.Maison;
import immogest.modele.Quittance;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service métier d'ImmoGest (version Phase 4) : chaque méthode publique
 * valide ses paramètres dès sa première instruction (fail-fast), aucune ne
 * renvoie {@code null}, et les méthodes critiques documentent leurs
 * préconditions et postconditions.
 *
 * <p>Biens, baux et locataires sont conservés en mémoire ; la persistance
 * réelle arrive en Phase 7.</p>
 */
public class ServiceImmoGest {

    private static final Logger LOGGER = Logger.getLogger(ServiceImmoGest.class.getName());

    private final ServiceQuittancement quittancement = new ServiceQuittancement();
    private final List<Bail> baux = new ArrayList<>();
    private final List<Bien> biens = new ArrayList<>();
    private final Map<Long, Locataire> locatairesParId = new HashMap<>();

    /**
     * Enregistre un bien dans le parc géré.
     *
     * @param bien bien à enregistrer, non null
     * @throws IllegalArgumentException si bien est null
     */
    public void enregistrerBien(Bien bien) {
        if (bien == null) {
            throw new IllegalArgumentException("Le bien ne peut pas être null.");
        }
        biens.add(bien);
    }

    /**
     * Enregistre un locataire sous un identifiant.
     *
     * @param id identifiant, non null
     * @param locataire locataire, non null
     * @throws IllegalArgumentException si un paramètre est null
     */
    public void enregistrerLocataire(Long id, Locataire locataire) {
        if (id == null || locataire == null) {
            throw new IllegalArgumentException("L'identifiant et le locataire sont obligatoires.");
        }
        locatairesParId.put(id, locataire);
    }

    /**
     * Retourne une copie de la liste des biens (jamais null, éventuellement vide).
     *
     * @return la liste des biens
     */
    public List<Bien> getBiens() {
        return new ArrayList<>(biens);
    }

    /**
     * Retourne une copie de la liste des baux (jamais null, éventuellement vide).
     *
     * @return la liste des baux
     */
    public List<Bail> getBaux() {
        return new ArrayList<>(baux);
    }

    /**
     * Signe un bail sur un bien.
     *
     * <p><b>Préconditions</b> : bien, locataire, dateDebut et dateFin non
     * null ; dateFin strictement postérieure à dateDebut.</p>
     * <p><b>Postconditions</b> : si aucune exception n'est levée, un bail
     * actif est ajouté au registre et sa période ne chevauche celle d'aucun
     * autre bail actif du même bien. Si une exception est levée, le registre
     * des baux n'est pas modifié.</p>
     *
     * @param bien bien à louer
     * @param locataire locataire signataire
     * @param gestionnaire gestionnaire responsable (peut être null)
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return le bail signé (jamais null)
     * @throws BienIndisponibleException si le bien est déjà loué sur une
     *      période qui chevauche la demande
     * @throws IllegalArgumentException si une précondition n'est pas respectée
     */
    public Bail signerBail(Bien bien, Locataire locataire, Gestionnaire gestionnaire,
                           LocalDate dateDebut, LocalDate dateFin) throws BienIndisponibleException {
        if (bien == null) {
            throw new IllegalArgumentException("Le bien ne peut pas être null.");
        }
        if (locataire == null) {
            throw new IllegalArgumentException("Le locataire ne peut pas être null.");
        }
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }

        boolean signe = false;
        try {
            for (Bail existant : baux) {
                if (existant.getBien() == bien && existant.chevauche(dateDebut, dateFin)) {
                    throw new BienIndisponibleException("Le bien '" + bien.getAdresse()
                            + "' est déjà loué sur une période qui chevauche la demande.");
                }
            }
            Bail nouveau = new Bail(bien, locataire, gestionnaire, dateDebut, dateFin);
            baux.add(nouveau);
            signe = true;
            return nouveau;
        } catch (BienIndisponibleException e) {
            LOGGER.log(Level.WARNING, "Signature refusée : {0}", e.getMessage());
            throw e;
        } finally {
            LOGGER.log(Level.INFO, "Journal d''activité : demande de bail sur ''{0}'' terminée ({1}).",
                    new Object[] {bien.getAdresse(), signe ? "signé" : "non signé"});
        }
    }

    /**
     * Clôture un bail.
     *
     * <p><b>Précondition</b> : bail non null et non déjà clôturé.</p>
     * <p><b>Postcondition</b> : le bail est clôturé et ne bloque plus son bien.</p>
     *
     * @param bail bail à clôturer
     * @throws IllegalArgumentException si bail est null
     * @throws IllegalStateException si le bail est déjà clôturé (cas limite :
     *      double clôture)
     */
    public void cloturerBail(Bail bail) {
        if (bail == null) {
            throw new IllegalArgumentException("Le bail ne peut pas être null.");
        }
        bail.cloturer();

        // Invariant interne : après cloturer() sans exception, le bail est forcément
        // clôturé. Ce n'est pas une saisie utilisateur mais une garantie que notre propre
        // code doit tenir : un assert convient, pas une IllegalArgumentException.
        assert bail.isCloture() : "Le bail devrait être clôturé après cloturer().";

        LOGGER.log(Level.INFO, "Bail clôturé pour le bien ''{0}''.", bail.getBien().getAdresse());
    }

    /**
     * Calcule et émet la quittance d'un bail, via le calcul polymorphe de
     * {@link ServiceQuittancement} (aucun {@code instanceof}).
     *
     * <p><b>Préconditions</b> : bail non null et non clôturé ; dateEmission
     * non null.</p>
     * <p><b>Postconditions</b> : la quittance retournée a au moins une ligne
     * et un montant total strictement positif.</p>
     *
     * @param bail bail concerné
     * @param dateEmission date d'émission
     * @return la quittance émise (jamais null)
     * @throws IllegalArgumentException si une précondition n'est pas respectée
     */
    public Quittance calculerQuittance(Bail bail, LocalDate dateEmission) {
        if (bail == null) {
            throw new IllegalArgumentException("Le bail ne peut pas être null.");
        }
        if (dateEmission == null) {
            throw new IllegalArgumentException("La date d'émission est obligatoire.");
        }
        if (bail.isCloture()) {
            throw new IllegalArgumentException("Impossible d'émettre une quittance pour un bail clôturé.");
        }

        Quittance quittance = quittancement.genererQuittance(bail, dateEmission);

        // Invariant interne : les taux de charges sont positifs, donc le montant calculé
        // est toujours > 0. Cela dépend de notre code, pas d'une saisie : assert.
        assert quittance.getMontantTotal() > 0 : "Le montant d'une quittance doit être positif.";

        return quittance;
    }

    /**
     * Crée l'état des lieux d'un bail.
     *
     * <p><b>Préconditions</b> : bail et dateRealisation non null ;
     * dateRealisation non antérieure au début du bail (cas limite) et non
     * future (vérifié par {@link EtatDesLieux}).</p>
     * <p><b>Postcondition</b> : l'état des lieux porte sur le bien du bail.</p>
     *
     * @param bail bail concerné
     * @param dateRealisation date de réalisation
     * @param defauts défauts constatés (null accepté, traité comme liste vide)
     * @return l'état des lieux créé (jamais null)
     * @throws IllegalArgumentException si une précondition n'est pas respectée
     */
    public EtatDesLieux creerEtatDesLieux(Bail bail, LocalDate dateRealisation, List<String> defauts) {
        if (bail == null) {
            throw new IllegalArgumentException("Le bail ne peut pas être null.");
        }
        if (dateRealisation == null) {
            throw new IllegalArgumentException("La date de réalisation est obligatoire.");
        }
        if (dateRealisation.isBefore(bail.getDateDebut())) {
            throw new IllegalArgumentException(
                    "L'état des lieux ne peut pas être daté avant le début du bail.");
        }
        return new EtatDesLieux(bail.getBien(), dateRealisation, defauts);
    }

    /**
     * Recherche un bien par fragment d'adresse (insensible à la casse).
     *
     * <p>Retourne un {@link Optional} : ne rien trouver est un résultat
     * normal d'une recherche, pas une erreur métier.</p>
     *
     * @param fragmentAdresse fragment recherché, non null
     * @return le premier bien correspondant, ou {@code Optional.empty()}
     * @throws IllegalArgumentException si fragmentAdresse est null
     */
    public Optional<Bien> rechercherBienParAdresse(String fragmentAdresse) {
        if (fragmentAdresse == null) {
            throw new IllegalArgumentException("Le fragment d'adresse ne peut pas être null.");
        }
        String recherche = fragmentAdresse.toLowerCase();
        return biens.stream()
                .filter(b -> b.getAdresse() != null && b.getAdresse().toLowerCase().contains(recherche))
                .findFirst();
    }

    /**
     * Recherche un locataire par identifiant.
     *
     * <p>Ici l'absence est une <b>erreur métier</b> (un identifiant est censé
     * désigner un locataire existant) : on lève une exception au lieu de
     * renvoyer un {@code Optional}.</p>
     *
     * @param id identifiant recherché, non null
     * @return le locataire trouvé (jamais null)
     * @throws LocataireIntrouvableException si aucun locataire n'a cet identifiant
     * @throws IllegalArgumentException si id est null
     */
    public Locataire rechercherLocataireParId(Long id) throws LocataireIntrouvableException {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant ne peut pas être null.");
        }
        Locataire trouve = locatairesParId.get(id);
        if (trouve == null) {
            LOGGER.log(Level.WARNING, "Locataire introuvable pour l''identifiant {0}.", id);
            throw new LocataireIntrouvableException("Aucun locataire trouvé pour l'identifiant " + id);
        }
        return trouve;
    }

    /**
     * Importe des biens depuis un fichier texte, un bien par ligne, au format
     * {@code Type;adresse;surfaceM2;loyerBase}. Une ligne mal formée est
     * journalisée en WARNING et ignorée ; un fichier illisible est journalisé
     * en SEVERE.
     *
     * @param cheminFichier chemin du fichier, non vide
     * @return les biens importés (jamais null, éventuellement vide)
     * @throws IllegalArgumentException si le chemin est vide ou null
     */
    public List<Bien> importerBiensDepuisFichier(String cheminFichier) {
        if (cheminFichier == null || cheminFichier.isBlank()) {
            throw new IllegalArgumentException("Le chemin du fichier ne peut pas être vide.");
        }

        List<Bien> importes = new ArrayList<>();
        int numeroLigne = 0;

        try (BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                numeroLigne++;
                if (ligne.isBlank()) {
                    continue;
                }
                try {
                    Bien bien = parserLigne(ligne);
                    biens.add(bien);
                    importes.add(bien);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Ligne {0} ignorée : {1}",
                            new Object[] {numeroLigne, e.getMessage()});
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Import impossible, fichier illisible : " + cheminFichier, e);
        }

        return importes;
    }

    private Bien parserLigne(String ligne) {
        String[] champs = ligne.split(";");
        if (champs.length != 4) {
            throw new IllegalArgumentException("format attendu Type;adresse;surface;loyer, reçu : " + ligne);
        }
        String type = champs[0].trim();
        String adresse = champs[1].trim();
        double surface = Double.parseDouble(champs[2].trim());
        double loyer = Double.parseDouble(champs[3].trim());

        switch (type) {
            case "Appartement":
                return new Appartement(adresse, surface, loyer);
            case "Maison":
                return new Maison(adresse, surface, loyer);
            case "LocalCommercial":
                return new LocalCommercial(adresse, surface, loyer);
            default:
                throw new IllegalArgumentException("type de bien inconnu : " + type);
        }
    }
}