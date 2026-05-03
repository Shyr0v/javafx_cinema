package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Contrôleur de la page d'accueil (page_accueil.fxml).
 * Hérite de MenuController pour bénéficier de la barre de navigation.
 * Son seul rôle métier est d'afficher le message de bienvenue personnalisé.
 */
public class AccueilController extends MenuController implements Initializable {

    @FXML
    private Label bienvenue; // Label où sera affiché "BONJOUR NOM PRENOM"

    private Utilisateur utilisateur; // Objet métier de l'utilisateur connecté

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX après le chargement du FXML.
     * Rien à initialiser ici : l'affichage dépend de l'utilisateur transmis après le login.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pas d'initialisation nécessaire
    }

    /**
     * Reçoit l'objet Utilisateur depuis ConnexionController après une connexion réussie.
     * C'est le pont entre l'authentification et l'affichage personnalisé.
     * On stocke aussi le nom dans nameUti (hérité de MenuController) pour le propager
     * aux pages suivantes même si on n'a plus l'objet Utilisateur complet.
     * @param utilisateur L'objet métier contenant les infos de la personne connectée
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur != null) {
            // Stocke le nom complet dans nameUti pour la propagation entre pages via le menu
            this.nameUti = utilisateur.getNom() + " " + utilisateur.getPrenom();
        }
    }

    /**
     * Met à jour le texte du Label bienvenue avec le nom de l'utilisateur.
     * Deux cas possibles :
     * - Cas standard (depuis ConnexionController) : on a l'objet Utilisateur complet
     * - Cas de retour depuis une autre page : on n'a que la String nameUti
     * toUpperCase() garantit un rendu uniforme quelle que soit la casse en base de données.
     */
    public void setBienvenue() {
        if (utilisateur != null) {
            // Cas standard : affichage depuis nom et prénom de l'objet Utilisateur
            bienvenue.setText("BONJOUR " + utilisateur.getNom().toUpperCase() + " "
                    + utilisateur.getPrenom().toUpperCase());
        } else if (nameUti != null) {
            // Cas de secours : utilise la chaîne transmise entre les contrôleurs
            bienvenue.setText("BONJOUR " + nameUti.toUpperCase());
        }
    }
}