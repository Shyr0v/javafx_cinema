package cinema.DAO;

import java.sql.*;

import cinema.BO.Salarie;

public class SalarieDAO {

    private Connection connect;

    public Salarie find(int id) {
        Salarie salarie = null;
        try {
            String sql = "SELECT * FROM salarie WHERE id_salarie = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                salarie = new Salarie(
                        rs.getInt("id_salarie"),
                        rs.getString("nom"),
                        rs.getString("prenom")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salarie; // retourne null si pas trouvé
    }

}
