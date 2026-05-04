package cinema.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion à la base de données PostgreSQL.
 * Implémente le pattern Singleton : une seule connexion est créée pour toute l'application.
 * Tous les DAO récupèrent cette même connexion via DBManager.getInstance().
 *
 * Avantage du Singleton ici : évite d'ouvrir/fermer une connexion à chaque opération DAO,
 * ce qui serait coûteux en performance sur une application desktop.
 */
public class DBManager {

    // Paramètres de connexion à la base PostgreSQL
    private static final String URL  = "jdbc:postgresql://localhost:5432/gestion_cinema"; // adresse et nom de la base
    private static final String USER = "postgres"; // utilisateur PostgreSQL
    private static final String PASS = "root";     // mot de passe PostgreSQL

    /**
     * Instance unique de la connexion (Singleton).
     * Static pour n'exister qu'une seule fois en mémoire.
     * null au démarrage, initialisée au premier appel de getInstance().
     */
    private static Connection connect;

    /**
     * Retourne la connexion unique à la base de données.
     * Si la connexion n'existe pas encore, elle est créée (lazy initialization).
     * Class.forName() charge explicitement le driver PostgreSQL dans le classpath.
     *
     * @return La connexion active à la base de données
     * @throws RuntimeException Si le driver est introuvable ou si la connexion échoue
     */
    public static Connection getInstance() {
        if (connect == null) { // lazy init : connexion créée seulement au premier appel
            try {
                Class.forName("org.postgresql.Driver"); // charge le driver JDBC PostgreSQL
                connect = DriverManager.getConnection(URL, USER, PASS); // ouvre la connexion
            } catch (ClassNotFoundException e) {
                // Le jar du driver PostgreSQL n'est pas dans le classpath
                throw new RuntimeException("Driver PostgreSQL introuvable", e);
            } catch (SQLException e) {
                // Paramètres de connexion incorrects ou base inaccessible
                throw new RuntimeException("Connexion BD impossible", e);
            }
        }
        return connect; // retourne toujours la même instance
    }
}