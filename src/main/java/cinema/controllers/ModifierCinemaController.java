package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.BO.Franchise;
import cinema.DAO.CinemaDAO;
import cinema.DAO.FranchiseDAO;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModifierCinemaController extends MenuController implements Initializable {

    @FXML
    private TextArea taLibSec;

    @FXML
    private TextField tfAdresse, tfVille;

    @FXML
    private ComboBox<Franchise> cbFranchise;

    private int idSec;

    @FXML
    private Button bRetour, bEnregistrer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerFranchises();
    }

    public void setIdSec(int idSec) {
        this.idSec = idSec;
    }

    public void setAttrinuts() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        Cinema cinema = cinemaDAO.find(idSec);

        if (cinema != null) {
            taLibSec.setText(cinema.getDenomination());
            tfAdresse.setText(cinema.getAdresse());
            tfVille.setText(cinema.getVille());
            selectionnerFranchise(cinema.getIdFranchise());
        }
    }

    private void chargerFranchises() {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        cbFranchise.setItems(FXCollections.observableArrayList(franchiseDAO.findAll()));

        cbFranchise.setConverter(new StringConverter<Franchise>() {
            @Override
            public String toString(Franchise franchise) {
                return franchise == null ? "" : franchise.getNomFranchise();
            }

            @Override
            public Franchise fromString(String string) {
                return null;
            }
        });
    }

    private void selectionnerFranchise(int idFranchise) {
        for (Franchise franchise : cbFranchise.getItems()) {
            if (franchise.getIdFranchise() == idFranchise) {
                cbFranchise.setValue(franchise);
                break;
            }
        }
    }

    @FXML
    private void bRetourClick(ActionEvent event) {

        Stage stageP = (Stage) bRetour.getScene().getWindow();
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

    @FXML
    private void bEnregistrerClick(ActionEvent event) {

        String lib = taLibSec.getText();
        String adresse = tfAdresse.getText();
        String ville = tfVille.getText();

        if (!lib.trim().isEmpty()) {

            CinemaDAO cinemaDAO = new CinemaDAO();
            Cinema cinemaExistant = cinemaDAO.find(idSec);

            int idFranchise = 0;

            if (cbFranchise.getValue() != null) {
                idFranchise = cbFranchise.getValue().getIdFranchise();
            } else if (cinemaExistant != null) {
                idFranchise = cinemaExistant.getIdFranchise();
            }

            Cinema cinema = new Cinema(idSec, lib, adresse, ville, idFranchise);

            boolean ok = cinemaDAO.update(cinema);

            if (ok) {

                Stage stageP = (Stage) bRetour.getScene().getWindow();
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
    }
}