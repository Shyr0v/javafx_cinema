package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cinema.BO.Franchise;
import cinema.BO.Utilisateur;
import cinema.DAO.FranchiseDAO;
import cinema.DAO.UtilisateurDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Contrôleur de la page de modification d'une franchise (page_modif_franchise.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Les champs sont pré-remplis avec les valeurs actuelles via setAttributes().
 */
public class ModifierFranchiseController extends MenuController implements Initializable {

    @FXML
    private TextField tfNomFranchise; // champ pré-rempli avec le nom actuel

    @FXML
    private TextField tfSiegeSocial; // champ pré-rempli avec le siège social actuel

    @FXML
    private Button bRetour; // bouton de retour vers la liste des franchises

    @FXML
    private ComboBox<Utilisateur> lvGerantFranchise; // liste des gérants, pré-sélectionnée sur le gérant actuel

    private int idFranchise; // id de la franchise à modifier, reçu depuis ListeFranchiseController

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Charge la liste des utilisateurs pour que le gérant puisse être changé.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvGerantFranchise.setItems(getUtilisateurList());
    }

    /**
     * Charge tous les utilisateurs depuis la base pour alimenter la ListView.
     * @return ObservableList des utilisateurs disponibles comme gérants
     */
    private ObservableList<Utilisateur> getUtilisateurList() {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        return FXCollections.observableArrayList(utilisateurs);
    }

    /**
     * Pré-remplit les champs avec les valeurs actuelles de la franchise à modifier.
     * Appelé depuis ListeFranchiseController après le chargement du FXML.
     * @param franchise L'objet Franchise dont les données sont à afficher et modifier
     */
    public void setAttributes(Franchise franchise) {
        tfNomFranchise.setText(franchise.getNomFranchise());
        tfSiegeSocial.setText(franchise.getSiegeSocial());
        this.idFranchise = franchise.getIdFranchise();

        // Présélectionner le gérant correspondant
        lvGerantFranchise.getItems().stream()
                .filter(u -> u.getIdUtilisateur() == franchise.getIdGerant())
                .findFirst()
                .ifPresent(u -> lvGerantFranchise.setValue(u));
    }

    /**
     * Déclenché par le bouton "Enregistrer".
     * Valide les saisies, construit l'objet mis à jour et l'envoie au DAO.
     * Redirige vers la liste des franchises si la mise à jour réussit.
     */
    @FXML
    private void bEnregistrerClick(ActionEvent event) {
        String nom = tfNomFranchise.getText().trim();
        String siegeSocial = tfSiegeSocial.getText().trim();
        Utilisateur selected = lvGerantFranchise.getSelectionModel().getSelectedItem();

        // Vérification que tous les champs sont remplis
        if (!nom.isEmpty() && !siegeSocial.isEmpty() && selected != null) {
            // Reconstruit l'objet avec l'id existant pour que le DAO fasse un UPDATE et non un INSERT
            Franchise newFranchise = new Franchise(this.idFranchise, nom, siegeSocial, selected.getIdUtilisateur());

            FranchiseDAO franchiseDAO = new FranchiseDAO();
            boolean controle = franchiseDAO.update(newFranchise);

            if (controle) {
                // Mise à jour réussie : retourne à la liste des franchises
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
                    Parent root = loader.load();

                    ListeFranchiseController controller = loader.getController();
                    controller.setName(nameUti); // transmet le nom pour maintenir la session

                    Stage stage = (Stage) bRetour.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Liste franchises");
                    stage.setResizable(false);
                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Retourne à la liste des franchises sans sauvegarder les modifications.
     */
    @FXML
    private void bRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
            Parent root = loader.load();

            ListeFranchiseController controller = loader.getController();
            controller.setName(nameUti); // transmet le nom pour maintenir la session

            Stage stage = (Stage) bRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste franchises");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}