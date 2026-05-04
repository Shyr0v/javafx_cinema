package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Utilisateur;
import cinema.DAO.UtilisateurDAO;
import cinema.Session;
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

/**
 * Contrôleur de la page de connexion (page_connexion.fxml).
 * Gère l'authentification de l'utilisateur et la transition vers l'accueil.
 * N'hérite pas de MenuController car la page de connexion n'a pas de barre de navigation.
 */
public class ConnexionController implements Initializable {

    @FXML
    private TextField tfLogin; // champ de saisie du login (email)

    @FXML
    private PasswordField tfMDP; // champ de saisie du mot de passe (masqué automatiquement)

    @FXML
    private Button bConnexion; // bouton déclenchant la tentative de connexion

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Rien à initialiser ici : les champs sont vides par défaut grâce au FXML (promptText).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pas d'initialisation nécessaire
    }

    /**
     * Déclenché par le clic sur le bouton "Connexion".
     * Récupère les saisies, interroge la base via le DAO, puis redirige ou affiche une erreur.
     */
    @FXML
    public void bConnexionClick(ActionEvent event) {
        String login = tfLogin.getText().trim();  // trim() supprime les espaces accidentels
        String mdp = tfMDP.getText().trim();

        // Interroge la base de données pour vérifier les identifiants
        UtilisateurDAO userDAO = new UtilisateurDAO();
        Utilisateur user = userDAO.authenticate(login, mdp);

        tfMDP.clear(); // efface le mot de passe de l'affichage pour la sécurité

        if (user != null) {
            // Connexion réussie : on stocke l'utilisateur en session et on affiche l'accueil
            Session.setUtilisateur(user);
            showAccueil(user);
        } else {
            // Identifiants incorrects : affiche la popup d'erreur
            showError();
        }
    }

    /**
     * Charge et affiche la page d'accueil après une connexion réussie.
     * Réutilise le même stage (fenêtre) pour ne pas ouvrir une nouvelle fenêtre.
     * @param user L'utilisateur authentifié, transmis à AccueilController pour personnaliser l'affichage
     */
    private void showAccueil(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            // Transmet l'objet utilisateur au contrôleur de l'accueil
            AccueilController controller = loader.getController();
            controller.setUtilisateur(user);
            controller.setBienvenue(); // déclenche l'affichage "BONJOUR NOM PRENOM"

            // Récupère le stage actuel depuis le bouton et change la scène
            Stage stage = (Stage) bConnexion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil Gestion de franchises");
            stage.setResizable(false);
            Navigation.applyLogo(stage); // applique le logo via Navigation (source unique)
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvre une popup modale affichant le message d'erreur de connexion.
     * initModality(APPLICATION_MODAL) bloque l'interaction avec la fenêtre principale
     * jusqu'à ce que l'utilisateur ferme la popup.
     */
    @FXML
    private void showError() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/ErreurConnexion.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Erreur de connexion");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale
            stage.setResizable(false);
            Navigation.applyLogo(stage); // logo appliqué sur la popup d'erreur aussi
            stage.showAndWait(); // attend que l'utilisateur ferme la popup

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}