package cinema.controllers;

import cinema.BO.Cinema;

import bo.Evenement;
import cinema.DAO.CinemaDAO;
import cinema.DAO.EvenementDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EvenementController extends MenuController implements Initializable {

    @FXML private TextField tfNom;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfNbrPlace;
    @FXML private CheckBox cbGratuit;
    @FXML private ComboBox<Cinema> cbCinema;
    @FXML private Label lblMessage;

    private EvenementDAO evenementDAO = new EvenementDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cbCinema.getItems().addAll(cinemaDAO.findAll());
    }

    public void setEvenementDAO(EvenementDAO evenementDAO) {
        this.evenementDAO = evenementDAO;
    }

    @FXML
    public void handleCreate() {
        String nom = tfNom.getText().trim();
        LocalDate date = dpDate.getValue();
        String nbrPlaceStr = tfNbrPlace.getText().trim();
        Cinema cinema = cbCinema.getValue();

        if (nom.isEmpty() || date == null || nbrPlaceStr.isEmpty() || cinema == null) {
            lblMessage.setText("Veuillez remplir tous les champs.");
            return;
        }

        int nbrPlace;
        try {
            nbrPlace = Integer.parseInt(nbrPlaceStr);
        } catch (NumberFormatException e) {
            lblMessage.setText("Nombre de places invalide.");
            return;
        }

        Evenement evenement = new Evenement(nom, date, nbrPlace, cbGratuit.isSelected(), cinema);
        boolean success = evenementDAO.create(evenement);

        if (success) {
            lblMessage.setText("Événement créé avec l'ID : " + evenement.getId());
        } else {
            lblMessage.setText("Erreur lors de la création.");
        }
    }



    public ComboBox<Cinema> getCbCinema() {
        return cbCinema;
    }
}