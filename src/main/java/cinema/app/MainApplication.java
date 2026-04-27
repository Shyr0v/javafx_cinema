package cinema.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/cinema/views/page_connexion.fxml"));

            Scene scene = new Scene(parent);

            primaryStage.setTitle("Application de gestion de franchise - Authentification");
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.getIcons().clear();
            primaryStage.getIcons().add(new Image("/cinema/images/cinema_32x32.png"));
            primaryStage.setScene(scene);

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}