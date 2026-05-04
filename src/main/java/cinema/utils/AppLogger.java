package cinema.utils;

import cinema.BO.Utilisateur;
import cinema.Session;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Utilitaire de journalisation dans un fichier texte (logs/application.log).
 * Complément à LogDAO qui lui écrit les logs en base de données :
 * AppLogger écrit les mêmes informations dans un fichier local pour un suivi hors base.
 *
 * Utilise l'API java.util.logging (JUL) intégrée à Java, sans dépendance externe.
 * Le bloc static initialise le FileHandler une seule fois au chargement de la classe.
 */
public class AppLogger {

    /**
     * Logger nommé "CinemaLogger". Le nom permet d'identifier la source dans les logs
     * et d'appliquer des configurations spécifiques si besoin.
     */
    private static final Logger logger = Logger.getLogger("CinemaLogger");

    /**
     * Bloc d'initialisation statique : exécuté une seule fois au premier accès à la classe.
     * Configure le FileHandler pour écrire dans logs/application.log avec un format personnalisé.
     */
    static {
        try {
            // Crée le dossier "logs" s'il n'existe pas encore
            File dossierLogs = new File("logs");
            if (!dossierLogs.exists()) {
                dossierLogs.mkdirs(); // mkdirs() crée aussi les dossiers parents si nécessaire
            }

            // true = mode append : on ajoute à la fin du fichier existant (pas d'écrasement)
            FileHandler fileHandler = new FileHandler("logs/application.log", true);

            // Formateur personnalisé : affiche [date/heure] NIVEAU - message
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return "[" + LocalDateTime.now() + "] "
                            + record.getLevel() + " - "
                            + record.getMessage() + System.lineSeparator();
                }
            });

            logger.addHandler(fileHandler);
            // setUseParentHandlers(false) : désactive la propagation vers la console système
            // sans ça, chaque log serait affiché deux fois (fichier + console)
            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enregistre une action utilisateur dans le fichier de log.
     * Récupère automatiquement l'utilisateur connecté depuis Session.
     * @param action  Verbe décrivant l'action (ex: "CREATE", "UPDATE", "DELETE")
     * @param entite  Entité concernée (ex: "Cinema", "Franchise", "Salle")
     * @param detail  Détail supplémentaire (ex: "ID=3, Denomination=UGC Nation")
     */
    public static void action(String action, String entite, String detail) {
        Utilisateur utilisateur = Session.getUtilisateur();

        // Construction de l'info utilisateur (inconnu si pas de session active)
        String userInfo = "Utilisateur inconnu";
        if (utilisateur != null) {
            userInfo = utilisateur.getIdUtilisateur() + " - "
                    + utilisateur.getNom() + " "
                    + utilisateur.getPrenom();
        }

        // Écriture de la ligne de log au niveau INFO
        logger.info("ACTION=" + action
                + " | ENTITE=" + entite
                + " | DETAIL=" + detail
                + " | USER=" + userInfo);
    }

    /**
     * Enregistre un message d'erreur dans le fichier de log au niveau SEVERE.
     * SEVERE est le niveau le plus critique dans java.util.logging.
     * @param message Description de l'erreur survenue
     */
    public static void erreur(String message) {
        logger.severe(message);
    }
}