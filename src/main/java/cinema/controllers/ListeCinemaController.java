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
import javafx.stage.Stage;

/**
 * Contrôleur de la page listant tous les cinémas (page_liste_cinema.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Affiche un tableau avec dénomination, franchise, et des boutons Voir salles / Modifier / Supprimer.
 */
public class ListeCinemaController extends MenuController implements Initializable {

    @FXML
    private TableView<Cinema> tvCinema;

    @FXML
    private TableColumn<Cinema, String> tcDenomination, tcFranchise;

    @FXML
    private TableColumn<Cinema, Void> tcVp, tcModif, tcSupp;
    // tcVp = colonne "Voir plus" : ouvre la liste des salles filtrées par cinéma

    @FXML
    private Button bRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tcDenomination.setCellValueFactory(new PropertyValueFactory<>("denomination"));
        tcFranchise.setCellValueFactory(cellData ->
                new SimpleStringProperty(getNomFranchise(cellData.getValue().getIdFranchise())));

        btnVoirPlus(); // configure les boutons "Voir salles" dans la colonne tcVp
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

    /**
     * Configure la colonne "Voir plus" : ouvre la page des salles filtrées
     * pour le cinéma de la ligne cliquée.
     * Passe l'objet Cinema à SallesCinemaController via setCinema().
     */
    private void btnVoirPlus() {
        tcVp.setCellFactory(column -> new TableCell<Cinema, Void>() {
            private final Button btn = new Button("Voir salles");

            {
                btn.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_salles_cinema.fxml"));
                        Parent root = loader.load();

                        SallesCinemaController controller = loader.getController();
                        controller.setName(nameUti);
                        controller.setCinema(cinema); // charge les salles de ce cinéma uniquement

                        Stage stage = (Stage) bRetour.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Salles de " + cinema.getDenomination());
                        stage.setResizable(false);
                        Navigation.applyLogo(stage);
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

    /**
     * Configure la colonne Modifier : ouvre la page de modification du cinéma.
     */
    private void btnModif() {
        tcModif.setCellFactory(column -> new TableCell<Cinema, Void>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_cinema.fxml"));
                        Parent root = loader.load();

                        ModifierCinemaController controller = loader.getController();
                        controller.setName(nameUti);
                        controller.setIdSec(cinema.getIdCinema());
                        controller.setAttrinuts();

                        Stage stage = (Stage) bRetour.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Modifier un cinéma");
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

    /**
     * Configure la colonne Supprimer : supprime le cinéma en base et retire la ligne du tableau.
     */
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