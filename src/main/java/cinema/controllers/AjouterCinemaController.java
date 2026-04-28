package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.BO.Franchise;
import cinema.DAO.CinemaDAO;
import cinema.DAO.FranchiseDAO;
import cinema.utils.AppLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterCinemaController extends MenuController implements Initializable {

    @FXML
    private TextField tfDenomination, tfAdresse, tfVille;

    @FXML
    private ListView<Franchise> lvFranchise;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvFranchise.setItems(getFranchiseList());
    }

    private ObservableList<Franchise> getFranchiseList() {
        FranchiseDAO dao = new FranchiseDAO();
        return FXCollections.observableArrayList(dao.findAll());
    }

    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        String denom = tfDenomination.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String ville = tfVille.getText().trim();
        Franchise franchise = lvFranchise.getSelectionModel().getSelectedItem();

        if (denom.isEmpty() || adresse.isEmpty() || ville.isEmpty() || franchise == null) {
            AppLogger.erreur("Ajout cinéma échoué : champs manquants");
            return;
        }

        Cinema cinema = new Cinema(0, denom, adresse, ville, franchise.getIdFranchise());
        CinemaDAO dao = new CinemaDAO();

        boolean ok = dao.create(cinema);

        if (ok) {
            AppLogger.action("AJOUT", "cinema",
                    "Ajout cinéma : " + denom + " / ville=" + ville);

            tfDenomination.clear();
            tfAdresse.clear();
            tfVille.clear();
            lvFranchise.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void bRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tfDenomination.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste cinémas");
            Navigation.applyLogo(stage);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.erreur("Erreur retour liste cinema");
        }
    }
}