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
        boolean result = false; // par défaut l'ajout échoue
        try {
            String query = "INSERT INTO salle (numero, description, nb_places, id_cinema) VALUES (?,?,?,?);"; // requête d'insertion
            PreparedStatement ps = this.connect.prepareStatement(query); // prépare la requête pour éviter les injections SQL
            ps.setInt(1, obj.getNumero()); // remplace le 1er ? par le numéro
            ps.setString(2, obj.getDescription()); // remplace le 2ème ? par la description
            ps.setInt(3, obj.getNbPlaces()); // remplace le 3ème ? par le nombre de places
            ps.setInt(4, obj.getIdCinema()); // remplace le 4ème ? par l'id du cinéma
            if (ps.executeUpdate() > 0) { result = true; } // executeUpdate retourne le nombre de lignes insérées
        } catch (SQLException e) { e.printStackTrace(); } // affiche l'erreur SQL en console
        return result; // retourne true si insertion réussie, false sinon
    }

    @Override
    public boolean delete(Salle obj) {
        boolean result = false; // par défaut la suppression échoue
        String query = "DELETE FROM salle WHERE id_salle = ?;"; // requête de suppression par id
        try (PreparedStatement ps = this.connect.prepareStatement(query)) { // try-with-resources ferme automatiquement le statement
            ps.setInt(1, obj.getIdSalle()); // remplace le ? par l'id de la salle à supprimer
            result = ps.executeUpdate() > 0; // true si au moins une ligne supprimée
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public boolean update(Salle obj) {
        boolean result = false; // par défaut la mise à jour échoue
        String query = "UPDATE salle SET numero = ?, description = ?, nb_places = ?, id_cinema = ? WHERE id_salle = ?;"; // requête de mise à jour
        try {
            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, obj.getNumero()); // nouveau numéro
            ps.setString(2, obj.getDescription()); // nouvelle description
            ps.setInt(3, obj.getNbPlaces()); // nouveau nombre de places
            ps.setInt(4, obj.getIdCinema()); // nouveau cinéma
            ps.setInt(5, obj.getIdSalle()); // id en position 5 pour le WHERE, jamais dans le SET
            if (ps.executeUpdate() > 0) { result = true; }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public Salle find(int id) {
        Salle salle = null; // null par défaut si aucune salle trouvée
        String query = "SELECT * FROM salle WHERE id_salle = ?;"; // recherche par id
        try {
            PreparedStatement ps = this.connect.prepareStatement(query);
            ps.setInt(1, id); // id de la salle recherchée
            ResultSet rs = ps.executeQuery(); // exécute et récupère les résultats
            if (rs.next()) { // rs.next() positionne sur la première ligne, retourne false si vide
                salle = new Salle(
                        rs.getInt("id_salle"), // lit la colonne id_salle
                        rs.getInt("numero"), // lit la colonne numero
                        rs.getString("description"), // lit la colonne description
                        rs.getInt("nb_places"), // lit la colonne nb_places
                        rs.getInt("id_cinema")); // lit la colonne id_cinema
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return salle; // retourne la salle ou null
    }

    @Override
    public List<Salle> findAll() {
        List<Salle> salles = new ArrayList<>(); // liste vide qui va recevoir toutes les salles
        String query = "SELECT * FROM salle;"; // récupère toutes les lignes de la table
        try (PreparedStatement ps = this.connect.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) { // exécute directement car pas de paramètre
            while (rs.next()) { // while car plusieurs lignes possibles, contrairement à find() qui utilise if
                salles.add(new Salle(
                        rs.getInt("id_salle"),
                        rs.getInt("numero"),
                        rs.getString("description"),
                        rs.getInt("nb_places"),
                        rs.getInt("id_cinema")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return salles; // retourne la liste complète
    }

    public List<Salle> findByCinema(int idCinema) {
        List<Salle> salles = new ArrayList<>(); // liste des salles filtrées par cinéma
        String query = "SELECT * FROM salle WHERE id_cinema = ?;"; // même logique que findAll mais avec filtre
        try (PreparedStatement ps = this.connect.prepareStatement(query)) {
            ps.setInt(1, idCinema); // filtre par l'id du cinéma
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