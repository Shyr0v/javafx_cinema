package cinema.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale JavaFX. Hérite de Application et implémente start() qui est
 * le vrai point d'entrée du cycle de vie JavaFX (appelé après l'initialisation du runtime).
 * C'est ici que la fenêtre principale est créée et que la page de connexion est chargée.
 */
public class MainApplication extends Application {

    /**
     * Méthode appelée automatiquement par JavaFX après l'initialisation du runtime.
     * Charge la page de connexion, configure la fenêtre principale et l'affiche.
     * @param primaryStage Le Stage principal créé par JavaFX, représente la fenêtre principale
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Charge le fichier FXML de la page de connexion
            // FXMLLoader.load() instancie aussi le ConnexionController associé
            Parent parent = FXMLLoader.load(getClass().getResource("/cinema/views/page_connexion.fxml"));

            Scene scene = new Scene(parent);

            // Configuration de la fenêtre principale
            primaryStage.setTitle("Application de gestion de franchise - Authentification");
            primaryStage.setResizable(false); // taille fixe, pas de redimensionnement
            primaryStage.centerOnScreen();    // centré sur l'écran au démarrage

            // Application du logo dans la barre de titre
            primaryStage.getIcons().clear();
            primaryStage.getIcons().add(new Image("/cinema/images/cinema_logo.png"));

            primaryStage.setScene(scene);
            primaryStage.show(); // affiche la fenêtre

        } catch (IOException e) {
            e.printStackTrace(); // affiché en console si le fichier FXML est introuvable
        }
    }
}