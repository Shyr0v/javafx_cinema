package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Contrôleur de la page d'accueil (page_accueil.fxml).
 * Cette classe hérite de MenuController pour bénéficier de la barre de navigation.
 */
public class AccueilController extends MenuController implements Initializable {

    @FXML
    private Label bienvenue; // Composant texte où sera affiché "Bonjour NOM PRENOM"

    private Utilisateur utilisateur; // Référence locale pour stocker l'utilisateur connecté

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX lors du chargement du FXML.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Rien à initialiser ici car l'affichage dépend de l'utilisateur transmis après le login
    }

    /**
     * Reçoit l'objet Utilisateur depuis le ConnexionController.
     * C'est ici qu'on fait le pont entre l'authentification et l'affichage personnalisé.
     * @param utilisateur L'objet métier contenant les infos de la personne connectée
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur != null) {
            // On stocke le nom complet dans la variable 'nameUti' héritée de MenuController
            // Cela permet de garder le nom affiché même si on change de page via le menu.
            this.nameUti = utilisateur.getNom() + " " + utilisateur.getPrenom();
        }
    }

    /**
     * Met à jour le texte du Label 'bienvenue'.
     * On utilise .toUpperCase() pour garantir un rendu professionnel et uniforme.
     */
    public void setBienvenue() {
        if (utilisateur != null) {
            // Cas standard : on a l'objet complet
            bienvenue.setText("BONJOUR " + utilisateur.getNom().toUpperCase() + " "
                    + utilisateur.getPrenom().toUpperCase());
        } else if (nameUti != null) {
            // Cas de secours : on utilise la chaîne de caractères transmise entre les menus
            bienvenue.setText("BONJOUR " + nameUti.toUpperCase());
        }
    }
}