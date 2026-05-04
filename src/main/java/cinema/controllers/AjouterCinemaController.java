package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.BO.Franchise;
import cinema.DAO.CinemaDAO;
import cinema.DAO.FranchiseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Contrôleur du formulaire d'ajout d'un cinéma (page_ajout_cinema.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Permet de saisir la dénomination, l'adresse, la ville et de choisir une franchise.
 */
public class AjouterCinemaController extends MenuController implements Initializable {

    @FXML private TextField tfDenomination; // nom commercial du cinéma
    @FXML private TextField tfAdresse;      // rue et numéro
    @FXML private TextField tfVille;        // ville de localisation
    @FXML private ListView<Franchise> lvFranchise; // liste de sélection des franchises

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Charge la liste des franchises pour que l'utilisateur puisse en sélectionner une.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvFranchise.setItems(getFranchiseList());
    }

    /**
     * Charge toutes les franchises depuis la base pour alimenter la ListView.
     * @return ObservableList des franchises disponibles
     */
    private ObservableList<Franchise> getFranchiseList() {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        List<Franchise> franchises = franchiseDAO.findAll();
        return FXCollections.observableArrayList(franchises);
    }

    /**
     * Déclenché par le bouton "Enregistrer".
     * Valide les saisies, crée l'objet Cinema et l'insère en base via le DAO.
     * Si un champ est vide ou aucune franchise n'est sélectionnée, rien n'est envoyé en base.
     */
    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        String denom = tfDenomination.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String ville = tfVille.getText().trim();
        Franchise franchise = lvFranchise.getSelectionModel().getSelectedItem();

        if (denom.isEmpty() || adresse.isEmpty() || ville.isEmpty() || franchise == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et sélectionner une franchise.");
            alert.showAndWait();
            return;
        }

        Cinema cinema = new Cinema(0, denom, adresse, ville, franchise.getIdFranchise());

        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.create(cinema);

        bEffacerClick(null);
    }

    /**
     * Vide tous les champs du formulaire sans enregistrer.
     */
    @FXML
    public void bEffacerClick(ActionEvent event) {
        tfDenomination.clear();
        tfAdresse.clear();
        tfVille.clear();
        lvFranchise.getSelectionModel().clearSelection();
    }

    /**
     * Retourne à la liste des cinémas en réutilisant le stage existant.
     */
    @FXML
    public void bRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = loader.load();

            ListeCinemaController controller = loader.getController();
            controller.setName(nameUti); // transmet le nom pour maintenir la session

            Stage stage = (Stage) tfDenomination.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste cinémas");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}