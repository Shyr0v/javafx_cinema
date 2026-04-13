package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.BO.Franchise;
import cinema.DAO.CinemaDAO;
import cinema.DAO.FranchiseDAO;
import javafx.beans.property.SimpleStringProperty;
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

        tvCinema.setItems(getCinema());
    }

    private ObservableList<Cinema> getCinema() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        List<Cinema> mesCinemas = cinemaDAO.findAll();
        return FXCollections.observableArrayList(mesCinemas);
    }

    private String getNomFranchise(int idFranchise) {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        Franchise franchise = franchiseDAO.find(idFranchise);

        if (franchise != null) {
            return franchise.getNomFranchise();
        }
        return "";
    }

    @FXML
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

            {
                btn.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());
                    CinemaDAO cinemaDAO = new CinemaDAO();

                    boolean deleted = cinemaDAO.delete(cinema);
                    if (deleted) {
                        tvCinema.getItems().remove(cinema);
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