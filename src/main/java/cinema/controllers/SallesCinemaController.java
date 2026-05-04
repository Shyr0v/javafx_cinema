package cinema.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.BO.Salle;
import cinema.DAO.SalleDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Contrôleur de la page affichant les salles d'un cinéma spécifique (page_salles_cinema.fxml).
 * Reçoit un objet Cinema depuis ListeCinemaController via setCinema(),
 * puis charge uniquement les salles liées à ce cinéma via SalleDAO.findByCinema().
 */
public class SallesCinemaController extends MenuController implements Initializable {

    @FXML
    private Label lbTitreCinema; // affiche "Salles de : CinéMax Étoile"

    @FXML
    private TableView<Salle> tvSalles;

    @FXML
    private TableColumn<Salle, Integer> tcNumero, tcNbPlaces;

    @FXML
    private TableColumn<Salle, String> tcDescription;

    @FXML
    private Button bRetour;

    private Cinema cinema; // cinéma dont on affiche les salles

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes du tableau
        tcNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tcNbPlaces.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));
    }

    /**
     * Reçoit le cinéma sélectionné depuis ListeCinemaController.
     * Charge ensuite les salles filtrées et met à jour le titre.
     * Doit être appelé après le chargement du FXML.
     * @param cinema Le cinéma dont on veut voir les salles
     */
    public void setCinema(Cinema cinema) {
        this.cinema = cinema;

        // Met à jour le titre avec le nom du cinéma
        lbTitreCinema.setText("Salles de : " + cinema.getDenomination());

        // Charge uniquement les salles de ce cinéma
        tvSalles.setItems(getSallesByCinema(cinema.getIdCinema()));
    }

    /**
     * Appelle SalleDAO.findByCinema() pour ne récupérer que les salles
     * appartenant au cinéma passé en paramètre.
     * @param idCinema L'identifiant du cinéma à filtrer
     * @return Liste observable des salles du cinéma
     */
    private ObservableList<Salle> getSallesByCinema(int idCinema) {
        SalleDAO salleDAO = new SalleDAO();
        List<Salle> salles = salleDAO.findByCinema(idCinema); // méthode déjà présente dans SalleDAO
        return FXCollections.observableArrayList(salles);
    }

    /**
     * Retourne à la liste des cinémas.
     */
    @FXML
    public void bRetourClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = loader.load();

            ListeCinemaController controller = loader.getController();
            controller.setName(nameUti);

            Stage stage = (Stage) bRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste cinémas");
            stage.setResizable(false);
            Navigation.applyLogo(stage);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}