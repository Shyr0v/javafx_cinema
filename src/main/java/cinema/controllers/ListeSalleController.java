package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cinema.BO.Salle;
import cinema.DAO.CinemaDAO;
import cinema.DAO.SalleDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ListeSalleController extends MenuController implements Initializable {

    @FXML
    private TableView<Salle> tvSalle;

    @FXML
    private TableColumn<Salle, Integer> tcNumero, tcNbPlaces;

    @FXML
    private TableColumn<Salle, String> tcDescription, tcCinema;

    @FXML
    private TableColumn<Salle, Void> tcModif, tcSupp;

    @FXML
    private Button bRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tcNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tcNbPlaces.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));
        tcCinema.setCellValueFactory(cellData ->
                new SimpleStringProperty(getNomCinema(cellData.getValue().getIdCinema())));

        btnModif();
        btnSupp();

        tvSalle.setItems(getSalles());
    }

    private ObservableList<Salle> getSalles() {
        SalleDAO salleDAO = new SalleDAO();
        List<Salle> mesSalles = salleDAO.findAll();
        return FXCollections.observableArrayList(mesSalles);
    }

    private String getNomCinema(int idCinema) {
        CinemaDAO cinemaDAO = new CinemaDAO();
        if (cinemaDAO.find(idCinema) != null) {
            return cinemaDAO.find(idCinema).getDenomination();
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
            stage.setTitle("Accueil");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btnModif() {
        tcModif.setCellFactory(column -> new TableCell<Salle, Void>() {
            private final Button btn = new Button("Modifier");
            {
                btn.setOnAction(event -> {
                    Salle salle = getTableView().getItems().get(getIndex());
                    Stage stageP = (Stage) bRetour.getScene().getWindow();
                    stageP.close();
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_salle.fxml"));
                        Parent root = fxmlLoader.load();
                        ModifierSalleController modifierSalleController = fxmlLoader.getController();
                        modifierSalleController.setName(nameUti);
                        modifierSalleController.setIdSalle(salle.getIdSalle());
                        modifierSalleController.setAttributs();
                        Stage stage = new Stage();
                        stage.setTitle("Modifier une salle");
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
        tcSupp.setCellFactory(col -> new TableCell<Salle, Void>() {
            private final Button btn = new Button("Supprimer");
            {
                btn.setOnAction(event -> {
                    Salle salle = getTableView().getItems().get(getIndex());
                    try {
                        SalleDAO salleDAO = new SalleDAO();
                        salleDAO.delete(salle);
                        tvSalle.getItems().remove(salle);
                    } catch (Exception e) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Suppression impossible");
                        alert.setHeaderText(null);
                        alert.setContentText("La salle n'a pas pu être supprimée.");
                        alert.showAndWait();
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

}