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

/**
 * Contrôleur de la page de modification d'une salle (page_modif_salle.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Les champs sont pré-remplis avec les valeurs actuelles via setAttributs().
 * L'ordre d'appel depuis ListeSalleController est : setIdSalle() → setAttributs().
 */
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

    private int idSalle; // id de la salle à modifier, reçu depuis ListeSalleController

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Charge les cinémas dans la ComboBox immédiatement pour qu'elle soit prête
     * avant l'appel à setAttributs() qui va sélectionner le bon cinéma.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerCinemas(); // doit être fait avant setAttributs() pour que la sélection fonctionne
    }

    /**
     * Reçoit l'id de la salle à modifier depuis ListeSalleController.
     * Doit être appelé AVANT setAttributs().
     * @param idSalle L'identifiant de la salle à modifier
     */
    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle;
    }

    /**
     * Pré-remplit les champs avec les valeurs actuelles de la salle.
     * Doit être appelé APRÈS setIdSalle() car utilise this.idSalle pour charger depuis la base.
     */
    public void setAttributs() {
        SalleDAO salleDAO = new SalleDAO();
        Salle salle = salleDAO.find(idSalle); // récupère la salle par son id
        if (salle != null) {
            tfNumero.setText(String.valueOf(salle.getNumero()));       // int → String pour le TextField
            tfDescription.setText(salle.getDescription());             // String directement
            tfNbPlaces.setText(String.valueOf(salle.getNbPlaces()));   // int → String pour le TextField
            selectionnerCinema(salle.getIdCinema());                   // sélectionne le bon cinéma dans la ComboBox
        }
    }

    /**
     * Charge tous les cinémas dans la ComboBox et configure leur affichage.
     * StringConverter est nécessaire pour afficher le nom du cinéma
     * plutôt que le résultat de toString() de l'objet Cinema.
     */
    private void chargerCinemas() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cbCinema.setItems(FXCollections.observableArrayList(cinemaDAO.findAll()));
        cbCinema.setConverter(new StringConverter<Cinema>() {
            @Override
            public String toString(Cinema cinema) {
                return cinema == null ? "" : cinema.getDenomination(); // texte affiché dans la ComboBox
            }
            @Override
            public Cinema fromString(String string) {
                return null; // non utilisé (pas de saisie libre dans la ComboBox)
            }
        });
    }

    /**
     * Sélectionne le cinéma correspondant à l'id donné dans la ComboBox.
     * On compare les ids et non les objets pour éviter les problèmes d'égalité Java.
     * @param idCinema L'id du cinéma à sélectionner
     */
    private void selectionnerCinema(int idCinema) {
        for (Cinema cinema : cbCinema.getItems()) {
            if (cinema.getIdCinema() == idCinema) {
                cbCinema.setValue(cinema); // sélectionne ce cinéma dans la ComboBox
                break; // inutile de continuer à parcourir
            }
        }
    }

    /**
     * Déclenché par le bouton "Enregistrer".
     * Valide les champs obligatoires, reconstruit l'objet Salle et l'envoie au DAO.
     * Si la ComboBox est vide, conserve l'ancien cinéma pour ne pas perdre l'association.
     * Redirige vers la liste des salles si la mise à jour réussit.
     */
    @FXML
    private void bEnregistrerClick(ActionEvent event) {
        String numeroStr = tfNumero.getText();
        String description = tfDescription.getText();
        String nbPlacesStr = tfNbPlaces.getText();

        // Vérifie que les champs obligatoires sont remplis (description est optionnelle)
        if (!numeroStr.trim().isEmpty() && !nbPlacesStr.trim().isEmpty()) {
            int numero = Integer.parseInt(numeroStr);   // String → int
            int nbPlaces = Integer.parseInt(nbPlacesStr); // String → int

            SalleDAO salleDAO = new SalleDAO();
            Salle salleExistante = salleDAO.find(idSalle); // récupère l'état actuel pour conserver l'idCinema

            int idCinema = 0;
            if (cbCinema.getValue() != null) {
                idCinema = cbCinema.getValue().getIdCinema(); // cinéma choisi par l'utilisateur
            } else if (salleExistante != null) {
                idCinema = salleExistante.getIdCinema(); // conserve l'ancien cinéma si la ComboBox est vide
            }

            // Reconstruit l'objet avec l'idSalle existant pour que le DAO fasse un UPDATE et non un INSERT
            Salle salle = new Salle(idSalle, numero, description, nbPlaces, idCinema);
            boolean controle = salleDAO.update(salle);

            if (controle) {
                // Mise à jour réussie : ferme cette fenêtre et ouvre la liste des salles
                Stage stageP = (Stage) bRetour.getScene().getWindow();
                stageP.close();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(
                            getClass().getResource("/cinema/views/page_liste_salle.fxml"));
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
        } else {
            // Affiche une alerte si les champs obligatoires sont vides
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Le numéro et le nombre de places sont obligatoires.");
            alert.showAndWait();
        }
    }

    /**
     * Retourne à la liste des salles sans sauvegarder les modifications.
     * Ferme d'abord la fenêtre actuelle avant d'ouvrir la liste.
     */
    @FXML
    private void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close(); // ferme sans sauvegarder
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_salle.fxml"));
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