package cinema.controllers;

import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

import cinema.BO.Utilisateur;
import cinema.DAO.DBManager;
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
import javafx.scene.image.Image;
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
            enregistrerUtilisateurPourLogs(user);
            showAccueil(user);
        } else {
            showError();
        }
    }

    private void enregistrerUtilisateurPourLogs(Utilisateur user) {
        try {
            PreparedStatement ps = DBManager.getInstance().prepareStatement(
                    "SELECT set_current_user_id(?)"
            );
            ps.setInt(1, user.getIdUtilisateur());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appliquerLogo(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(new Image("/cinema/images/cinema_32x32.png"));
    }

    private void showAccueil(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            AccueilController controller = loader.getController();
            controller.setUtilisateur(user);
            controller.setBienvenue();

            Stage stage = (Stage) bConnexion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil Gestion de franchises");
            stage.setResizable(false);
            appliquerLogo(stage);
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
            appliquerLogo(stage);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}