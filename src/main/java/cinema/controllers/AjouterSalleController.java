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

public class AjouterSalleController extends MenuController implements Initializable {

    @FXML private TextField tfNumero;
    @FXML private TextField tfDescription;
    @FXML private TextField tfNbPlaces;
    @FXML private ComboBox<Cinema> cbCinema;
    @FXML private Button bRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cbCinema.setItems(FXCollections.observableArrayList(cinemaDAO.findAll()));
    }

    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        try {
            int numero = Integer.parseInt(tfNumero.getText().trim());
            String desc = tfDescription.getText().trim();
            int places = Integer.parseInt(tfNbPlaces.getText().trim());
            Cinema cinema = cbCinema.getValue();

            if (cinema != null) {
                Salle salle = new Salle(0, numero, desc, places, cinema.getIdCinema());
                SalleDAO salleDAO = new SalleDAO();

                if (salleDAO.create(salle)) {
                    tfNumero.clear();
                    tfDescription.clear();
                    tfNbPlaces.clear();
                    cbCinema.setValue(null);
                }
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR, "Le numéro et les places doivent être des nombres !");
            alert.show();
        }
    }

    @FXML
    public void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cinema/views/page_liste_salle.fxml"));
            Parent root = fxmlLoader.load();

            ListeSalleController listeSalleController = fxmlLoader.getController();
            listeSalleController.setName(nameUti);

            Stage stage = new Stage();
            stage.setTitle("Liste des salles");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            Navigation.applyLogo(stage); // logo appliqué sur le nouveau Stage
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}