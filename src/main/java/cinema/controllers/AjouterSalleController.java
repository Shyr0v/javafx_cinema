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
    private TextField tfNumero; // champ de saisie pour le numéro de la salle

    @FXML
    private TextField tfDescription; // champ de saisie pour la description

    @FXML
    private TextField tfNbPlaces; // champ de saisie pour le nombre de places

    @FXML
    private ComboBox<Cinema> cbCinema; // liste déroulante contenant les objets Cinema (pas juste des String)

    @FXML
    private Button bRetour; // bouton pour revenir à la liste

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerCinemas(); // appelé automatiquement par JavaFX à l'ouverture de la page
    }

    private void chargerCinemas() {
        CinemaDAO cinemaDAO = new CinemaDAO(); // instancie le DAO pour accéder aux cinémas
        cbCinema.setItems(FXCollections.observableArrayList(cinemaDAO.findAll())); // charge tous les cinémas dans la combobox
        cbCinema.setConverter(new StringConverter<Cinema>() {
            @Override
            public String toString(Cinema cinema) {
                return cinema == null ? "" : cinema.getDenomination(); // affiche le nom au lieu de l'objet brut
            }
            @Override
            public Cinema fromString(String string) {
                return null; // non utilisé car la combobox n'est pas éditable
            }
        });
    }

    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        String numeroStr = tfNumero.getText(); // récupère le texte saisi dans le champ numéro
        String description = tfDescription.getText(); // récupère le texte saisi dans le champ description
        String nbPlacesStr = tfNbPlaces.getText(); // récupère le texte saisi dans le champ nb places

        if (numeroStr.trim().isEmpty() || nbPlacesStr.trim().isEmpty() || cbCinema.getValue() == null) {
            // trim() supprime les espaces, isEmpty() vérifie si vide
            Alert alert = new Alert(AlertType.ERROR); // crée une boîte de dialogue d'erreur
            alert.setTitle("Erreur de validation"); // titre de la fenêtre d'alerte
            alert.setHeaderText(null); // supprime le texte d'en-tête pour un rendu plus simple
            alert.setContentText("Numéro, nombre de places et cinéma sont obligatoires."); // message affiché à l'utilisateur
            alert.showAndWait(); // affiche l'alerte et attend que l'utilisateur ferme
            return; // stoppe la méthode, on n'insère rien
        }

        int numero = Integer.parseInt(numeroStr); // convertit la String en int (plante si l'utilisateur a tapé une lettre)
        int nbPlaces = Integer.parseInt(nbPlacesStr); // idem pour le nombre de places
        int idCinema = cbCinema.getValue().getIdCinema(); // récupère l'id du cinéma sélectionné dans la combobox

        Salle salle = new Salle(0, numero, description, nbPlaces, idCinema); // id=0 car auto-incrémenté par la base
        SalleDAO salleDAO = new SalleDAO(); // instancie le DAO pour accéder aux salles
        boolean controle = salleDAO.create(salle); // envoie l'objet au DAO qui fait l'INSERT en base

        if (controle) { // si l'insertion a réussi
            tfNumero.clear(); // vide le champ numéro
            tfDescription.clear(); // vide le champ description
            tfNbPlaces.clear(); // vide le champ nb places
            cbCinema.setValue(null); // réinitialise la combobox
        }
    }

    @FXML
    public void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow(); // remonte du bouton → scène → fenêtre
        stageP.close(); // ferme la fenêtre actuelle
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_salle.fxml")); // chemin vers le FXML de la liste
            Parent root = fxmlLoader.load(); // charge le fichier FXML et crée les composants graphiques
            ListeSalleController listeSalleController = fxmlLoader.getController(); // récupère l'instance du contrôleur créée par JavaFX
            listeSalleController.setName(nameUti); // propage le nom de l'utilisateur connecté
            Stage stage = new Stage(); // crée une nouvelle fenêtre
            stage.setTitle("Liste des salles"); // définit le titre de la fenêtre
            stage.setScene(new Scene(root)); // associe le contenu FXML à la fenêtre
            stage.initModality(Modality.APPLICATION_MODAL); // bloque les autres fenêtres tant que celle-ci est ouverte
            stage.show(); // affiche la fenêtre
        } catch (Exception e) {
            e.printStackTrace(); // affiche la stack trace si le chargement du FXML échoue
        }
    }

}