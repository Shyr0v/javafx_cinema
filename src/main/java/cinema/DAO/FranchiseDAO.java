package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cinema.BO.Franchise;

/**
 * DAO gérant les opérations CRUD sur la table "franchise".
 * Hérite de DAO<Franchise> et implémente les 5 méthodes abstraites.
 * Chaque opération de modification (create, update, delete) appelle LogDAO.log()
 * pour tracer l'action dans la table de logs.
 * Fournit aussi getAllByGerant() pour filtrer les franchises par gérant.
 */
public class FranchiseDAO extends DAO<Franchise> {

    /**
     * Insère une nouvelle franchise en base de données.
     * @param obj La franchise à insérer (obj.getIdFranchise() ignoré, doit être 0)
     * @return true si l'insertion a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean create(Franchise obj) {
        boolean result = false;

        try {
            String sql = "INSERT INTO franchise(nom_franchise, siege_social, id_gerant) VALUES (?, ?, ?)";
            PreparedStatement ps = this.connect.prepareStatement(sql);

            ps.setString(1, obj.getNomFranchise());
            ps.setString(2, obj.getSiegeSocial());
            ps.setInt(3, obj.getIdGerant());

            result = ps.executeUpdate() > 0;

            if (result) {
                // Log de l'insertion : ancienContenu vide car l'objet n'existait pas avant
                LogDAO.log("franchise", "INSERT", "", formatFranchise(obj));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Supprime une franchise de la base de données par son id.
     * Note : dans ListeFranchiseController, on vérifie d'abord qu'aucun cinéma
     * ne référence cette franchise avant d'appeler delete().
     * @param obj La franchise à supprimer (seul obj.getIdFranchise() est utilisé)
     * @return true si la suppression a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean delete(Franchise obj) {
        boolean result = false;

        try {
            // On sauvegarde l'état avant suppression pour le log
            Franchise ancienneFranchise = find(obj.getIdFranchise());

            String sql = "DELETE FROM franchise WHERE id_franchise = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, obj.getIdFranchise());

            result = ps.executeUpdate() > 0;

            if (result && ancienneFranchise != null) {
                // Log de la suppression : nouveauContenu vide car l'objet n'existe plus
                LogDAO.log("franchise", "DELETE", formatFranchise(ancienneFranchise), "");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Met à jour une franchise existante en base de données.
     * @param obj La franchise avec les nouvelles valeurs (obj.getIdFranchise() identifie la ligne)
     * @return true si la mise à jour a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean update(Franchise obj) {
        boolean result = false;

        try {
            // On sauvegarde l'état avant modification pour le log
            Franchise ancienneFranchise = find(obj.getIdFranchise());

            String sql = "UPDATE franchise SET nom_franchise = ?, siege_social = ?, id_gerant = ? WHERE id_franchise = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);

            ps.setString(1, obj.getNomFranchise());
            ps.setString(2, obj.getSiegeSocial());
            ps.setInt(3, obj.getIdGerant());
            ps.setInt(4, obj.getIdFranchise()); // l'id va dans le WHERE, jamais dans le SET

            result = ps.executeUpdate() > 0;

            if (result && ancienneFranchise != null) {
                // Log avec l'état avant et après pour tracer la modification
                LogDAO.log("franchise", "UPDATE", formatFranchise(ancienneFranchise), formatFranchise(obj));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Recherche une franchise par son identifiant.
     * @param idFranchise L'identifiant de la franchise à retrouver
     * @return La franchise trouvée, ou null si aucune ligne ne correspond
     */
    @Override
    public Franchise find(int idFranchise) {
        Franchise franchise = null;

        try {
            String sql = "SELECT * FROM franchise WHERE id_franchise = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idFranchise);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                franchise = hydrate(rs); // construit l'objet Franchise depuis le ResultSet
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return franchise;
    }

    /**
     * Retourne toutes les franchises de la base de données.
     * @return Liste de toutes les franchises, vide si la table est vide
     */
    @Override
    public List<Franchise> findAll() {
        List<Franchise> franchises = new ArrayList<>();

        try {
            String sql = "SELECT * FROM franchise";
            Statement statement = this.connect.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                franchises.add(hydrate(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return franchises;
    }

    /**
     * Retourne toutes les franchises gérées par un utilisateur donné.
     * Méthode spécifique (non héritée de DAO) pour filtrer par gérant.
     * @param idGerant L'identifiant de l'utilisateur gérant
     * @return Liste des franchises dont le gérant correspond à l'id donné
     */
    public List<Franchise> getAllByGerant(int idGerant) {
        List<Franchise> franchises = new ArrayList<>();

        try {
            String sql = "SELECT * FROM franchise WHERE id_gerant = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idGerant);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                franchises.add(hydrate(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return franchises;
    }

    /**
     * Construit un objet Franchise à partir d'une ligne du ResultSet.
     * Centralisé pour éviter de dupliquer la lecture des colonnes dans find() et findAll().
     * @param rs Le ResultSet positionné sur la ligne à lire
     * @return Un objet Franchise hydraté avec les valeurs de la base
     */
    private Franchise hydrate(ResultSet rs) throws SQLException {
        return new Franchise(
                rs.getInt("id_franchise"),
                rs.getString("nom_franchise"),
                rs.getString("siege_social"),
                rs.getInt("id_gerant")
        );
    }

    /**
     * Formate les données d'une franchise en chaîne lisible pour les logs.
     * @param franchise La franchise à formater
     * @return Chaîne de type "ID=1, Nom=UGC, Siege=..., IdGerant=2"
     */
    private String formatFranchise(Franchise franchise) {
        return "ID=" + franchise.getIdFranchise()
                + ", Nom=" + franchise.getNomFranchise()
                + ", Siege=" + franchise.getSiegeSocial()
                + ", IdGerant=" + franchise.getIdGerant();
    }
}