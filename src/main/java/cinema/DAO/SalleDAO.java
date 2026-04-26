package cinema.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cinema.BO.Salle;

public class SalleDAO extends DAO<Salle> {

    @Override
    public boolean create(Salle obj) {
        boolean result = false;
        try {
            String query = "INSERT INTO salle (numero, description, nb_places, id_cinema) VALUES (?,?,?,?);";
            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, obj.getNumero());
            ps.setString(2, obj.getDescription());
            ps.setInt(3, obj.getNbPlaces());
            ps.setInt(4, obj.getIdCinema());
            if (ps.executeUpdate() > 0) { result = true; }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public boolean delete(Salle obj) {
        boolean result = false;
        String query = "DELETE FROM salle WHERE id_salle = ?;";
        try (PreparedStatement ps = this.connect.prepareStatement(query)) {
            ps.setInt(1, obj.getIdSalle());
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public boolean update(Salle obj) {
        boolean result = false;
        String query = "UPDATE salle SET numero = ?, description = ?, nb_places = ?, id_cinema = ? WHERE id_salle = ?;";
        try {
            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, obj.getNumero());
            ps.setString(2, obj.getDescription());
            ps.setInt(3, obj.getNbPlaces());
            ps.setInt(4, obj.getIdCinema());
            ps.setInt(5, obj.getIdSalle());
            if (ps.executeUpdate() > 0) { result = true; }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public Salle find(int id) {
        Salle salle = null;
        String query = "SELECT * FROM salle WHERE id_salle = ?;";
        try {
            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                salle = new Salle(
                        rs.getInt("id_salle"),
                        rs.getInt("numero"),
                        rs.getString("description"),
                        rs.getInt("nb_places"),
                        rs.getInt("id_cinema"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return salle;
    }

    @Override
    public List<Salle> findAll() {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM salle;";
        try (PreparedStatement ps = this.connect.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                salles.add(new Salle(
                        rs.getInt("id_salle"),
                        rs.getInt("numero"),
                        rs.getString("description"),
                        rs.getInt("nb_places"),
                        rs.getInt("id_cinema")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return salles;
    }

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
        } catch (SQLException e) { e.printStackTrace(); }
        return salles;
    }

}