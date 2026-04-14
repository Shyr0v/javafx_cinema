package cinema.controllers;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    protected MenuItem bListeFranchise, bAjouterFranchise, bListeCinema, bAjouterCinema, bQuitter, bAccueil,
            bListeSalle, bAjouterSalle;

    protected String nameUti;

    public void setName(String nameUti) {
        this.nameUti = nameUti;
    }

    protected Stage getStageFromMenu(ActionEvent event) {
        return (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
    }

    protected Stage getStageFromNode(Node node) {
        return (Stage) node.getScene().getWindow();
    }

    protected void openInSameStage(Stage stage, FXMLLoader loader, String title) {
        try {
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof MenuController menuController) {
                menuController.setName(nameUti);
            }
            if (controller instanceof AccueilController accueilController) {
                accueilController.setBienvenue();
            }

            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bQuitterClick(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void bAccueilClick(ActionEvent event) {
        Stage stage = getStageFromMenu(event);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cinema/views/page_accueil.fxml"));
        openInSameStage(stage, loader, "Accueil");
    }

    @FXML
    public void bListFranchiseClick(ActionEvent event) {
        Stage stage = getStageFromMenu(event);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
        openInSameStage(stage, loader, "Liste franchises");
    }

    @FXML
    public void bAjouterFranchiseClick(ActionEvent event) {
        Stage stage = getStageFromMenu(event);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cinema/views/page_ajout_franchise.fxml"));
        openInSameStage(stage, loader, "Ajouter une franchise");
    }

    @FXML
    public void bListeCinemaClick(ActionEvent event) {
        Stage stage = getStageFromMenu(event);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
        openInSameStage(stage, loader, "Liste cinémas");
    }

    @FXML
    public void bAjouterCinemaClick(ActionEvent event) {
        Stage stage = getStageFromMenu(event);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cinema/views/page_ajout_cinema.fxml"));
        openInSameStage(stage, loader, "Ajouter un cinéma");
    }

    @FXML
    public void bListeSalleClick(ActionEvent event) {
        Stage stage = getStageFromMenu(event);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cinema/views/page_liste_salle.fxml"));
        openInSameStage(stage, loader, "Liste salles");
    }

    @FXML
    public void bAjouterSalleClick(ActionEvent event) {
        Stage stage = getStageFromMenu(event);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cinema/views/page_ajout_salle.fxml"));
        openInSameStage(stage, loader, "Ajouter une salle");
    }
}