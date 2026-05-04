package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Contrôleur de la popup d'erreur de connexion (ErreurConnexion.fxml).
 * Rôle unique : fermer la fenêtre quand l'utilisateur clique sur OK.
 * La popup est ouverte par ConnexionController en mode APPLICATION_MODAL.
 */
public class ErrorController implements Initializable {

    @FXML
    private Button ButtonOk; // bouton de fermeture de la popup

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX.
     * Rien à initialiser : la popup est purement informative.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pas d'initialisation nécessaire
    }

    /**
     * Ferme la popup quand l'utilisateur clique sur OK.
     * On récupère le Stage depuis le bouton lui-même (getScene().getWindow()).
     * stage.close() retourne le focus à la fenêtre de connexion (comportement modal).
     */
    @FXML
    public void ButtonOkOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ButtonOk.getScene().getWindow();
        stage.close(); // ferme la popup et redonne la main à la fenêtre parente
    }
}