package cinema.DAO;

import cinema.BO.Evenement;
import java.sql.*;
import java.util.List;

public class EvenementDAO extends DAO<Evenement> {

    public boolean create(Evenement evenement) {
        String sql = "INSERT INTO evenement (nom, date, nbr_place, gratuit, id_cinema) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, evenement.getNom());
            stmt.setDate(2, Date.valueOf(evenement.getDate()));
            stmt.setInt(3, evenement.getNbrPlace());
            stmt.setBoolean(4, evenement.isGratuit());
            stmt.setInt(5, evenement.getCinema().getIdCinema());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    evenement.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Evenement obj) {
        return false;}

    @Override
    public boolean update(Evenement obj) {
        return false;}
    @Override public Evenement find(int id) {
        return null; }
    @Override public List<Evenement> findAll() {
        return null; }
}