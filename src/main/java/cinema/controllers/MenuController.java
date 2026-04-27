package cinema.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    protected MenuItem bListeFranchise, bAjouterFranchise, bListeCinema, bAjouterCinema, bQuitter, bAccueil,
            bListeSalle, bAjouterSalle;

    protected String nameUti;

    public void setName(String nameUti) {
        this.nameUti = nameUti;
    }

    protected void appliquerLogo(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(new Image("/cinema/images/cinema_32x32.png"));
    }

    protected void changerScene(Stage stage, Parent root, String titre) {
        stage.setScene(new Scene(root));
        stage.setTitle(titre);
        stage.setResizable(false);
        appliquerLogo(stage);
        stage.show();
    }

    private Stage getStageDepuisMenu(ActionEvent event) {
        return (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
    }

    @FXML
    public void bQuitterClick(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void bAccueilClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            AccueilController controller = loader.getController();
            controller.setName(nameUti);
            controller.setBienvenue();

            changerScene(stage, root, "Accueil");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bListFranchiseClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
            Parent root = loader.load();

            ListeFranchiseController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Liste franchises");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bAjouterFranchiseClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_franchise.fxml"));
            Parent root = loader.load();

            AjouterFranchiseController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Ajouter une franchise");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bListeCinemaClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = loader.load();

            ListeCinemaController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Liste cinémas");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bAjouterCinemaClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_cinema.fxml"));
            Parent root = loader.load();

            AjouterCinemaController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Ajouter un cinéma");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bListeSalleClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_salle.fxml"));
            Parent root = loader.load();

            changerScene(stage, root, "Liste salles");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bAjouterSalleClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_salle.fxml"));
            Parent root = loader.load();

            changerScene(stage, root, "Ajouter une salle");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}