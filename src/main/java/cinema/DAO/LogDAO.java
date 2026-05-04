package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.Types;

import cinema.BO.Utilisateur;
import cinema.Session;

/**
 * DAO dédié à l'insertion des entrées dans la table de logs.
 * Contrairement aux autres DAO, LogDAO n'hérite pas de DAO<T> car il n'a pas
 * besoin des opérations CRUD complètes : on écrit des logs, on ne les modifie pas.
 *
 * Chaque opération (INSERT, UPDATE, DELETE) dans les autres DAO appelle LogDAO.log()
 * pour tracer qui a fait quoi et sur quelle donnée.
 */
public class LogDAO {

    /**
     * Insère une entrée dans la table log.
     * Récupère automatiquement l'utilisateur connecté depuis Session pour l'associer au log.
     * Si aucun utilisateur n'est en session (cas théorique), l'id est laissé à NULL en base.
     *
     * @param tableName      Nom de la table impactée (ex: "cinema", "franchise", "salle")
     * @param operation      Type d'opération effectuée : "INSERT", "UPDATE" ou "DELETE"
     * @param ancienContenu  État de l'objet AVANT l'opération (vide pour INSERT)
     * @param nouveauContenu État de l'objet APRÈS l'opération (vide pour DELETE)
     */
    public static void log(String tableName, String operation, String ancienContenu, String nouveauContenu) {
        try {
            // Requête d'insertion dans la table log
            // Text Block Java (""") pour la lisibilité des requêtes multi-lignes
            String sql = """
                    INSERT INTO log(table_name, operation, ancien_contenu, nouveau_contenu, id_utilisateur)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            PreparedStatement ps = DBManager.getInstance().prepareStatement(sql);

            ps.setString(1, tableName);
            ps.setString(2, operation);
            ps.setString(3, ancienContenu);  // vide ("") pour INSERT
            ps.setString(4, nouveauContenu); // vide ("") pour DELETE

            // Récupère l'utilisateur depuis la session globale
            Utilisateur utilisateur = Session.getUtilisateur();

            if (utilisateur != null) {
                ps.setInt(5, utilisateur.getIdUtilisateur()); // id de l'utilisateur connecté
            } else {
                ps.setNull(5, Types.INTEGER); // NULL si aucun utilisateur en session
            }

            ps.executeUpdate(); // exécute l'insertion du log

        } catch (Exception e) {
            // Un échec de log ne doit pas bloquer l'opération principale
            e.printStackTrace();
        }
    }
}