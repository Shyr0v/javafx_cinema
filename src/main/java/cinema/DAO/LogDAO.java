package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.Types;

import cinema.BO.Utilisateur;
import cinema.Session;

public class LogDAO {

    public static void log(String tableName, String operation, String ancienContenu, String nouveauContenu) {
        try {
            String sql = """
                    INSERT INTO log(table_name, operation, ancien_contenu, nouveau_contenu, id_utilisateur)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            PreparedStatement ps = DBManager.getInstance().prepareStatement(sql);

            ps.setString(1, tableName);
            ps.setString(2, operation);
            ps.setString(3, ancienContenu);
            ps.setString(4, nouveauContenu);

            Utilisateur utilisateur = Session.getUtilisateur();

            if (utilisateur != null) {
                ps.setInt(5, utilisateur.getIdUtilisateur());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}