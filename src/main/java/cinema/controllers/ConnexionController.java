package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Utilisateur;
import cinema.DAO.UtilisateurDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConnexionController implements Initializable {

    @FXML
    private TextField tfLogin;

    @FXML
    private PasswordField tfMDP;

    @FXML
    private Button bConnexion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void bConnexionClick(ActionEvent event) {
        String login = tfLogin.getText().trim();
        String mdp = tfMDP.getText().trim();

        UtilisateurDAO userDAO = new UtilisateurDAO();
        Utilisateur user = userDAO.authenticate(login, mdp);

        tfMDP.clear();

        if (user != null) {
            showAccueil(user);
        } else {
            showError();
        }
    }

    private void showAccueil(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            AccueilController accueilController = loader.getController();
            accueilController.setUtilisateur(user);
            accueilController.setBienvenue();

            Stage stage = (Stage) bConnexion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil Gestion de franchises");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showError() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/ErreurConnexion.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Erreur de connexion");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}