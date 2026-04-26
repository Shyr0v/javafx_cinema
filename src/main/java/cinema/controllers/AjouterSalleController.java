package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.BO.Salle;
import cinema.DAO.CinemaDAO;
import cinema.DAO.SalleDAO;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class AjouterSalleController extends MenuController implements Initializable {

    @FXML
    private TextField tfNumero;

    @FXML
    private TextField tfDescription;

    @FXML
    private TextField tfNbPlaces;

    @FXML
    private ComboBox<Cinema> cbCinema;

    @FXML
    private Button bRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerCinemas();
    }

    private void chargerCinemas() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cbCinema.setItems(FXCollections.observableArrayList(cinemaDAO.findAll()));
        cbCinema.setConverter(new StringConverter<Cinema>() {
            @Override
            public String toString(Cinema cinema) {
                return cinema == null ? "" : cinema.getDenomination();
            }
            @Override
            public Cinema fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        String numeroStr = tfNumero.getText();
        String description = tfDescription.getText();
        String nbPlacesStr = tfNbPlaces.getText();

        if (numeroStr.trim().isEmpty() || nbPlacesStr.trim().isEmpty() || cbCinema.getValue() == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Numéro, nombre de places et cinéma sont obligatoires.");
            alert.showAndWait();
            return;
        }

        int numero = Integer.parseInt(numeroStr);
        int nbPlaces = Integer.parseInt(nbPlacesStr);
        int idCinema = cbCinema.getValue().getIdCinema();

        Salle salle = new Salle(0, numero, description, nbPlaces, idCinema);
        SalleDAO salleDAO = new SalleDAO();
        boolean controle = salleDAO.create(salle);

        if (controle) {
            tfNumero.clear();
            tfDescription.clear();
            tfNbPlaces.clear();
            cbCinema.setValue(null);
        }
    }

    @FXML
    public void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_salle.fxml"));
            Parent root = fxmlLoader.load();
            ListeSalleController listeSalleController = fxmlLoader.getController();
            listeSalleController.setName(nameUti);
            Stage stage = new Stage();
            stage.setTitle("Liste des salles");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}