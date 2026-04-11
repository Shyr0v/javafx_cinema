package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.DAO.CinemaDAO;
import cinema.BO.Franchise;
import cinema.DAO.FranchiseDAO;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModifierCinemaController extends MenuController implements Initializable {

    @FXML
    private TextArea taLibSec;

    @FXML
    private TextField tfAdresse, tfVille;  // ← ajoutés

    @FXML
    private ComboBox<Franchise> cbFranchise;  // ← ajouté

    private int idSec;

    @FXML
    private Button bRetour, bEnregistrer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerFranchises();
    }

    public void setIdSec(int idSec) {
        this.idSec = idSec;
    }

    public void setAttrinuts() {
        CinemaDAO sectionDAO = new CinemaDAO();
        Cinema cinema = sectionDAO.find(idSec);  // ← renommé sec → cinema
        if (cinema != null) {
            taLibSec.setText(cinema.getDenomination());
            tfAdresse.setText(cinema.getAdresse());
            tfVille.setText(cinema.getVille());
            selectionnerFranchise(cinema.getIdFranchise());
        }
    }

    private void chargerFranchises() {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        cbFranchise.setItems(FXCollections.observableArrayList(franchiseDAO.findAll()));
        cbFranchise.setConverter(new StringConverter<Franchise>() {
            @Override
            public String toString(Franchise franchise) {
                return franchise == null ? "" : franchise.getNomFranchise();
            }

            @Override
            public Franchise fromString(String string) {
                return null;
            }
        });
    }

    private void selectionnerFranchise(int idFranchise) {
        for (Franchise franchise : cbFranchise.getItems()) {
            if (franchise.getIdFranchise() == idFranchise) {
                cbFranchise.setValue(franchise);
                break;
            }
        }
    }

    @FXML
    private void bRetourClick(ActionEvent event) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = fxmlLoader.load();

            ListeCinemaController listeCinemaController = fxmlLoader.getController();
            listeCinemaController.setName(nameUti);

            Stage stage = new Stage();
            stage.setTitle("Liste franchises");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void bEnregistrerClick(ActionEvent event) {
        String lib = taLibSec.getText();
        String adresse = tfAdresse.getText();
        String ville = tfVille.getText();

        if (!lib.trim().isEmpty()) {
            CinemaDAO cinemaDAO = new CinemaDAO();

            // Correction ligne 11 :
            // on récupère le cinéma existant pour éviter d'écraser
            // les données avec des valeurs incohérentes.
            Cinema cinemaExistant = cinemaDAO.find(idSec);

            int idFranchise = 0;

            // Si une franchise est sélectionnée, on prend son identifiant.
            if (cbFranchise.getValue() != null) {
                idFranchise = cbFranchise.getValue().getIdFranchise();
            }
            // Sinon, on conserve la franchise déjà enregistrée.
            else if (cinemaExistant != null) {
                idFranchise = cinemaExistant.getIdFranchise();
            }

            // Correction ligne 11 :
            // on reconstruit correctement l'objet Cinema avec
            // les vraies valeurs de chaque champ.
            Cinema cinema = new Cinema(idSec, lib, adresse, ville, idFranchise);

            boolean controle = cinemaDAO.update(cinema);

            if (controle) {
                Stage stageP = (Stage) bRetour.getScene().getWindow();
                stageP.close();
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(
                            getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
                    Parent root = fxmlLoader.load();

                    ListeCinemaController listeCinemaController = fxmlLoader.getController();
                    listeCinemaController.setName(nameUti);

                    Stage stage = new Stage();
                    stage.setTitle("Liste franchises");
                    stage.setScene(new Scene(root));

                    stage.initModality(Modality.APPLICATION_MODAL);

                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                // Cette popup sera corrigée à la ligne 12.
                FXMLLoader fxmlLoader = new FXMLLoader(
                        getClass().getResource("/cinema/views/popup_ajout_etu.fxml"));
                Parent root = fxmlLoader.load();

                Stage stage = new Stage();
                stage.setTitle("Pop-up");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}