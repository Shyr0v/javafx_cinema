package cinema.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_cinema";
    private static final String USER = "postgres";
    private static final String PASS = "root";

    private static Connection connect;

    public static Connection getInstance() {
        if (connect == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connect = DriverManager.getConnection(URL, USER, PASS);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Driver PostgreSQL introuvable", e);
            } catch (SQLException e) {
                throw new RuntimeException("Connexion BD impossible", e);
            }
        }
        return connect;
    }
}