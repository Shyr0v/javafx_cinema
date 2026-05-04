package cinema.DAO;

import java.sql.Connection;
import java.util.List;

/**
 * Classe abstraite générique définissant le contrat de tous les DAO de l'application.
 * Utilise un paramètre de type générique T pour éviter de dupliquer cette structure
 * pour chaque entité (Cinema, Franchise, Salle, Utilisateur).
 *
 * Pattern DAO (Data Access Object) : sépare la logique d'accès aux données
 * des classes métier (BO) et des contrôleurs. Chaque DAO concret implémente
 * les 5 opérations CRUD pour son entité.
 *
 * @param <T> Le type de l'entité gérée (ex: Cinema, Franchise, Salle, Utilisateur)
 */
public abstract class DAO<T> {

    /**
     * Connexion à la base de données partagée entre tous les DAO.
     * DBManager.getInstance() retourne toujours la même connexion (pattern Singleton).
     * Protected pour être accessible directement dans les classes filles sans getter.
     */
    protected Connection connect = DBManager.getInstance();

    /**
     * Insère un nouvel objet en base de données.
     * @param obj L'objet à insérer (avec id = 0, l'auto-incrément SQL génère le vrai id)
     * @return true si l'insertion a réussi (au moins 1 ligne affectée), false sinon
     */
    public abstract boolean create(T obj);

    /**
     * Supprime un objet de la base de données.
     * @param obj L'objet à supprimer (seul l'id est utilisé pour le WHERE)
     * @return true si la suppression a réussi, false sinon
     */
    public abstract boolean delete(T obj);

    /**
     * Met à jour un objet existant en base de données.
     * @param obj L'objet avec les nouvelles valeurs (l'id identifie la ligne à modifier)
     * @return true si la mise à jour a réussi, false sinon
     */
    public abstract boolean update(T obj);

    /**
     * Recherche un objet par son identifiant.
     * @param id L'identifiant de l'objet à retrouver
     * @return L'objet trouvé, ou null si aucune ligne ne correspond à cet id
     */
    public abstract T find(int id);

    /**
     * Retourne tous les objets de la table correspondante.
     * @return Liste de tous les objets, vide si la table est vide
     */
    public abstract List<T> findAll();
}