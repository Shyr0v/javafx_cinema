package cinema.BO;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Classe métier représentant une franchise de cinémas.
 * Correspond à la table "franchise" en base de données.
 *
 * Les champs nomFranchise et siegeSocial utilisent SimpleStringProperty (JavaFX)
 * plutôt que de simples String. Cela permet à JavaFX de détecter automatiquement
 * les changements de valeur et de mettre à jour l'interface sans code supplémentaire
 * (binding réactif). PropertyValueFactory dans les TableView en tire également profit.
 */
public class Franchise {

    private int idFranchise;                                          // clé primaire
    private SimpleStringProperty nomFranchise = new SimpleStringProperty(); // nom de la franchise
    private SimpleStringProperty siegeSocial  = new SimpleStringProperty(); // adresse du siège
    private int idGerant;                                             // clé étrangère vers utilisateur

    /**
     * Constructeur vide requis par certains mécanismes de JavaFX (désérialisation FXML).
     */
    public Franchise() {
    }

    /**
     * Constructeur principal utilisé par les DAO et les contrôleurs.
     * @param idFranchise  Identifiant unique (0 pour un nouvel objet avant insertion)
     * @param nomFranchise Nom de la franchise (ex: "UGC")
     * @param siegeSocial  Adresse du siège social
     * @param idGerant     Identifiant de l'utilisateur gérant cette franchise
     */
    public Franchise(int idFranchise, String nomFranchise, String siegeSocial, int idGerant) {
        this.idFranchise = idFranchise;
        this.nomFranchise.set(nomFranchise); // .set() car c'est une Property, pas un String direct
        this.siegeSocial.set(siegeSocial);
        this.idGerant = idGerant;
    }

    /** @return L'identifiant unique de la franchise */
    public int getIdFranchise() {
        return idFranchise;
    }

    /** @return L'identifiant de l'utilisateur gérant cette franchise */
    public int getIdGerant() {
        return idGerant;
    }

    public void setIdGerant(int idGerant) {
        this.idGerant = idGerant;
    }

    /** @return Le nom de la franchise sous forme de String */
    public String getNomFranchise() {
        return nomFranchise.get(); // .get() pour extraire la valeur du Property
    }

    public void setNomFranchise(String nomFranchise) {
        this.nomFranchise.set(nomFranchise);
    }

    /** @return Le siège social sous forme de String */
    public String getSiegeSocial() {
        return siegeSocial.get();
    }

    public void setSiegeSocial(String siegeSocial) {
        this.siegeSocial.set(siegeSocial);
    }

    /**
     * Expose la propriété observable nomFranchise pour le binding JavaFX.
     * Utilisé par PropertyValueFactory pour lier la colonne du tableau à ce champ.
     * @return La StringProperty du nom de la franchise
     */
    public StringProperty nomFranchiseProperty() {
        return nomFranchise;
    }

    /**
     * Expose la propriété observable siegeSocial pour le binding JavaFX.
     * @return La StringProperty du siège social
     */
    public StringProperty siegeSocialProperty() {
        return siegeSocial;
    }

    /**
     * Utilisé par les ListView et ComboBox pour afficher la franchise de façon lisible.
     * Sans ce toString(), JavaFX afficherait l'adresse mémoire de l'objet.
     */
    @Override
    public String toString() {
        return nomFranchise.get();
    }
}