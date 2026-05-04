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

/**
 * Contrôleur de la page listant toutes les salles (page_liste_salle.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Affiche un tableau avec numéro, description, nombre de places, cinéma et des boutons Modifier/Supprimer.
 * Cette page est ouverte comme une fenêtre modale (new Stage avec APPLICATION_MODAL).
 */
public class ListeSalleController extends MenuController implements Initializable {

    @FXML
    private TableView<Salle> tvSalle; // tableau principal affichant les salles

    @FXML
    private TableColumn<Salle, Integer> tcNumero, tcNbPlaces; // colonnes entiers

    @FXML
    private TableColumn<Salle, String> tcDescription, tcCinema; // colonnes texte

    @FXML
    private TableColumn<Salle, Void> tcModif, tcSupp; // colonnes boutons (pas de données)

    @FXML
    private Button bRetour; // bouton de retour vers l'accueil

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tcNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));           // getNumero()
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description")); // getDescription()
        tcNbPlaces.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));       // getNbPlaces()

        // Lambda nécessaire car le nom du cinéma s'obtient via un appel DAO supplémentaire
        tcCinema.setCellValueFactory(cellData ->
                new SimpleStringProperty(getNomCinema(cellData.getValue().getIdCinema())));

        btnModif(); // configure les boutons Modifier
        btnSupp();  // configure les boutons Supprimer

        tvSalle.setItems(getSalles()); // charge les données dans le tableau
    }

    /**
     * Charge toutes les salles depuis la base et les retourne en ObservableList.
     */
    private ObservableList<Salle> getSalles() {
        SalleDAO salleDAO = new SalleDAO();
        List<Salle> mesSalles = salleDAO.findAll();
        return FXCollections.observableArrayList(mesSalles);
    }

    /**
     * Retourne le nom du cinéma associé à une salle.
     * Appelée pour chaque ligne du tableau lors du rendu de la colonne tcCinema.
     * @param idCinema L'identifiant du cinéma à retrouver
     * @return La dénomination du cinéma, ou chaîne vide si introuvable
     */
    private String getNomCinema(int idCinema) {
        CinemaDAO cinemaDAO = new CinemaDAO();
        if (cinemaDAO.find(idCinema) != null) {
            return cinemaDAO.find(idCinema).getDenomination();
        }
        return ""; // cinéma supprimé ou introuvable
    }

    /**
     * Ferme la fenêtre modale actuelle et ouvre la page d'accueil dans une nouvelle fenêtre modale.
     * On ferme d'abord (stageP.close()) avant d'ouvrir la nouvelle pour éviter les fenêtres superposées.
     */
    public void bRetourClick(ActionEvent actionEvent) {
        Stage stageP = (Stage) bRetour.getScene().getWindow();
        stageP.close(); // ferme la liste des salles
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = fxmlLoader.load();

            AccueilController accueilController = fxmlLoader.getController();
            accueilController.setName(nameUti);  // transmet le nom pour maintenir la session
            accueilController.setBienvenue();

            Stage stage = new Stage();
            stage.setTitle("Accueil");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            Navigation.applyLogo(stage); // logo appliqué sur le nouveau Stage
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configure la colonne Modifier : crée un bouton par ligne qui ouvre la page de modification.
     */
    private void btnModif() {
        tcModif.setCellFactory(column -> new TableCell<Salle, Void>() {
            private final Button btn = new Button("Modifier"); // un bouton par cellule
            {
                btn.setOnAction(event -> {
                    Salle salle = getTableView().getItems().get(getIndex()); // salle de la ligne cliquée
                    Stage stageP = (Stage) bRetour.getScene().getWindow();
                    stageP.close(); // ferme la liste avant d'ouvrir la modification
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_salle.fxml"));
                        Parent root = fxmlLoader.load();

                        ModifierSalleController modifierSalleController = fxmlLoader.getController();
                        modifierSalleController.setName(nameUti);
                        modifierSalleController.setIdSalle(salle.getIdSalle()); // id transmis AVANT setAttributs()
                        modifierSalleController.setAttributs(); // pré-remplit les champs avec les données actuelles

                        Stage stage = new Stage();
                        stage.setTitle("Modifier une salle");
                        stage.setScene(new Scene(root));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        Navigation.applyLogo(stage); // logo appliqué sur le nouveau Stage
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            /**
             * updateItem est appelée par JavaFX à chaque rendu de cellule.
             * On n'affiche le bouton que si la cellule correspond à une ligne non vide.
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty); // obligatoire pour réinitialiser correctement la cellule
                setGraphic(empty ? null : btn);
            }
        });
    }

    /**
     * Configure la colonne Supprimer : supprime la salle en base et retire la ligne du tableau.
     * En cas d'erreur (contrainte de clé étrangère par exemple), une alerte est affichée.
     */
    private void btnSupp() {
        tcSupp.setCellFactory(col -> new TableCell<Salle, Void>() {
            private final Button btn = new Button("Supprimer");
            {
                btn.setOnAction(event -> {
                    Salle salle = getTableView().getItems().get(getIndex()); // salle de la ligne cliquée
                    try {
                        SalleDAO salleDAO = new SalleDAO();
                        salleDAO.delete(salle);                  // supprime en base
                        tvSalle.getItems().remove(salle);        // retire de la ObservableList → tableau mis à jour
                    } catch (Exception e) {
                        // Affiche une alerte si la suppression échoue (contrainte, connexion, etc.)
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