package cinema.app;

import cinema.controllers.Navigation;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // méthode appelée au démarrage de l'application

        primaryStage.setTitle("Application de gestion de franchise - Authentification");
        // définit le titre de la fenêtre principale

        primaryStage.setResizable(false);
        // empêche le redimensionnement

        primaryStage.centerOnScreen();
        // centre la fenêtre à l'écran

        primaryStage.getIcons().add(new Image("/cinema/images/cinema_32x32.png"));
        // ajoute une icône à la fenêtre

        Navigation.setPrimaryStage(primaryStage);
        // enregistre la fenêtre principale dans la classe Navigation

        Navigation.clearHistory();
        // vide l'historique de navigation

        Navigation.clearParams();
        // vide les paramètres de navigation

        Navigation.goTo("/cinema/views/page_connexion.fxml");
        // ouvre l'écran de connexion via la classe Navigation
    }
}