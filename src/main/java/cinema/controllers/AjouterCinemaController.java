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
import javafx.stage.Stage;

/**
 * Contrôleur gérant le formulaire d'ajout d'un cinéma (page_ajout_cinema.fxml).
 */
public class AjouterCinemaController extends MenuController implements Initializable {

    // Liens vers les champs de saisie du fichier FXML
    @FXML private TextField tfDenomination; // Nom commercial du cinéma
    @FXML private TextField tfAdresse;      // Rue et numéro
    @FXML private TextField tfVille;        // Ville de localisation
    @FXML private ListView<Franchise> lvFranchise; // Liste de sélection des franchises

    /**
     * Au chargement, on remplit la liste des franchises pour que l'utilisateur puisse choisir.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvFranchise.setItems(getFranchiseList());
    }

    /**
     * Appelle le DAO pour récupérer toutes les franchises en base.
     * @return Une ObservableList, format requis par JavaFX pour mettre à jour la vue en temps réel.
     */
    private ObservableList<Franchise> getFranchiseList() {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        List<Franchise> franchises = franchiseDAO.findAll();
        return FXCollections.observableArrayList(franchises);
    }

    /**
     * Méthode déclenchée par le bouton "Enregistrer".
     * Elle crée l'objet BO et demande au DAO de l'insérer en BDD.
     */
    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        // Récupération et nettoyage des saisies (trim() retire les espaces inutiles)
        String denom = tfDenomination.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String ville = tfVille.getText().trim();
        // Récupère l'objet Franchise sélectionné dans la liste
        Franchise franchise = lvFranchise.getSelectionModel().getSelectedItem();

        // Vérification de sécurité : on n'envoie rien en BDD si un champ est vide
        if (denom.isEmpty() || adresse.isEmpty() || ville.isEmpty() || franchise == null) {
            return; // On pourrait ajouter une popup d'erreur ici
        }

        // Création de l'objet Cinéma (l'ID est à 0 car géré par l'auto-incrément SQL)
        Cinema cinema = new Cinema(0, denom, adresse, ville, franchise.getIdFranchise());

        // Interaction avec la base de données
        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.create(cinema);

        // Nettoyage de l'interface après l'ajout réussi
        bEffacerClick(null);
    }

    /**
     * Réinitialise tous les champs du formulaire.
     */
    @FXML
    public void bEffacerClick(ActionEvent event) {
        tfDenomination.clear();
        tfAdresse.clear();
        tfVille.clear();
        lvFranchise.getSelectionModel().clearSelection();
    }

    /**
     * Retourne à la liste des cinémas en rechargeant la scène correspondante.
     */
    @FXML
    public void bRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = loader.load();

            // Transmission du nom de l'utilisateur au nouveau contrôleur pour garder la session
            ListeCinemaController controller = loader.getController();
            controller.setName(nameUti);

            // Changement de fenêtre
            Stage stage = (Stage) tfDenomination.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste cinémas");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Affiche l'erreur en console si le fichier FXML est introuvable
        }
    }
}