package cinema.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    protected MenuItem bListeFranchise, bAjouterFranchise, bListeCinema, bAjouterCinema, bQuitter, bAccueil,
            bListeSalle,
            bAjouterSalle;

    protected String nameUti;

    @FXML
    public void bQuitterClick(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void bAccueilClick(ActionEvent event) {
        Stage StageE = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        StageE.close();

    }

    @FXML
    public void bListFranchiseClick(ActionEvent event) {
        Stage stageP = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stageP.close();
        try {

            // Charger le fichier FXML
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
            Parent root = fxmlLoader.load();

            // Obtenir le contrôleur de la nouvelle fenetre
            ListeFranchiseController listeFranchiseController = fxmlLoader.getController();
            listeFranchiseController.setName(nameUti);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Liste franchises");
            stage.setScene(new Scene(root));

            // Configurer la fenêtre en tant que modal
            stage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la fenêtre et attendre qu'elle se ferme
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bAjouterFranchiseClick(ActionEvent event) {
        Stage stageP = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stageP.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_franchise.fxml"));
            Parent root = fxmlLoader.load();

            // Obtenir le contrôleur de la nouvelle fenetre
            AjouterFranchiseController ajouterFranchiseController = fxmlLoader.getController();
            ajouterFranchiseController.setName(nameUti);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Ajouter une franchise");
            stage.setScene(new Scene(root));

            // Configurer la fenêtre en tant que modal
            stage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la fenêtre et attendre qu'elle se ferme
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void bListeCinemaClick(ActionEvent event) {
        Stage stageP = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stageP.close();
        try {

            // Correction ligne 2 :
            // le fichier cible était mal orthographié ("cinemaa" au lieu de "cinema"),
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = fxmlLoader.load();

            ListeCinemaController listeCinemaController = fxmlLoader.getController();
            listeCinemaController.setName(nameUti);

            Stage stage = new Stage();
            stage.setTitle("Liste cinéma");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bAjouterCinemaClick(ActionEvent event) {
        Stage stageP = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stageP.close();
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_cinema.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();

            stage.setTitle("Ajouter un cinéma");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bListeSalleClick(ActionEvent event) {
        Stage stageP = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stageP.close(); // ferme la fenêtre actuelle
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_salle.fxml")); // charge la vue liste salle
            Parent root = fxmlLoader.load(); // charge le FXML

            ListeSalleController listeSalleController = fxmlLoader.getController(); // récupère le contrôleur
            listeSalleController.setName(nameUti); // passe le nom de l'utilisateur

            Stage stage = new Stage(); // crée une nouvelle fenêtre
            stage.setTitle("Liste des salles"); // définit le titre
            stage.setScene(new Scene(root)); // définit la scène
            stage.initModality(Modality.APPLICATION_MODAL); // mode modal
            stage.show(); // affiche la fenêtre
        } catch (Exception e) {
            e.printStackTrace(); // affiche l'erreur si chargement échoue
        }
    }

    public void setName(String nameUti) {
        this.nameUti = nameUti;
    }

    @FXML
    public void bAjouterSalleClick(ActionEvent event) {
        Stage stageP = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stageP.close(); // ferme la fenêtre actuelle
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_salle.fxml")); // charge la vue ajout salle
            Parent root = fxmlLoader.load(); // charge le FXML

            AjouterSalleController ajouterSalleController = fxmlLoader.getController(); // récupère le contrôleur
            ajouterSalleController.setName(nameUti); // passe le nom de l'utilisateur

            Stage stage = new Stage(); // crée une nouvelle fenêtre
            stage.setTitle("Ajouter une salle"); // définit le titre
            stage.setScene(new Scene(root)); // définit la scène
            stage.initModality(Modality.APPLICATION_MODAL); // mode modal
            stage.show(); // affiche la fenêtre
        } catch (Exception e) {
            e.printStackTrace(); // affiche l'erreur si chargement échoue
        }
    }
}
