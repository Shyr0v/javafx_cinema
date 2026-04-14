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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifierFranchiseController extends MenuController implements Initializable {

    @FXML
    private TextField tfNomFranchise, tfSiegeSocial;

    @FXML
    private Button bRetour;

    @FXML
    private ListView<Utilisateur> lvGerantFranchise;

    private int idFranchise;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvGerantFranchise.setItems(getUtilisateurList());
    }

    private ObservableList<Utilisateur> getUtilisateurList() {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        return FXCollections.observableArrayList(utilisateurs);
    }

    public void setAttributes(Franchise franchise) {
        tfNomFranchise.setText(franchise.getNomFranchise());
        tfSiegeSocial.setText(franchise.getSiegeSocial());
        lvGerantFranchise.getSelectionModel().select(franchise.getIdGerant() - 1);
        this.idFranchise = franchise.getIdFranchise();
    }

    @FXML
    private void bEnregistrerClick(ActionEvent event) {
        String nom = tfNomFranchise.getText().trim();
        String siegeSocial = tfSiegeSocial.getText().trim();
        Utilisateur selected = lvGerantFranchise.getSelectionModel().getSelectedItem();

        if (!nom.isEmpty() && !siegeSocial.isEmpty() && selected != null) {
            Franchise newFranchise = new Franchise(this.idFranchise, nom, siegeSocial, selected.getIdUtilisateur());

            FranchiseDAO franchiseDAO = new FranchiseDAO();
            boolean controle = franchiseDAO.update(newFranchise);

            if (controle) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
                    Parent root = loader.load();

                    ListeFranchiseController controller = loader.getController();
                    controller.setName(nameUti);

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

    @FXML
    private void bRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
            Parent root = loader.load();

            ListeFranchiseController controller = loader.getController();
            controller.setName(nameUti);

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