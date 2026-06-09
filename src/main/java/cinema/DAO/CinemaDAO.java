package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cinema.BO.Cinema;

/**
 * DAO gérant les opérations CRUD sur la table "cinema".
 * Hérite de DAO<Cinema> et implémente les 5 méthodes abstraites.
 * Chaque opération de modification (create, update, delete) appelle LogDAO.log()
 * pour tracer l'action dans la table de logs.
 */
public class CinemaDAO extends DAO<Cinema> {

    /**
     * Insère un nouveau cinéma en base de données.
     * L'id_cinema est géré par l'auto-incrément SQL, pas besoin de le passer.
     * @param obj Le cinéma à insérer (obj.getIdCinema() ignoré, doit être 0)
     * @return true si l'insertion a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean create(Cinema obj) {
        boolean result = false;

        try {
            String sql = "INSERT INTO cinema(denomination, adresse, ville, id_franchise) VALUES (?, ?, ?, ?)";
            // PreparedStatement protège contre les injections SQL en paramétrant les valeurs
            PreparedStatement ps = this.connect.prepareStatement(sql);

            ps.setString(1, obj.getDenomination());
            ps.setString(2, obj.getAdresse());
            ps.setString(3, obj.getVille());
            ps.setInt(4, obj.getIdFranchise());

            result = ps.executeUpdate() > 0; // executeUpdate retourne le nombre de lignes insérées

            if (result) {
                // Log de l'insertion : ancienContenu vide car l'objet n'existait pas avant
                LogDAO.log("cinema", "INSERT", "", formatCinema(obj));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Supprime un cinéma de la base de données par son id.
     * Récupère l'état actuel AVANT la suppression pour le stocker dans le log.
     * @param obj Le cinéma à supprimer (seul obj.getIdCinema() est utilisé)
     * @return true si la suppression a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean delete(Cinema obj) {
        boolean result = false;

        try {
            // On sauvegarde l'état avant suppression pour le log
            Cinema ancienCinema = find(obj.getIdCinema());

            String sql = "DELETE FROM cinema WHERE id_cinema = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, obj.getIdCinema());

            result = ps.executeUpdate() > 0;

            if (result && ancienCinema != null) {
                // Log de la suppression : nouveauContenu vide car l'objet n'existe plus
                LogDAO.log("cinema", "DELETE", formatCinema(ancienCinema), "");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Met à jour un cinéma existant en base de données.
     * Récupère l'état AVANT modification pour le comparer dans le log.
     * @param obj Le cinéma avec les nouvelles valeurs (obj.getIdCinema() identifie la ligne)
     * @return true si la mise à jour a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean update(Cinema obj) {
        boolean result = false;

        try {
            // On sauvegarde l'état avant modification pour le log
            Cinema ancienCinema = find(obj.getIdCinema());

            String sql = "UPDATE cinema SET denomination = ?, adresse = ?, ville = ?, id_franchise = ? WHERE id_cinema = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);

            ps.setString(1, obj.getDenomination());
            ps.setString(2, obj.getAdresse());
            ps.setString(3, obj.getVille());
            ps.setInt(4, obj.getIdFranchise());
            ps.setInt(5, obj.getIdCinema()); // l'id va dans le WHERE, jamais dans le SET

            result = ps.executeUpdate() > 0;

            if (result && ancienCinema != null) {
                // Log avec l'état avant et après pour tracer la modification
                LogDAO.log("cinema", "UPDATE", formatCinema(ancienCinema), formatCinema(obj));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Recherche un cinéma par son identifiant.
     * @param idCinema L'identifiant du cinéma à retrouver
     * @return Le cinéma trouvé, ou null si aucune ligne ne correspond
     */
    @Override
    public Cinema find(int idCinema) {
        Cinema cinema = null;

        try {
            String sql = "SELECT * FROM cinema WHERE id_cinema = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idCinema);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cinema = hydrate(rs); // construit l'objet Cinema depuis le ResultSet
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cinema;
    }

    /**
     * Retourne tous les cinémas de la base de données.
     * Utilise Statement (sans paramètre) car la requête ne nécessite pas de filtre.
     * @return Liste de tous les cinémas, vide si la table est vide
     */
    @Override
    public List<Cinema> findAll() {
        List<Cinema> cinemas = new ArrayList<>();

        try {
            String sql = "SELECT * FROM cinema";
            Statement statement = this.connect.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                cinemas.add(hydrate(rs)); // construit et ajoute chaque cinéma à la liste
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cinemas;
    }

    public int countByFranchise(int idFranchise) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM cinema WHERE id_franchise = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idFranchise);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Construit un objet Cinema à partir d'une ligne du ResultSet.
     * Centralisé pour éviter de dupliquer la lecture des colonnes dans find() et findAll().
     * @param rs Le ResultSet positionné sur la ligne à lire
     * @return Un objet Cinema hydraté avec les valeurs de la base
     */
    private Cinema hydrate(ResultSet rs) throws SQLException {
        return new Cinema(
                rs.getInt("id_cinema"),
                rs.getString("denomination"),
                rs.getString("adresse"),
                rs.getString("ville"),
                rs.getInt("id_franchise")
        );
    }

    /**
     * Formate les données d'un cinéma en chaîne lisible pour les logs.
     * @param cinema Le cinéma à formater
     * @return Chaîne de type "ID=1, Denomination=UGC, Adresse=..., Ville=..., IdFranchise=2"
     */
    private String formatCinema(Cinema cinema) {
        return "ID=" + cinema.getIdCinema()
                + ", Denomination=" + cinema.getDenomination()
                + ", Adresse=" + cinema.getAdresse()
                + ", Ville=" + cinema.getVille()
                + ", IdFranchise=" + cinema.getIdFranchise();
    }
}