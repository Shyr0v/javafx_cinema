package cinema.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * Contrôleur parent dont héritent tous les contrôleurs de l'application (sauf ConnexionController).
 * Il contient la barre de menu partagée et les méthodes communes de navigation.
 * Grâce à l'héritage, chaque page hérite automatiquement du menu sans dupliquer le code.
 */
public class MenuController {

    // Éléments du menu déclarés dans le FXML de chaque vue via fx:id
    @FXML
    protected MenuItem bListeFranchise, bAjouterFranchise, bListeCinema, bAjouterCinema,
            bQuitter, bAccueil, bListeSalle, bAjouterSalle;

    /**
     * Nom de l'utilisateur connecté, propagé de page en page pour maintenir la session.
     * Protected pour être accessible depuis toutes les classes filles sans getter/setter.
     */
    protected String nameUti;

    /**
     * Reçoit et stocke le nom de l'utilisateur connecté.
     * Appelé depuis chaque contrôleur au moment du changement de page.
     * @param nameUti Nom complet de l'utilisateur (ex: "DUPONT Jean")
     */
    public void setName(String nameUti) {
        this.nameUti = nameUti;
    }

    /**
     * Applique le logo sur un stage en déléguant à Navigation.
     * Centralisé ici pour que toutes les classes filles puissent l'appeler sans importer Navigation.
     * @param stage La fenêtre sur laquelle appliquer le logo
     */
    protected void appliquerLogo(Stage stage) {
        Navigation.applyLogo(stage); // délègue à Navigation, pas de duplication du chargement d'image
    }

    /**
     * Méthode utilitaire pour changer de scène sur un stage existant.
     * Regroupe les 4 opérations toujours faites ensemble : setScene, setTitle, setResizable, show.
     * @param stage  La fenêtre à mettre à jour
     * @param root   Le nouveau contenu FXML chargé
     * @param titre  Le titre à afficher dans la barre de la fenêtre
     */
    protected void changerScene(Stage stage, Parent root, String titre) {
        stage.setScene(new Scene(root));
        stage.setTitle(titre);
        stage.setResizable(false);
        appliquerLogo(stage); // logo réappliqué à chaque changement de scène
        stage.show();
    }

    /**
     * Récupère le Stage depuis un clic sur un MenuItem du menu.
     * Un MenuItem n'est pas un Node standard donc on ne peut pas faire getScene().getWindow() directement.
     * On passe par getParentPopup().getOwnerWindow() qui remonte jusqu'à la fenêtre parente.
     * @param event L'événement déclenché par le clic sur le MenuItem
     * @return Le Stage (fenêtre) contenant le menu
     */
    private Stage getStageDepuisMenu(ActionEvent event) {
        return (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
    }

    /**
     * Quitte l'application proprement.
     * Platform.exit() est préféré à System.exit() car il ferme JavaFX correctement
     * et déclenche les listeners d'arrêt si besoin.
     */
    @FXML
    public void bQuitterClick(ActionEvent event) {
        Platform.exit();
    }

    /** Navigue vers la page d'accueil depuis le menu. */
    @FXML
    public void bAccueilClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_accueil.fxml"));
            Parent root = loader.load();

            AccueilController controller = loader.getController();
            controller.setName(nameUti); // transmet le nom pour maintenir la session
            controller.setBienvenue();   // met à jour le label de bienvenue

            changerScene(stage, root, "Accueil");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Navigue vers la liste des franchises depuis le menu. */
    @FXML
    public void bListFranchiseClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_franchise.fxml"));
            Parent root = loader.load();

            ListeFranchiseController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Liste franchises");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Navigue vers le formulaire d'ajout de franchise depuis le menu. */
    @FXML
    public void bAjouterFranchiseClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_franchise.fxml"));
            Parent root = loader.load();

            AjouterFranchiseController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Ajouter une franchise");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Navigue vers la liste des cinémas depuis le menu. */
    @FXML
    public void bListeCinemaClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_cinema.fxml"));
            Parent root = loader.load();

            ListeCinemaController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Liste cinémas");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Navigue vers le formulaire d'ajout de cinéma depuis le menu. */
    @FXML
    public void bAjouterCinemaClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_cinema.fxml"));
            Parent root = loader.load();

            AjouterCinemaController controller = loader.getController();
            controller.setName(nameUti);

            changerScene(stage, root, "Ajouter un cinéma");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Navigue vers la liste des salles depuis le menu. */
    @FXML
    public void bListeSalleClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_liste_salle.fxml"));
            Parent root = loader.load();

            ListeSalleController controller = loader.getController();
            controller.setName(nameUti); // transmet le nom pour maintenir la session

            changerScene(stage, root, "Liste salles");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Navigue vers le formulaire d'ajout de salle depuis le menu. */
    @FXML
    public void bAjouterSalleClick(ActionEvent event) {
        Stage stage = getStageDepuisMenu(event);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cinema/views/page_ajout_salle.fxml"));
            Parent root = loader.load();

            AjouterSalleController controller = loader.getController();
            controller.setName(nameUti); // transmet le nom pour maintenir la session

            changerScene(stage, root, "Ajouter une salle");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}