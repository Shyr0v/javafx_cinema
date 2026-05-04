package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Cinema;
import cinema.BO.Franchise;
import cinema.DAO.CinemaDAO;
import cinema.DAO.FranchiseDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Contrôleur de la page de modification d'un cinéma (page_modif_cinema.fxml).
 * Hérite de MenuController pour la barre de navigation.
 * Les champs sont pré-remplis avec les valeurs actuelles via setAttrinuts().
 * L'ordre d'appel depuis ListeCinemaController est : setIdSec() → setAttrinuts().
 */
public class ModifierCinemaController extends MenuController implements Initializable {

    @FXML
    private TextArea taLibSec; // champ pré-rempli avec la dénomination actuelle (TextArea pour textes longs)

    @FXML
    private TextField tfAdresse, tfVille; // champs pré-remplis avec l'adresse et la ville actuelles

    @FXML
    private ComboBox<Franchise> cbFranchise; // combobox pré-sélectionnée sur la franchise actuelle

    private int idSec; // id du cinéma à modifier, reçu depuis ListeCinemaController

    @FXML
    private Button bRetour, bEnregistrer; // boutons retour et enregistrer

    /**
     * Initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Charge les franchises dans la ComboBox immédiatement pour qu'elle soit prête
     * avant l'appel à setAttrinuts() qui va sélectionner la bonne franchise.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerFranchises(); // doit être fait avant setAttrinuts() pour que la sélection fonctionne
    }

    /**
     * Reçoit l'id du cinéma à modifier depuis ListeCinemaController.
     * Doit être appelé AVANT setAttrinuts().
     * @param idSec L'identifiant du cinéma à modifier
     */
    public void setIdSec(int idSec) {
        this.idSec = idSec;
    }

    /**
     * Pré-remplit les champs avec les valeurs actuelles du cinéma.
     * Doit être appelé APRÈS setIdSec() car utilise this.idSec pour charger depuis la base.
     */
    public void setAttrinuts() {
        CinemaDAO cinemaDAO = new CinemaDAO();
        Cinema cinema = cinemaDAO.find(idSec); // récupère le cinéma par son id

        if (cinema != null) {
            taLibSec.setText(cinema.getDenomination());
            tfAdresse.setText(cinema.getAdresse());
            tfVille.setText(cinema.getVille());
            selectionnerFranchise(cinema.getIdFranchise()); // sélectionne la franchise dans la ComboBox
        }
    }

    /**
     * Charge toutes les franchises dans la ComboBox et configure leur affichage.
     * StringConverter est nécessaire pour que la ComboBox affiche le nom de la franchise
     * plutôt que le résultat de toString() de l'objet Franchise.
     */
    private void chargerFranchises() {
        FranchiseDAO franchiseDAO = new FranchiseDAO();
        cbFranchise.setItems(FXCollections.observableArrayList(franchiseDAO.findAll()));

        cbFranchise.setConverter(new StringConverter<Franchise>() {
            @Override
            public String toString(Franchise franchise) {
                return franchise == null ? "" : franchise.getNomFranchise(); // texte affiché dans la ComboBox
            }

            @Override
            public Franchise fromString(String string) {
                return null; // non utilisé (pas de saisie libre dans la ComboBox)
            }
        });
    }

    /**
     * Sélectionne la franchise correspondant à l'id donné dans la ComboBox.
     * On compare les ids et non les objets pour éviter les problèmes d'égalité Java
     * (deux objets distincts avec le même id ne sont pas == ni .equals() par défaut).
     * @param idFranchise L'id de la franchise à sélectionner
     */
    private void selectionnerFranchise(int idFranchise) {
        for (Franchise franchise : cbFranchise.getItems()) {
            if (franchise.getIdFranchise() == idFranchise) {
                cbFranchise.setValue(franchise); // sélectionne cette franchise
                break; // inutile de continuer à parcourir
            }
        }
    }

    /**
     * Retourne à la liste des cinémas sans sauvegarder les modifications.
     */
    @FXML
    private void bRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = loader.load();

            ListeCinemaController controller = loader.getController();
            controller.setName(nameUti); // transmet le nom pour maintenir la session

            Stage stage = (Stage) bRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste cinémas");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Déclenché par le bouton "Enregistrer".
     * Valide la dénomination, reconstruit l'objet Cinema et l'envoie au DAO pour mise à jour.
     * Si la ComboBox est vide, conserve l'ancienne franchise pour ne pas perdre l'association.
     * Redirige vers la liste des cinémas si la mise à jour réussit.
     */
    @FXML
    private void bEnregistrerClick(ActionEvent event) {
        String lib = taLibSec.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String ville = tfVille.getText().trim();

        // Seule la dénomination est obligatoire (adresse et ville peuvent être vides)
        if (!lib.isEmpty()) {
            CinemaDAO cinemaDAO = new CinemaDAO();
            Cinema cinemaExistant = cinemaDAO.find(idSec); // récupère l'état actuel pour conserver l'idFranchise

            int idFranchise = 0;
            if (cbFranchise.getValue() != null) {
                idFranchise = cbFranchise.getValue().getIdFranchise(); // franchise choisie par l'utilisateur
            } else if (cinemaExistant != null) {
                idFranchise = cinemaExistant.getIdFranchise(); // conserve l'ancienne franchise si la ComboBox est vide
            }

            // Reconstruit l'objet avec l'idSec existant pour que le DAO fasse un UPDATE et non un INSERT
            Cinema cinema = new Cinema(idSec, lib, adresse, ville, idFranchise);
            boolean ok = cinemaDAO.update(cinema);

            if (ok) {
                // Mise à jour réussie : retourne à la liste des cinémas
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
                    Parent root = loader.load();

                    ListeCinemaController controller = loader.getController();
                    controller.setName(nameUti); // transmet le nom pour maintenir la session

                    Stage stage = (Stage) bRetour.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Liste cinémas");
                    stage.setResizable(false);
                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}