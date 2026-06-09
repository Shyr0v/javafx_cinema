package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cinema.BO.Salle;

/**
 * DAO gérant les opérations CRUD sur la table "salle".
 * Hérite de DAO<Salle> et implémente les 5 méthodes abstraites.
 * Chaque opération de modification (create, update, delete) appelle LogDAO.log()
 * pour tracer l'action dans la table de logs.
 * Fournit aussi findByCinema() pour filtrer les salles par cinéma.
 */
public class SalleDAO extends DAO<Salle> {

    /**
     * Insère une nouvelle salle en base de données.
     * @param obj La salle à insérer (obj.getIdSalle() ignoré, doit être 0)
     * @return true si l'insertion a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean create(Salle obj) {
        boolean result = false;
        try {
            String query = "INSERT INTO salle (numero, description, nb_places, id_cinema) VALUES (?,?,?,?);";
            // PreparedStatement protège contre les injections SQL en paramétrant les valeurs
            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, obj.getNumero());
            ps.setString(2, obj.getDescription());
            ps.setInt(3, obj.getNbPlaces());
            ps.setInt(4, obj.getIdCinema());

            if (ps.executeUpdate() > 0) {
                result = true;
                // Log de l'insertion : ancienContenu vide car l'objet n'existait pas avant
                LogDAO.log("salle", "INSERT", "", formatSalle(obj));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Supprime une salle de la base de données par son id.
     * Utilise try-with-resources pour fermer automatiquement le PreparedStatement.
     * @param obj La salle à supprimer (seul obj.getIdSalle() est utilisé)
     * @return true si la suppression a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean delete(Salle obj) {
        boolean result = false;
        String query = "DELETE FROM salle WHERE id_salle = ?;";

        // try-with-resources : ferme automatiquement le PreparedStatement à la fin du bloc
        try (PreparedStatement ps = this.connect.prepareStatement(query)) {
            // On sauvegarde l'état avant suppression pour le log
            Salle ancienneSalle = find(obj.getIdSalle());

            ps.setInt(1, obj.getIdSalle());
            result = ps.executeUpdate() > 0; // true si au moins une ligne supprimée

            if (result && ancienneSalle != null) {
                // Log de la suppression : nouveauContenu vide car l'objet n'existe plus
                LogDAO.log("salle", "DELETE", formatSalle(ancienneSalle), "");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Met à jour une salle existante en base de données.
     * @param obj La salle avec les nouvelles valeurs (obj.getIdSalle() identifie la ligne)
     * @return true si la mise à jour a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean update(Salle obj) {
        boolean result = false;
        String query = "UPDATE salle SET numero = ?, description = ?, nb_places = ?, id_cinema = ? WHERE id_salle = ?;";

        try {
            // On sauvegarde l'état avant modification pour le log
            Salle ancienneSalle = find(obj.getIdSalle());

            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, obj.getNumero());
            ps.setString(2, obj.getDescription());
            ps.setInt(3, obj.getNbPlaces());
            ps.setInt(4, obj.getIdCinema());
            ps.setInt(5, obj.getIdSalle()); // l'id va en position 5 dans le WHERE, jamais dans le SET

            if (ps.executeUpdate() > 0) {
                result = true;

                if (ancienneSalle != null) {
                    // Log avec l'état avant et après pour tracer la modification
                    LogDAO.log("salle", "UPDATE", formatSalle(ancienneSalle), formatSalle(obj));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Recherche une salle par son identifiant.
     * @param id L'identifiant de la salle à retrouver
     * @return La salle trouvée, ou null si aucune ligne ne correspond
     */
    @Override
    public Salle find(int id) {
        Salle salle = null;
        String query = "SELECT * FROM salle WHERE id_salle = ?;";

        try {
            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // rs.next() positionne sur la première ligne, retourne false si vide
                salle = new Salle(
                        rs.getInt("id_salle"),
                        rs.getInt("numero"),
                        rs.getString("description"),
                        rs.getInt("nb_places"),
                        rs.getInt("id_cinema"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salle;
    }

    /**
     * Retourne toutes les salles de la base de données.
     * Utilise try-with-resources pour fermer automatiquement PreparedStatement et ResultSet.
     * @return Liste de toutes les salles, vide si la table est vide
     */
    @Override
    public List<Salle> findAll() {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM salle;";

        // try-with-resources sur ps ET rs : les deux sont fermés automatiquement
        try (PreparedStatement ps = this.connect.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) { // while car plusieurs lignes possibles
                salles.add(new Salle(
                        rs.getInt("id_salle"),
                        rs.getInt("numero"),
                        rs.getString("description"),
                        rs.getInt("nb_places"),
                        rs.getInt("id_cinema")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salles;
    }

    /**
     * Retourne toutes les salles appartenant à un cinéma donné.
     * Méthode spécifique (non héritée de DAO) pour filtrer par cinéma.
     * @param idCinema L'identifiant du cinéma dont on veut les salles
     * @return Liste des salles du cinéma, vide si le cinéma n'a pas de salle
     */
    public List<Salle> findByCinema(int idCinema) {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM salle WHERE id_cinema = ?;";

        try (PreparedStatement ps = this.connect.prepareStatement(query)) {
            ps.setInt(1, idCinema);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                salles.add(new Salle(
                        rs.getInt("id_salle"),
                        rs.getInt("numero"),
                        rs.getString("description"),
                        rs.getInt("nb_places"),
                        rs.getInt("id_cinema")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salles;
    }

    public int countByCinema(int idCinema) {
        int count = 0;  // valeur par defaut si erreur
        try {
            String sql = "SELECT COUNT(*) FROM salle WHERE id_cinema = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idCinema);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);  // lit la premiere colonne du resultat
            }
        } catch (SQLException e) {
            e.printStackTrace();  // affiche l'erreur en console si probleme SQL
        }
        return count;
    }


    /**
     * Formate les données d'une salle en chaîne lisible pour les logs.
     * @param salle La salle à formater
     * @return Chaîne de type "ID=1, Numero=3, Description=IMAX, NbPlaces=200, IdCinema=2"
     */
    private String formatSalle(Salle salle) {
        return "ID=" + salle.getIdSalle()
                + ", Numero=" + salle.getNumero()
                + ", Description=" + salle.getDescription()
                + ", NbPlaces=" + salle.getNbPlaces()
                + ", IdCinema=" + salle.getIdCinema();
    }
}