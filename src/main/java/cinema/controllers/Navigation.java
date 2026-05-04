package cinema.controllers;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Classe utilitaire centralisée pour la navigation et l'application du logo.
 * Toutes les fenêtres de l'application passent par cette classe pour afficher le logo,
 * ce qui évite de dupliquer le code de chargement de l'image dans chaque contrôleur.
 */
public class Navigation {

    /**
     * Logo chargé une seule fois au démarrage de l'application (champ statique final).
     * requireNonNull garantit un crash immédiat et lisible si le fichier image est introuvable,
     * plutôt qu'une NullPointerException silencieuse plus tard.
     */
    private static final Image LOGO =
            new Image(Objects.requireNonNull(Navigation.class.getResourceAsStream(
                    "/cinema/images/cinema_logo.png")));

    /**
     * Applique le logo sur n'importe quel Stage.
     * On vide d'abord les icônes existantes pour éviter les doublons si la méthode
     * est appelée plusieurs fois sur le même stage.
     * @param stage La fenêtre JavaFX sur laquelle appliquer le logo
     */
    public static void applyLogo(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(LOGO);
    }

    /**
     * Référence au stage principal de l'application.
     * Permet à navigateTo() de changer de scène sans avoir besoin du stage en paramètre.
     */
    private static Stage primaryStage;

    /**
     * Enregistre le stage principal. Doit être appelé une seule fois depuis MainApplication.
     * @param stage Le stage principal créé par JavaFX au démarrage
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Change la scène affichée sur le stage principal.
     * Méthode de navigation simplifiée : il suffit de donner le chemin FXML.
     * @param fxmlPath Chemin vers le fichier FXML à charger (ex: "/cinema/views/page_accueil.fxml")
     */
    public static void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(fxmlPath));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}