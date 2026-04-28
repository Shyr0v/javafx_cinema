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

public class AppLogger {

    private static final Logger logger = Logger.getLogger("CinemaLogger");

    static {
        try {
            File dossierLogs = new File("logs");
            if (!dossierLogs.exists()) {
                dossierLogs.mkdirs();
            }

            FileHandler fileHandler = new FileHandler("logs/application.log", true);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return "[" + LocalDateTime.now() + "] "
                            + record.getLevel() + " - "
                            + record.getMessage() + System.lineSeparator();
                }
            });

            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void action(String action, String entite, String detail) {
        Utilisateur utilisateur = Session.getUtilisateur();

        String userInfo = "Utilisateur inconnu";

        if (utilisateur != null) {
            userInfo = utilisateur.getIdUtilisateur() + " - "
                    + utilisateur.getNom() + " "
                    + utilisateur.getPrenom();
        }

        logger.info("ACTION=" + action
                + " | ENTITE=" + entite
                + " | DETAIL=" + detail
                + " | USER=" + userInfo);
    }

    public static void erreur(String message) {
        logger.severe(message);
    }
}