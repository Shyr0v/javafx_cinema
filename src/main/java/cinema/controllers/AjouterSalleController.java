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
 * Contrôleur du formulaire d'ajout d'une salle (page_ajout_salle.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Permet de saisir le numéro, la description, le nombre de places et de choisir un cinéma.
 */
public class AjouterSalleController extends MenuController implements Initializable {

    @FXML private TextField tfNumero;       // numéro de salle (ex: 102) — doit être un entier
    @FXML private TextField tfDescription;  // caractéristiques (ex: 4DX, Atmos) — optionnel
    @FXML private TextField tfNbPlaces;     // capacité de la salle — doit être un entier
    @FXML private ComboBox<Cinema> cbCinema; // liste déroulante des cinémas disponibles
    @FXML private Button bRetour;           // bouton pour revenir à la liste des salles

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Charge la liste des cinémas dans la ComboBox pour l'affectation de la salle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cbCinema.setItems(FXCollections.observableArrayList(cinemaDAO.findAll()));
    }

    /**
     * Déclenché par le bouton "Enregistrer".
     * Convertit les saisies en entiers et insère la salle en base via le DAO.
     * Une NumberFormatException est capturée si l'utilisateur saisit du texte dans les champs numériques.
     */
    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        try {
            // Integer.parseInt peut lever NumberFormatException si la saisie n'est pas un nombre
            int numero = Integer.parseInt(tfNumero.getText().trim());
            String desc = tfDescription.getText().trim();
            int places = Integer.parseInt(tfNbPlaces.getText().trim());
            Cinema cinema = cbCinema.getValue(); // cinéma sélectionné dans la ComboBox

            if (cinema != null) {
                // L'id est à 0 car géré par l'auto-incrément SQL
                Salle salle = new Salle(0, numero, desc, places, cinema.getIdCinema());
                SalleDAO salleDAO = new SalleDAO();

                if (salleDAO.create(salle)) {
                    // Insertion réussie : vide le formulaire pour permettre un nouvel ajout
                    tfNumero.clear();
                    tfDescription.clear();
                    tfNbPlaces.clear();
                    cbCinema.setValue(null);
                }
            }
        } catch (NumberFormatException e) {
            // Affiche une alerte si l'utilisateur a saisi des lettres dans les champs numériques
            Alert alert = new Alert(AlertType.ERROR, "Le numéro et les places doivent être des nombres !");
            alert.show();
        }
    }

    /**
     * Ferme la fenêtre modale actuelle et ouvre la liste des salles dans une nouvelle fenêtre modale.
     * On ferme d'abord avant d'ouvrir pour éviter les fenêtres superposées.
     */
    @FXML
    public void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close(); // ferme le formulaire d'ajout
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cinema/views/page_liste_salle.fxml"));
            Parent root = fxmlLoader.load();

            ListeSalleController listeSalleController = fxmlLoader.getController();
            listeSalleController.setName(nameUti); // transmet le nom pour maintenir la session

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