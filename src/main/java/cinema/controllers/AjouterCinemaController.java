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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AjouterCinemaController extends MenuController implements Initializable {

    @FXML
    private TextField tfDenomination;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfVille;

    @FXML
    private ListView<Franchise> lvFranchise;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvFranchise.setItems(getFranchiseList());
    }

    private ObservableList<Franchise> getFranchiseList() {

        FranchiseDAO franchiseDAO = new FranchiseDAO();
        List<Franchise> franchises = franchiseDAO.findAll();

        return FXCollections.observableArrayList(franchises);
    }

    @FXML
    public void bEnregistrerClick(ActionEvent event) {

        String denom = tfDenomination.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String ville = tfVille.getText().trim();
        Franchise franchise = lvFranchise.getSelectionModel().getSelectedItem();

        if (denom.isEmpty() || adresse.isEmpty() || ville.isEmpty() || franchise == null) {
            return;
        }

        Cinema cinema = new Cinema(
                0,
                denom,
                adresse,
                ville,
                franchise.getIdFranchise()
        );

        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.create(cinema);

        tfDenomination.clear();
        tfAdresse.clear();
        tfVille.clear();
        lvFranchise.getSelectionModel().clearSelection();
    }

    @FXML
    public void bEffacerClick(ActionEvent event) {
        tfDenomination.clear();
        tfAdresse.clear();
        tfVille.clear();
        lvFranchise.getSelectionModel().clearSelection();
    }

    @FXML
    public void bRetourClick(ActionEvent event) {

        Stage stageP = (Stage) tfDenomination.getScene().getWindow();
        stageP.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = fxmlLoader.load();

            ListeCinemaController controller = fxmlLoader.getController();
            controller.setName(nameUti);

            Stage stage = new Stage();
            stage.setTitle("Liste cinémas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}