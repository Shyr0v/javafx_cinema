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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Contrôleur du formulaire d'ajout d'une franchise (page_ajout_franchise.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Permet de saisir le nom, le siège social et de sélectionner un gérant parmi les utilisateurs.
 */
public class AjouterFranchiseController extends MenuController implements Initializable {

    @FXML
    private TextField tfNomFranchise; // champ nom de la franchise

    @FXML
    private TextField tfSiegeSocial; // champ siège social

    @FXML
    private Button bRetour; // bouton de retour vers l'accueil

    @FXML
    private ListView<Utilisateur> lvGerantFranchise; // liste de sélection du gérant

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Charge la liste des utilisateurs pour que le gérant puisse être sélectionné.
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
     * Retourne à la page d'accueil en réutilisant le stage existant.
     */
    @FXML
    public void bRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            AccueilController controller = loader.getController();
            controller.setName(nameUti);  // transmet le nom pour maintenir la session
            controller.setBienvenue();

            Stage stage = (Stage) bRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Déclenché par le bouton "Enregistrer".
     * Valide les saisies, crée l'objet Franchise et l'insère en base via le DAO.
     * Affiche une erreur si un champ est manquant ou si l'insertion échoue.
     */
    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        String nom = tfNomFranchise.getText().trim();
        String siege = tfSiegeSocial.getText().trim();
        Utilisateur gerant = lvGerantFranchise.getSelectionModel().getSelectedItem();

        // Vérification que tous les champs sont remplis avant d'envoyer en base
        if (nom.isEmpty() || siege.isEmpty() || gerant == null) {
            showError();
            return;
        }

        // L'id est à 0 car géré par l'auto-incrément SQL
        Franchise franchise = new Franchise(0, nom, siege, gerant.getIdUtilisateur());

        FranchiseDAO franchiseDAO = new FranchiseDAO();
        boolean controle = franchiseDAO.create(franchise);

        if (controle) {
            // Insertion réussie : on vide le formulaire pour permettre un nouvel ajout
            tfNomFranchise.clear();
            tfSiegeSocial.clear();
            lvGerantFranchise.getSelectionModel().clearSelection();
        } else {
            // Erreur lors de l'insertion (doublon, contrainte, etc.)
            showError();
        }
    }

    /**
     * Vide tous les champs du formulaire sans enregistrer.
     */
    @FXML
    public void bEffacerClick(ActionEvent event) {
        tfNomFranchise.clear();
        tfSiegeSocial.clear();
        lvGerantFranchise.getSelectionModel().clearSelection();
    }

    /**
     * Affiche une popup d'erreur modale indiquant que les champs sont incomplets.
     * Utilise initModality(APPLICATION_MODAL) pour bloquer la fenêtre principale.
     */
    private void showError() {
        Stage stage = new Stage();
        stage.setTitle("Erreur");

        Label label = new Label("Veuillez remplir tous les champs.");
        StackPane root = new StackPane(label);

        stage.setScene(new Scene(root, 320, 120));
        stage.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale
        stage.setResizable(false);
        stage.showAndWait(); // attend la fermeture avant de continuer
    }
}