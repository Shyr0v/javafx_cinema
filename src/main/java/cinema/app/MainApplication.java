package cinema.app;

import cinema.controllers.Navigation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_connexion.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Application de gestion de franchise - Authentification");
            primaryStage.setScene(new Scene(root));

            // 🔥 AJOUT DU LOGO ICI
            primaryStage.getIcons().add(
                    new Image("/cinema/images/cinema_logo.png"));

            primaryStage.setResizable(false);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}