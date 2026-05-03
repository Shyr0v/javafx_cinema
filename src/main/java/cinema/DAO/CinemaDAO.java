package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cinema.BO.Cinema;

public class CinemaDAO extends DAO<Cinema> {

    @Override
    public boolean create(Cinema obj) {
        boolean result = false;

        try {
            String sql = "INSERT INTO cinema(denomination, adresse, ville, id_franchise) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = this.connect.prepareStatement(sql);

            ps.setString(1, obj.getDenomination());
            ps.setString(2, obj.getAdresse());
            ps.setString(3, obj.getVille());
            ps.setInt(4, obj.getIdFranchise());

            result = ps.executeUpdate() > 0;

            if (result) {
                LogDAO.log(
                        "cinema",
                        "INSERT",
                        "",
                        formatCinema(obj)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean delete(Cinema obj) {
        boolean result = false;

        try {
            Cinema ancienCinema = find(obj.getIdCinema());

            String sql = "DELETE FROM cinema WHERE id_cinema = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, obj.getIdCinema());

            result = ps.executeUpdate() > 0;

            if (result && ancienCinema != null) {
                LogDAO.log(
                        "cinema",
                        "DELETE",
                        formatCinema(ancienCinema),
                        ""
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean update(Cinema obj) {
        boolean result = false;

        try {
            Cinema ancienCinema = find(obj.getIdCinema());

            String sql = "UPDATE cinema SET denomination = ?, adresse = ?, ville = ?, id_franchise = ? WHERE id_cinema = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);

            ps.setString(1, obj.getDenomination());
            ps.setString(2, obj.getAdresse());
            ps.setString(3, obj.getVille());
            ps.setInt(4, obj.getIdFranchise());
            ps.setInt(5, obj.getIdCinema());

            result = ps.executeUpdate() > 0;

            if (result && ancienCinema != null) {
                LogDAO.log(
                        "cinema",
                        "UPDATE",
                        formatCinema(ancienCinema),
                        formatCinema(obj)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Cinema find(int idCinema) {
        Cinema cinema = null;

        try {
            String sql = "SELECT * FROM cinema WHERE id_cinema = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idCinema);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cinema = hydrate(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cinema;
    }

    @Override
    public List<Cinema> findAll() {
        List<Cinema> cinemas = new ArrayList<>();

        try {
            String sql = "SELECT * FROM cinema";
            Statement statement = this.connect.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                cinemas.add(hydrate(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cinemas;
    }

    private Cinema hydrate(ResultSet rs) throws SQLException {
        return new Cinema(
                rs.getInt("id_cinema"),
                rs.getString("denomination"),
                rs.getString("adresse"),
                rs.getString("ville"),
                rs.getInt("id_franchise")
        );
    }

    private String formatCinema(Cinema cinema) {
        return "ID=" + cinema.getIdCinema()
                + ", Denomination=" + cinema.getDenomination()
                + ", Adresse=" + cinema.getAdresse()
                + ", Ville=" + cinema.getVille()
                + ", IdFranchise=" + cinema.getIdFranchise();
    }
}