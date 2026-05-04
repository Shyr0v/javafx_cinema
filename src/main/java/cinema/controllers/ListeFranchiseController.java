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

/**
 * Contrôleur de la page listant toutes les franchises (page_liste_franchise.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Affiche un tableau avec nom, siège social, gérant et des boutons Modifier/Supprimer.
 */
public class ListeFranchiseController extends MenuController implements Initializable {

    @FXML
    private TableView<Franchise> tvFranchises; // tableau principal affichant les franchises

    @FXML
    private TableColumn<Franchise, String> tcNomFranchise; // colonne nom de la franchise

    @FXML
    private TableColumn<Franchise, String> tcSiegeSocial; // colonne siège social

    @FXML
    private TableColumn<Franchise, String> tcGerant; // colonne gérant (calculée via DAO)

    @FXML
    private TableColumn<Franchise, Void> tcModifier; // colonne bouton Modifier (pas de données)

    @FXML
    private TableColumn<Franchise, Void> tcSupprimer; // colonne bouton Supprimer (pas de données)

    @FXML
    private Button bRetour; // bouton de retour vers l'accueil

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * On charge tous les gérants en une seule requête et on les indexe dans une Map
     * pour éviter un appel DAO par ligne lors du rendu du tableau.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UtilisateurDAO gerantDAO = new UtilisateurDAO();

        // Charge tous les utilisateurs et les indexe par id pour un accès en O(1)
        // Collectors.toMap crée une Map<idUtilisateur, Utilisateur>
        Map<Integer, Utilisateur> gerants = gerantDAO.findAll()
                .stream()
                .collect(Collectors.toMap(Utilisateur::getIdUtilisateur, u -> u));

        // Colonne gérant : lambda car nécessite une lookup dans la Map
        tcGerant.setCellValueFactory(cellData -> {
            Utilisateur gerant = gerants.get(cellData.getValue().getIdGerant());
            return new SimpleStringProperty(
                    gerant != null ? gerant.getNom() + " " + gerant.getPrenom() : "Aucun gérant");
        });

        // Colonnes simples : PropertyValueFactory appelle automatiquement le getter correspondant
        tcNomFranchise.setCellValueFactory(new PropertyValueFactory<>("nomFranchise")); // getNomFranchise()
        tcSiegeSocial.setCellValueFactory(new PropertyValueFactory<>("siegeSocial"));   // getSiegeSocial()

        tvFranchises.setItems(getFranchiseList()); // charge les données dans le tableau

        addButtonModifierToTable();   // configure la colonne des boutons Modifier
        addButtonSupprimerToTable();  // configure la colonne des boutons Supprimer
    }

    /**
     * Charge toutes les franchises depuis la base et les retourne en ObservableList.
     * ObservableList est requis par JavaFX pour que le tableau se mette à jour automatiquement.
     */
    private ObservableList<Franchise> getFranchiseList() {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        List<Franchise> franchises = franchiseDAO.findAll();
        return FXCollections.observableArrayList(franchises);
    }

    /**
     * Retourne à la page d'accueil en réutilisant le stage existant.
     */
    @FXML
    private void bRetourClick() {
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
     * CellFactory est appelée pour chaque cellule du tableau ; on y crée un bouton avec son action.
     */
    private void addButtonModifierToTable() {
        tcModifier.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                // Action du bouton : récupère la franchise de la ligne et ouvre sa page de modification
                btn.setOnAction(event -> {
                    Franchise franchise = getTableView().getItems().get(getIndex()); // ligne cliquée

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_franchise.fxml"));
                        Parent root = loader.load();

                        ModifierFranchiseController controller = loader.getController();
                        controller.setAttributes(franchise); // pré-remplit les champs avec les données actuelles
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
     * Configure la colonne Supprimer : crée un bouton par ligne qui supprime la franchise.
     * La suppression est bloquée si la franchise est liée à au moins un cinéma en base.
     */
    private void addButtonSupprimerToTable() {
        tcSupprimer.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    Franchise franchise = getTableView().getItems().get(getIndex());

                    // Vérifie si la franchise est encore utilisée par un cinéma
                    CinemaDAO cinemaDAO = new CinemaDAO();
                    List<Cinema> cinemas = cinemaDAO.findAll();

                    boolean utilisee = false;
                    for (Cinema cinema : cinemas) {
                        if (cinema.getIdFranchise() == franchise.getIdFranchise()) {
                            utilisee = true; // au moins un cinéma référence cette franchise
                            break;
                        }
                    }

                    if (!utilisee) {
                        // Suppression autorisée : on met à jour la base puis le tableau
                        FranchiseDAO franchiseDAO = new FranchiseDAO();
                        boolean deleted = franchiseDAO.delete(franchise);
                        if (deleted) {
                            tvFranchises.getItems().remove(franchise); // retire la ligne du tableau
                        }
                    }
                    // Si utilisée, on ne fait rien (on pourrait afficher un message d'erreur ici)
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