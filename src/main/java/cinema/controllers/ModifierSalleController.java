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

public class ModifierSalleController extends MenuController implements Initializable {

    @FXML
    private TextField tfNumero; // champ pré-rempli avec le numéro actuel de la salle

    @FXML
    private TextField tfDescription; // champ pré-rempli avec la description actuelle

    @FXML
    private TextField tfNbPlaces; // champ pré-rempli avec le nombre de places actuel

    @FXML
    private ComboBox<Cinema> cbCinema; // combobox pré-sélectionnée sur le cinéma actuel de la salle

    @FXML
    private Button bRetour, bEnregistrer; // boutons retour et enregistrer

    private int idSalle; // stocke l'id de la salle à modifier, passé depuis ListeSalleController

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerCinemas(); // charge les cinémas dès l'ouverture pour que la combobox soit prête avant setAttributs()
    }

    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle; // reçoit l'id depuis ListeSalleController, doit être appelé avant setAttributs()
    }

    public void setAttributs() {
        SalleDAO salleDAO = new SalleDAO();
        Salle salle = salleDAO.find(idSalle); // cherche la salle en base grâce à l'id reçu
        if (salle != null) {
            tfNumero.setText(String.valueOf(salle.getNumero())); // String.valueOf convertit l'int en String pour le TextField
            tfDescription.setText(salle.getDescription()); // String directement, pas besoin de conversion
            tfNbPlaces.setText(String.valueOf(salle.getNbPlaces())); // idem, conversion int → String
            selectionnerCinema(salle.getIdCinema()); // sélectionne le bon cinéma dans la combobox
        }
    }

    private void chargerCinemas() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cbCinema.setItems(FXCollections.observableArrayList(cinemaDAO.findAll())); // remplit la combobox avec tous les cinémas
        cbCinema.setConverter(new StringConverter<Cinema>() {
            @Override
            public String toString(Cinema cinema) {
                return cinema == null ? "" : cinema.getDenomination(); // affiche le nom du cinéma
            }
            @Override
            public Cinema fromString(String string) {
                return null; // non utilisé
            }
        });
    }

    private void selectionnerCinema(int idCinema) {
        for (Cinema cinema : cbCinema.getItems()) { // parcourt tous les cinémas chargés dans la combobox
            if (cinema.getIdCinema() == idCinema) { // compare les ids et non les objets (évite les problèmes d'égalité Java)
                cbCinema.setValue(cinema); // sélectionne ce cinéma dans la combobox
                break; // inutile de continuer à parcourir
            }
        }
    }

    @FXML
    private void bEnregistrerClick(ActionEvent event) {
        String numeroStr = tfNumero.getText();
        String description = tfDescription.getText();
        String nbPlacesStr = tfNbPlaces.getText();

        if (!numeroStr.trim().isEmpty() && !nbPlacesStr.trim().isEmpty()) { // vérifie que les champs obligatoires sont remplis
            int numero = Integer.parseInt(numeroStr); // convertit String → int
            int nbPlaces = Integer.parseInt(nbPlacesStr); // convertit String → int

            SalleDAO salleDAO = new SalleDAO();
            Salle salleExistante = salleDAO.find(idSalle); // récupère la salle actuelle pour conserver l'idCinema si besoin

            int idCinema = 0;
            if (cbCinema.getValue() != null) {
                idCinema = cbCinema.getValue().getIdCinema(); // prend le cinéma sélectionné par l'utilisateur
            } else if (salleExistante != null) {
                idCinema = salleExistante.getIdCinema(); // conserve l'ancien cinéma si la combobox est vide
            }

            Salle salle = new Salle(idSalle, numero, description, nbPlaces, idCinema); // reconstruit l'objet avec l'idSalle existant (pas 0)
            boolean controle = salleDAO.update(salle); // envoie la mise à jour au DAO

            if (controle) { // si le UPDATE a réussi, retourne à la liste
                Stage stageP = (Stage) bRetour.getScene().getWindow();
                stageP.close();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(
                            getClass().getResource("/cinema/views/page_liste_salle.fxml"));
                    Parent root = fxmlLoader.load();
                    ListeSalleController listeSalleController = fxmlLoader.getController();
                    listeSalleController.setName(nameUti); // propage le nom utilisateur
                    Stage stage = new Stage();
                    stage.setTitle("Liste des salles");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Le numéro et le nombre de places sont obligatoires."); // description n'est pas obligatoire
            alert.showAndWait();
        }
    }

    @FXML
    private void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow(); // récupère la fenêtre depuis le bouton
        stageP.close(); // ferme sans sauvegarder
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