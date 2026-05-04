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
    public void initialize(URL location, ResourceBundle resources) { // appelée automatiquement par JavaFX à l'ouverture de la page
        tcNumero.setCellValueFactory(new PropertyValueFactory<>("numero")); // dit à JavaFX d'appeler getNumero() sur chaque Salle pour remplir cette colonne
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description")); // idem, appelle getDescription()
        tcNbPlaces.setCellValueFactory(new PropertyValueFactory<>("nbPlaces")); // idem, appelle getNbPlaces()
        tcCinema.setCellValueFactory(cellData -> // pour la colonne cinéma on ne peut pas utiliser PropertyValueFactory car la valeur vient d'un autre DAO
                new SimpleStringProperty(getNomCinema(cellData.getValue().getIdCinema()))); // cellData.getValue() = l'objet Salle de la ligne, on récupère son idCinema pour chercher le nom

        btnModif(); // prépare les boutons Modifier dans chaque ligne du tableau
        btnSupp(); // prépare les boutons Supprimer dans chaque ligne du tableau

        tvSalle.setItems(getSalles()); // charge toutes les salles depuis la base et les affiche dans le tableau
    }

    private ObservableList<Salle> getSalles() {
        SalleDAO salleDAO = new SalleDAO(); // crée un accès à la table salle en base
        List<Salle> mesSalles = salleDAO.findAll(); // récupère toutes les salles depuis la base sous forme de liste Java classique
        return FXCollections.observableArrayList(mesSalles); // convertit en ObservableList, liste spéciale que JavaFX surveille pour mettre à jour le tableau automatiquement
    }

    private String getNomCinema(int idCinema) {
        CinemaDAO cinemaDAO = new CinemaDAO(); // crée un accès à la table cinema en base
        if (cinemaDAO.find(idCinema) != null) { // vérifie que le cinéma existe encore en base avant d'appeler getDenomination()
            return cinemaDAO.find(idCinema).getDenomination(); // retourne le nom du cinéma plutôt que son id
        }
        return ""; // retourne vide si le cinéma a été supprimé entre temps
    }

    public void bRetourClick(ActionEvent actionEvent) {
        Stage stageP = (Stage) bRetour.getScene().getWindow(); // remonte du bouton vers la scène puis vers la fenêtre pour pouvoir la fermer
        stageP.close(); // ferme la fenêtre actuelle
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml")); // chemin vers le FXML de la page d'accueil
            Parent root = fxmlLoader.load(); // charge le fichier FXML et crée tous les composants graphiques
            AccueilController accueilController = fxmlLoader.getController(); // récupère le contrôleur de la page d'accueil
            accueilController.setName(nameUti); // passe le nom de l'utilisateur connecté à la page d'accueil
            accueilController.setBienvenue(); // met à jour le label de bienvenue avec le nom
            Stage stage = new Stage(); // crée une nouvelle fenêtre
            stage.setTitle("Accueil"); // définit le titre de la fenêtre
            stage.setScene(new Scene(root)); // associe le contenu FXML à la fenêtre
            stage.initModality(Modality.APPLICATION_MODAL); // bloque les autres fenêtres tant que celle-ci est ouverte
            stage.show(); // affiche la fenêtre
        } catch (Exception e) {
            e.printStackTrace(); // affiche l'erreur dans la console si le chargement du FXML échoue
        }
    }

    private void btnModif() {
        tcModif.setCellFactory(column -> new TableCell<Salle, Void>() { // définit comment chaque cellule de la colonne Modifier est rendue
            private final Button btn = new Button("Modifier"); // crée un bouton Modifier pour chaque ligne

            {
                btn.setOnAction(event -> { // définit ce qui se passe quand on clique sur le bouton
                    Salle salle = getTableView().getItems().get(getIndex()); // getIndex() = numéro de la ligne, getItems() = liste du tableau, on combine pour avoir la salle de cette ligne
                    Stage stageP = (Stage) bRetour.getScene().getWindow(); // récupère la fenêtre actuelle
                    stageP.close(); // ferme la fenêtre de liste
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(
                                getClass().getResource("/cinema/views/page_modif_salle.fxml")); // charge le formulaire de modification
                        Parent root = fxmlLoader.load(); // charge le FXML
                        ModifierSalleController modifierSalleController = fxmlLoader.getController(); // récupère le contrôleur de modification
                        modifierSalleController.setName(nameUti); // passe le nom utilisateur
                        modifierSalleController.setIdSalle(salle.getIdSalle()); // passe l'id de la salle à modifier, doit être fait avant setAttributs()
                        modifierSalleController.setAttributs(); // déclenche le chargement des données de la salle dans les champs du formulaire
                        Stage stage = new Stage(); // crée une nouvelle fenêtre
                        stage.setTitle("Modifier une salle"); // titre de la fenêtre
                        stage.setScene(new Scene(root)); // associe le FXML à la fenêtre
                        stage.initModality(Modality.APPLICATION_MODAL); // bloque les autres fenêtres
                        stage.show(); // affiche la fenêtre
                    } catch (Exception e) {
                        e.printStackTrace(); // affiche l'erreur si le chargement échoue
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty); // appel obligatoire sinon comportements visuels étranges lors du scroll
                setGraphic(empty ? null : btn); // affiche le bouton si la ligne a une salle, sinon affiche rien
            }
        });
    }

    private void btnSupp() {
        tcSupp.setCellFactory(col -> new TableCell<Salle, Void>() { // même principe que btnModif, définit le rendu de chaque cellule de la colonne Supprimer
            private final Button btn = new Button("Supprimer"); // crée un bouton Supprimer pour chaque ligne

            {
                btn.setOnAction(event -> { // définit ce qui se passe quand on clique sur le bouton
                    Salle salle = getTableView().getItems().get(getIndex()); // récupère la salle de la ligne cliquée
                    try {
                        SalleDAO salleDAO = new SalleDAO(); // crée un accès à la table salle en base
                        salleDAO.delete(salle); // supprime la salle en base via le DAO
                        tvSalle.getItems().remove(salle); // retire la salle de la ObservableList, le tableau se met à jour instantanément sans recharger la page
                    } catch (Exception e) {
                        Alert alert = new Alert(AlertType.ERROR); // crée une alerte d'erreur
                        alert.setTitle("Suppression impossible"); // titre de l'alerte
                        alert.setHeaderText(null); // pas d'en-tête
                        alert.setContentText("La salle n'a pas pu être supprimée."); // message affiché
                        alert.showAndWait(); // affiche l'alerte et attend que l'utilisateur ferme
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty); // appel obligatoire
                setGraphic(empty ? null : btn); // affiche le bouton si la ligne a une salle, sinon affiche rien
            }
        });
    }
}