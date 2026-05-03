package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import cinema.BO.Cinema;
import cinema.BO.Franchise;
import cinema.BO.Utilisateur;
import cinema.DAO.CinemaDAO;
import cinema.DAO.FranchiseDAO;
import cinema.DAO.UtilisateurDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;

public class ListeFranchiseController extends MenuController implements Initializable {

    @FXML
    private TableView<Franchise> tvFranchises;

    @FXML
    private TableColumn<Franchise, String> tcNomFranchise;

    @FXML
    private TableColumn<Franchise, String> tcSiegeSocial;

    @FXML
    private TableColumn<Franchise, String> tcGerant;

    @FXML
    private TableColumn<Franchise, Void> tcModifier;

    @FXML
    private TableColumn<Franchise, Void> tcSupprimer;

    @FXML
    private Button bRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UtilisateurDAO gerantDAO = new UtilisateurDAO();

        Map<Integer, Utilisateur> gerants = gerantDAO.findAll()
                .stream()
                .collect(Collectors.toMap(Utilisateur::getIdUtilisateur, u -> u));

        tcGerant.setCellValueFactory(cellData -> {
            Utilisateur gerant = gerants.get(cellData.getValue().getIdGerant());
            return new SimpleStringProperty(
                    gerant != null ? gerant.getNom() + " " + gerant.getPrenom() : "Aucun gérant");
        });

        tcNomFranchise.setCellValueFactory(new PropertyValueFactory<>("nomFranchise"));
        tcSiegeSocial.setCellValueFactory(new PropertyValueFactory<>("siegeSocial"));
        tvFranchises.setItems(getFranchiseList());

        addButtonModifierToTable();
        addButtonSupprimerToTable();
    }

    private ObservableList<Franchise> getFranchiseList() {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        List<Franchise> franchises = franchiseDAO.findAll();
        return FXCollections.observableArrayList(franchises);
    }

    @FXML
    private void bRetourClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            AccueilController controller = loader.getController();
            controller.setName(nameUti);
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

    private void addButtonModifierToTable() {
        tcModifier.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Franchise franchise = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_franchise.fxml"));
                        Parent root = loader.load();

                        ModifierFranchiseController controller = loader.getController();
                        controller.setAttributes(franchise);
                        controller.setName(nameUti);

                        Stage stage = (Stage) bRetour.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Modification franchise");
                        stage.setResizable(false);
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

    private void addButtonSupprimerToTable() {
        tcSupprimer.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    Franchise franchise = getTableView().getItems().get(getIndex());

                    CinemaDAO cinemaDAO = new CinemaDAO();
                    List<Cinema> cinemas = cinemaDAO.findAll();

                    boolean utilisee = false;
                    for (Cinema cinema : cinemas) {
                        if (cinema.getIdFranchise() == franchise.getIdFranchise()) {
                            utilisee = true;
                            break;
                        }
                    }

                    if (!utilisee) {
                        FranchiseDAO franchiseDAO = new FranchiseDAO();
                        boolean deleted = franchiseDAO.delete(franchise);
                        if (deleted) {
                            tvFranchises.getItems().remove(franchise);
                        }
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