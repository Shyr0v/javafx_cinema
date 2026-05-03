package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cinema.BO.Franchise;

public class FranchiseDAO extends DAO<Franchise> {

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
                LogDAO.log(
                        "franchise",
                        "INSERT",
                        "",
                        formatFranchise(obj)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean delete(Franchise obj) {
        boolean result = false;

        try {
            Franchise ancienneFranchise = find(obj.getIdFranchise());

            String sql = "DELETE FROM franchise WHERE id_franchise = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, obj.getIdFranchise());

            result = ps.executeUpdate() > 0;

            if (result && ancienneFranchise != null) {
                LogDAO.log(
                        "franchise",
                        "DELETE",
                        formatFranchise(ancienneFranchise),
                        ""
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean update(Franchise obj) {
        boolean result = false;

        try {
            Franchise ancienneFranchise = find(obj.getIdFranchise());

            String sql = "UPDATE franchise SET nom_franchise = ?, siege_social = ?, id_gerant = ? WHERE id_franchise = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);

            ps.setString(1, obj.getNomFranchise());
            ps.setString(2, obj.getSiegeSocial());
            ps.setInt(3, obj.getIdGerant());
            ps.setInt(4, obj.getIdFranchise());

            result = ps.executeUpdate() > 0;

            if (result && ancienneFranchise != null) {
                LogDAO.log(
                        "franchise",
                        "UPDATE",
                        formatFranchise(ancienneFranchise),
                        formatFranchise(obj)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Franchise find(int idFranchise) {
        Franchise franchise = null;

        try {
            String sql = "SELECT * FROM franchise WHERE id_franchise = ?";
            PreparedStatement ps = this.connect.prepareStatement(sql);
            ps.setInt(1, idFranchise);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                franchise = hydrate(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return franchise;
    }

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

    private Franchise hydrate(ResultSet rs) throws SQLException {
        return new Franchise(
                rs.getInt("id_franchise"),
                rs.getString("nom_franchise"),
                rs.getString("siege_social"),
                rs.getInt("id_gerant")
        );
    }

    private String formatFranchise(Franchise franchise) {
        return "ID=" + franchise.getIdFranchise()
                + ", Nom=" + franchise.getNomFranchise()
                + ", Siege=" + franchise.getSiegeSocial()
                + ", IdGerant=" + franchise.getIdGerant();
    }
}