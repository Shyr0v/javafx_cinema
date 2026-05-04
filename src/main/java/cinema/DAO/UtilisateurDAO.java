package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cinema.BO.Utilisateur;

/**
 * DAO gérant les opérations CRUD sur la table "utilisateur".
 * Hérite de DAO<Utilisateur> et implémente les 5 méthodes abstraites.
 * Fournit aussi authenticate() pour vérifier les identifiants de connexion
 * via la fonction pgcrypto de PostgreSQL (vérification du hash du mot de passe).
 */
public class UtilisateurDAO extends DAO<Utilisateur> {

    /**
     * Insère un nouvel utilisateur en base de données.
     * @param obj L'utilisateur à insérer (obj.getIdUtilisateur() ignoré, doit être 0)
     * @return true si l'insertion a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean create(Utilisateur obj) {
        boolean result = false;
        try {
            String sql = "INSERT INTO utilisateur(nom, prenom, login, mdp) VALUES(?,?,?,?)";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setString(1, obj.getNom());
            ps.setString(2, obj.getPrenom());
            ps.setString(3, obj.getLogin());
            ps.setString(4, obj.getMdp());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Supprime un utilisateur de la base de données par son id.
     * @param obj L'utilisateur à supprimer (seul obj.getIdUtilisateur() est utilisé)
     * @return true si la suppression a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean delete(Utilisateur obj) {
        boolean result = false;
        try {
            String sql = "DELETE FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, obj.getIdUtilisateur());

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Met à jour un utilisateur existant en base de données.
     * @param obj L'utilisateur avec les nouvelles valeurs (obj.getIdUtilisateur() identifie la ligne)
     * @return true si la mise à jour a réussi, false en cas d'erreur SQL
     */
    @Override
    public boolean update(Utilisateur obj) {
        boolean result = false;
        try {
            String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, login = ?, mdp = ? WHERE id_utilisateur = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setString(1, obj.getNom());
            ps.setString(2, obj.getPrenom());
            ps.setString(3, obj.getLogin());
            ps.setString(4, obj.getMdp());
            ps.setInt(5, obj.getIdUtilisateur()); // l'id va dans le WHERE, jamais dans le SET

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Construit un objet Utilisateur à partir d'une ligne du ResultSet.
     * Centralisé pour éviter de dupliquer la lecture des colonnes dans find(), findAll() et authenticate().
     * @param resultSet Le ResultSet positionné sur la ligne à lire
     * @return Un objet Utilisateur hydraté avec les valeurs de la base
     */
    private Utilisateur hydrate(ResultSet resultSet) throws SQLException {
        return new Utilisateur(
                resultSet.getInt("id_utilisateur"),
                resultSet.getString("nom"),
                resultSet.getString("prenom"),
                resultSet.getString("login"),
                resultSet.getString("mdp"));
    }

    /**
     * Retourne tous les utilisateurs de la base de données.
     * Utilisé notamment pour peupler les ListView de sélection de gérant.
     * @return Liste de tous les utilisateurs, vide si la table est vide
     */
    @Override
    public List<Utilisateur> findAll() {
        List<Utilisateur> mesUtilisateurs = new ArrayList<>();

        try {
            String sql = "SELECT * FROM utilisateur";
            Statement statement = this.connect.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                mesUtilisateurs.add(hydrate(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesUtilisateurs;
    }

    /**
     * Recherche un utilisateur par son identifiant.
     * @param idUtilisateur L'identifiant de l'utilisateur à retrouver
     * @return L'utilisateur trouvé, ou null si aucune ligne ne correspond
     */
    @Override
    public Utilisateur find(int idUtilisateur) {
        try {
            String sql = "SELECT * FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idUtilisateur);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                return hydrate(result);
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /**
     * Vérifie les identifiants de connexion et retourne l'utilisateur si valides.
     * Utilise la fonction pgcrypto de PostgreSQL : crypt(?, mdp) compare le mot de passe
     * saisi avec le hash stocké en base sans jamais manipuler le mot de passe en clair côté Java.
     * @param login    Le login saisi par l'utilisateur
     * @param password Le mot de passe saisi (en clair, pgcrypto gère la comparaison avec le hash)
     * @return L'utilisateur authentifié, ou null si les identifiants sont incorrects
     */
    public Utilisateur authenticate(String login, String password) {
        Utilisateur user = null;

        try {
            // crypt(?, mdp) : pgcrypto re-hashe le mot de passe saisi avec le sel du hash stocké
            // et compare les deux hashes — le mot de passe en clair ne transite jamais en SQL
            String sql = "SELECT * FROM utilisateur WHERE login = ? AND mdp = crypt(?, mdp)";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setString(1, login);
            ps.setString(2, password); // pgcrypto s'occupe du hachage côté PostgreSQL

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                user = hydrate(result); // identifiants corrects : retourne l'utilisateur
            }
            // si result est vide, user reste null → identifiants incorrects

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}