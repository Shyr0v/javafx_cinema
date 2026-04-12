package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ListeCinemaController extends MenuController implements Initializable {

    @FXML
    private TableView<Cinema> tvCinema;

    @FXML
    private TableColumn<Cinema, String> tcDenomination, tcFranchise;

    @FXML
    private TableColumn<Cinema, Void> tcModif, tcSupp;

    @FXML
    private Button bRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tcDenomination.setCellValueFactory(new PropertyValueFactory<>("denomination"));
        tcFranchise.setCellValueFactory(cellData ->
                new SimpleStringProperty(getNomFranchise(cellData.getValue().getIdFranchise())));

        btnModif();
        btnSupp();

        ObservableList<Cinema> data = getCinema();
        tvCinema.setItems(data);
    }

    private ObservableList<Cinema> getCinema() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        List<Cinema> mesCinemas = cinemaDAO.findAll();
        return FXCollections.observableArrayList(mesCinemas);
    }

    private String getNomFranchise(int idFranchise) {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        if (franchiseDAO.find(idFranchise) != null) {
            return franchiseDAO.find(idFranchise).getNomFranchise();
        }
        return "";
    }

    public void bRetourClick(ActionEvent actionEvent) {
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
            stage.setTitle("Liste franchises");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btnModif() {
        tcModif.setCellFactory(column -> new TableCell<Cinema, Void>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());
                    Stage stageP = (Stage) bRetour.getScene().getWindow();
                    stageP.close();
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_cinema.fxml"));
                        Parent root = fxmlLoader.load();

                        ModifierCinemaController modifierCinemaController = fxmlLoader.getController();
                        modifierCinemaController.setName(nameUti);
                        modifierCinemaController.setIdSec(cinema.getIdCinema());
                        modifierCinemaController.setAttrinuts();

                        Stage stage = new Stage();
                        stage.setTitle("Modifier un cinéma");
                        stage.setScene(new Scene(root));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void btnSupp() {
        tcSupp.setCellFactory(col -> new TableCell<Cinema, Void>() {
            private final Button btn = new Button("Supprimer");
            // crée le bouton supprimer pour chaque ligne

            {
                btn.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());
                    // récupère le cinéma de la ligne cliquée

                    try {
                        CinemaDAO cinemaDAO = new CinemaDAO();
                        // crée l'accès aux données des cinémas

                        cinemaDAO.delete(cinema);
                        // supprime le cinéma en base

                        tvCinema.getItems().remove(cinema);
                        // retire le cinéma du tableau

                    } catch (Exception e) {
                        Alert alert = new Alert(AlertType.ERROR);
                        // crée une alerte d'erreur

                        alert.setTitle("Suppression impossible");
                        // définit le titre

                        alert.setHeaderText(null);
                        // enlève l'en-tête

                        alert.setContentText("Le cinéma n'a pas pu être supprimé.");
                        // définit le message affiché

                        alert.showAndWait();
                        // affiche l'alerte
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // met à jour la cellule

                setGraphic(empty ? null : btn);
                // affiche le bouton si la ligne existe
            }
        });
    }
}