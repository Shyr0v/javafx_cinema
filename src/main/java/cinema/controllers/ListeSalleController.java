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
    private TableView<Salle> tvSalle; // tableau qui affiche les objets Salle

    @FXML
    private TableColumn<Salle, Integer> tcNumero, tcNbPlaces; // colonnes affichant des entiers

    @FXML
    private TableColumn<Salle, String> tcDescription, tcCinema; // colonnes affichant des String

    @FXML
    private TableColumn<Salle, Void> tcModif, tcSupp; // colonnes sans donnée, contiennent des boutons

    @FXML
    private Button bRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tcNumero.setCellValueFactory(new PropertyValueFactory<>("numero")); // JavaFX appelle getNumero() sur chaque Salle
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description")); // JavaFX appelle getDescription()
        tcNbPlaces.setCellValueFactory(new PropertyValueFactory<>("nbPlaces")); // JavaFX appelle getNbPlaces()
        tcCinema.setCellValueFactory(cellData ->
                new SimpleStringProperty(getNomCinema(cellData.getValue().getIdCinema()))); // lambda car nécessite un appel DAO supplémentaire

        btnModif(); // configure les boutons Modifier dans la colonne tcModif
        btnSupp(); // configure les boutons Supprimer dans la colonne tcSupp

        tvSalle.setItems(getSalles()); // charge les données et les passe au tableau
    }

    private ObservableList<Salle> getSalles() {
        SalleDAO salleDAO = new SalleDAO();
        List<Salle> mesSalles = salleDAO.findAll(); // récupère toutes les salles depuis la base
        return FXCollections.observableArrayList(mesSalles); // convertit en ObservableList que JavaFX peut surveiller
    }

    private String getNomCinema(int idCinema) {
        CinemaDAO cinemaDAO = new CinemaDAO();
        if (cinemaDAO.find(idCinema) != null) {
            return cinemaDAO.find(idCinema).getDenomination(); // retourne le nom lisible plutôt que l'id
        }
        return ""; // retourne vide si le cinéma n'existe plus en base
    }

    public void bRetourClick(ActionEvent actionEvent) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = fxmlLoader.load();
            AccueilController accueilController = fxmlLoader.getController();
            accueilController.setName(nameUti); // propage le nom utilisateur vers l'accueil
            accueilController.setBienvenue(); // met à jour le label de bienvenue
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
            private final Button btn = new Button("Modifier"); // un bouton par cellule
            {
                btn.setOnAction(event -> {
                    Salle salle = getTableView().getItems().get(getIndex()); // getIndex() = numéro de la ligne cliquée
                    Stage stageP = (Stage) bRetour.getScene().getWindow();
                    stageP.close();
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_salle.fxml"));
                        Parent root = fxmlLoader.load();
                        ModifierSalleController modifierSalleController = fxmlLoader.getController();
                        modifierSalleController.setName(nameUti);
                        modifierSalleController.setIdSalle(salle.getIdSalle()); // passe l'id avant setAttributs()
                        modifierSalleController.setAttributs(); // déclenche le chargement des données dans les champs
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
                super.updateItem(item, empty); // obligatoire, initialise la cellule correctement
                setGraphic(empty ? null : btn); // n'affiche le bouton que si la ligne contient une salle
            }
        });
    }

    private void btnSupp() {
        tcSupp.setCellFactory(col -> new TableCell<Salle, Void>() {
            private final Button btn = new Button("Supprimer");
            {
                btn.setOnAction(event -> {
                    Salle salle = getTableView().getItems().get(getIndex()); // récupère la salle de la ligne cliquée
                    try {
                        SalleDAO salleDAO = new SalleDAO();
                        salleDAO.delete(salle); // supprime en base
                        tvSalle.getItems().remove(salle); // retire de la ObservableList → tableau mis à jour instantanément
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