
/**
 * Contrat commun aux biens pouvant être loués meublés.
 *
 * <p>Seuls {@link Appartement} et {@link Maison} l'implémentent.
 * {@link LocalCommercial}, loué nu par nature, ne l'implémente pas : une
 * interface ne s'applique qu'aux classes pour lesquelles le comportement a
 * un sens, contrairement à l'héritage qui s'impose à toute la hiérarchie.</p>
 */
public interface Meublable {

    /**
     * Ajoute un équipement fourni avec le bien.
     *
     * @param designation nom de l'équipement, non vide
     * @throws IllegalArgumentException si designation est vide ou null
     */
    void ajouterEquipement(String designation);

    /**
     * Indique si le bien est meublé (au moins un équipement).
     *
     * @return true si au moins un équipement a été ajouté
     */
    boolean estMeuble();
}