package cinema.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cinema.BO.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class AccueilController extends MenuController implements Initializable {

    @FXML
    private Label bienvenue;

    private Utilisateur utilisateur;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        if (utilisateur != null) {
            this.nameUti = utilisateur.getNom() + " " + utilisateur.getPrenom();
        }
    }

    public void setBienvenue() {
        if (utilisateur != null) {
            bienvenue.setText("BONJOUR " + utilisateur.getNom().toUpperCase() + " "
                    + utilisateur.getPrenom().toUpperCase());
        } else if (nameUti != null) {
            bienvenue.setText("BONJOUR " + nameUti.toUpperCase());
        }
    }
}