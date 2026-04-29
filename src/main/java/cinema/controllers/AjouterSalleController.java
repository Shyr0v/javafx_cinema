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

/**
 * Contrôleur pour la page d'ajout de salle (page_ajout_salle.fxml).
 */
public class AjouterSalleController extends MenuController implements Initializable {

    @FXML private TextField tfNumero;      // Numéro de salle (ex: 102)
    @FXML private TextField tfDescription; // Caractéristiques (ex: 4DX, Atmos)
    @FXML private TextField tfNbPlaces;    // Capacité de la salle
    @FXML private ComboBox<Cinema> cbCinema; // Liste déroulante des cinémas
    @FXML private Button bRetour;          // Bouton pour revenir à la liste

    /**
     * Remplit la ComboBox avec les cinémas existants pour l'affectation.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cbCinema.setItems(FXCollections.observableArrayList(cinemaDAO.findAll()));
    }

    /**
     * Traite l'enregistrement. Inclut une gestion d'exception pour les erreurs de format (nombres).
     */
    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        try {
            // Conversion des chaînes en entiers (peut générer une NumberFormatException)
            int numero = Integer.parseInt(tfNumero.getText().trim());
            String desc = tfDescription.getText().trim();
            int places = Integer.parseInt(tfNbPlaces.getText().trim());
            Cinema cinema = cbCinema.getValue(); // Récupère le cinéma choisi

            if (cinema != null) {
                // Création de l'objet Salle rattaché à l'ID du cinéma
                Salle salle = new Salle(0, numero, desc, places, cinema.getIdCinema());
                SalleDAO salleDAO = new SalleDAO();

                if (salleDAO.create(salle)) {
                    // Nettoyage des champs si l'insertion est confirmée
                    tfNumero.clear();
                    tfDescription.clear();
                    tfNbPlaces.clear();
                    cbCinema.setValue(null);
                }
            }
        } catch (NumberFormatException e) {
            // Affiche une alerte graphique si l'utilisateur a tapé des lettres au lieu de chiffres
            Alert alert = new Alert(AlertType.ERROR, "Le numéro et les places doivent être des nombres !");
            alert.show();
        }
    }

    /**
     * Ferme la fenêtre actuelle et recharge la liste des salles.
     */
    @FXML
    public void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close(); // Fermeture de la popup d'ajout
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cinema/views/page_liste_salle.fxml"));
            Parent root = fxmlLoader.load();

            // Propagation du nom d'utilisateur pour ne pas perdre la session
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