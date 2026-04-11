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

public class AjouterFranchiseController extends MenuController implements Initializable {

    @FXML
    private TextField tfNomFranchise, tfSiegeSocial;

    @FXML
    private Button bRetour;

    @FXML
    private ListView<Utilisateur> lvGerantFranchise;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Utilisateur> utilisateurs = getUtilisateurList();
        lvGerantFranchise.setItems(utilisateurs);
    }

    private ObservableList<Utilisateur> getUtilisateurList() {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        return FXCollections.observableArrayList(utilisateurs);
    }

    @FXML
    public void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = fxmlLoader.load();

            AccueilController accueilController = fxmlLoader.getController();
            accueilController.setName(nameUti);
            accueilController.setBienvenue();

            Stage stage = new Stage();
            stage.setTitle("Accueil");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bEnregistrerClick(ActionEvent event) {
        String nom = tfNomFranchise.getText().trim();
        String siege = tfSiegeSocial.getText().trim();
        Utilisateur gerant = lvGerantFranchise.getSelectionModel().getSelectedItem();

        if (nom.isEmpty() || siege.isEmpty() || gerant == null) {
            showError();
            return;
        }

        Franchise franchise = new Franchise(
                0,
                nom,
                siege,
                gerant.getIdUtilisateur());

        FranchiseDAO franchiseDAO = new FranchiseDAO();
        boolean controle = franchiseDAO.create(franchise);

        if (controle) {
            tfNomFranchise.clear();
            tfSiegeSocial.clear();
            lvGerantFranchise.getSelectionModel().clearSelection();
        } else {
            showError();
        }
    }

    @FXML
    public void bEffacerClick(ActionEvent event) {
        if (tfNomFranchise != null) {
            tfNomFranchise.clear();
        }
        if (tfSiegeSocial != null) {
            tfSiegeSocial.clear();
        }
        lvGerantFranchise.getSelectionModel().clearSelection();
    }

    private void showError() {
        Stage stage = new Stage();
        stage.setTitle("Erreur");

        Label label = new Label("Veuillez remplir tous les champs.");
        StackPane root = new StackPane(label);

        stage.setScene(new Scene(root, 320, 120));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}