package cinema.BO;

/**
 * Classe métier représentant un utilisateur de l'application.
 * Correspond à la table "utilisateur" en base de données.
 * Un utilisateur peut se connecter à l'application et être désigné gérant d'une ou plusieurs franchises.
 */
public class Utilisateur {

    private int idUtilisateur; // clé primaire
    private String nom;        // nom de famille
    private String prenom;     // prénom
    private String login;      // identifiant de connexion (email ou pseudo)
    private String mdp;        // mot de passe (stocké hashé en base via pgcrypto)

    /**
     * Constructeur vide requis par certains mécanismes JavaFX et pour les instanciations génériques.
     */
    public Utilisateur() {
    }

    /**
     * Constructeur principal utilisé par UtilisateurDAO lors de la lecture depuis la base.
     * @param idUtilisateur Identifiant unique
     * @param nom           Nom de famille
     * @param prenom        Prénom
     * @param login         Identifiant de connexion
     * @param mdp           Mot de passe (hashé en base, jamais affiché en clair)
     */
    public Utilisateur(int idUtilisateur, String nom, String prenom, String login, String mdp) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.mdp = mdp;
    }

    /** @return Le nom de famille de l'utilisateur */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    /** @return Le prénom de l'utilisateur */
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /** @return L'identifiant unique de l'utilisateur */
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    /** @return Le mot de passe (hashé, jamais affiché en clair dans l'interface) */
    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    /** @return Le login (identifiant de connexion) */
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Utilisé par les ListView pour afficher l'utilisateur de façon lisible.
     * Sans ce toString(), JavaFX afficherait l'adresse mémoire de l'objet.
     * @return "Nom Prenom" (ex: "DUPONT Jean")
     */
    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}