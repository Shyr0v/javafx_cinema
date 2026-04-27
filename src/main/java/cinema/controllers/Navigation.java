package cinema.controllers;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Navigation {


    private static final Image LOGO =
            new Image(Objects.requireNonNull(Navigation.class.getResourceAsStream(
                    "/cinema/images/cinema_32x32.png")));

    public static void applyLogo(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(LOGO);
    }

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

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