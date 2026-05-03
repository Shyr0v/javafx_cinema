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
 * Affiche un tableau avec la dénomination, la franchise et des boutons Modifier/Supprimer.
 */
public class ListeCinemaController extends MenuController implements Initializable {

    @FXML
    private TableView<Cinema> tvCinema; // tableau principal affichant les cinémas

    @FXML
    private TableColumn<Cinema, String> tcDenomination, tcFranchise; // colonnes texte

    @FXML
    private TableColumn<Cinema, Void> tcModif, tcSupp; // colonnes boutons (pas de données)

    @FXML
    private Button bRetour; // bouton de retour vers l'accueil

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // PropertyValueFactory appelle getDenomination() sur chaque objet Cinema
        tcDenomination.setCellValueFactory(new PropertyValueFactory<>("denomination"));

        // Lambda nécessaire car la franchise s'obtient via un appel DAO supplémentaire
        tcFranchise.setCellValueFactory(cellData ->
                new SimpleStringProperty(getNomFranchise(cellData.getValue().getIdFranchise())));

        btnModif(); // configure les boutons Modifier dans la colonne tcModif
        btnSupp();  // configure les boutons Supprimer dans la colonne tcSupp

        tvCinema.setItems(getCinema()); // charge les données et les passe au tableau
    }

    /**
     * Charge tous les cinémas depuis la base et les retourne en ObservableList.
     * ObservableList est requis par JavaFX pour que le tableau se mette à jour automatiquement.
     */
    private ObservableList<Cinema> getCinema() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        List<Cinema> mesCinemas = cinemaDAO.findAll();
        return FXCollections.observableArrayList(mesCinemas);
    }

    /**
     * Retourne le nom de la franchise associée à un cinéma.
     * Appelée pour chaque ligne du tableau lors du rendu de la colonne tcFranchise.
     * @param idFranchise L'identifiant de la franchise à retrouver
     * @return Le nom de la franchise, ou chaîne vide si introuvable
     */
    private String getNomFranchise(int idFranchise) {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        Franchise franchise = franchiseDAO.find(idFranchise);

        if (franchise != null) {
            return franchise.getNomFranchise();
        }
        return ""; // chaîne vide si la franchise a été supprimée
    }

    /**
     * Retourne à la page d'accueil en réutilisant le stage existant.
     */
    @FXML
    public void bRetourClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            AccueilController controller = loader.getController();
            controller.setName(nameUti);  // transmet le nom pour maintenir la session
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
     * Configure la colonne Modifier : crée un bouton par ligne qui ouvre la page de modification.
     * CellFactory est appelée pour chaque cellule ; on y crée un bouton avec son action.
     */
    private void btnModif() {
        tcModif.setCellFactory(column -> new TableCell<Cinema, Void>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex()); // récupère le cinéma de la ligne cliquée

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_cinema.fxml"));
                        Parent root = loader.load();

                        ModifierCinemaController controller = loader.getController();
                        controller.setName(nameUti);
                        controller.setIdSec(cinema.getIdCinema()); // transmet l'id avant de charger les attributs
                        controller.setAttrinuts(); // pré-remplit les champs avec les données actuelles

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
     * Configure la colonne Supprimer : crée un bouton par ligne qui supprime le cinéma en base
     * puis retire la ligne du tableau sans recharger toutes les données.
     */
    private void btnSupp() {
        tcSupp.setCellFactory(col -> new TableCell<Cinema, Void>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex()); // cinéma de la ligne cliquée
                    CinemaDAO cinemaDAO = new CinemaDAO();

                    boolean deleted = cinemaDAO.delete(cinema); // supprime en base
                    if (deleted) {
                        tvCinema.getItems().remove(cinema); // retire de la ObservableList → tableau mis à jour
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