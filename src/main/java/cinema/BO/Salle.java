package cinema.BO;

/**
 * Classe métier représentant une salle de cinéma.
 * Correspond à la table "salle" en base de données.
 * Une salle appartient à un cinéma (relation N-1 via idCinema).
 */
public class Salle {

    private int idSalle;       // clé primaire, généré par auto-incrément SQL
    private int numero;        // numéro de la salle dans le cinéma (ex: 3, 12)
    private String description; // caractéristiques de la salle (ex: "4DX", "IMAX", "Dolby Atmos")
    private int nbPlaces;      // capacité totale de la salle en nombre de sièges
    private int idCinema;      // clé étrangère vers la table cinema

    /**
     * Constructeur principal utilisé par les DAO lors de la lecture depuis la base
     * et par les contrôleurs lors de la création ou modification d'une salle.
     * @param idSalle     Identifiant unique (0 pour un nouvel objet avant insertion)
     * @param numero      Numéro de la salle
     * @param description Description ou caractéristiques de la salle
     * @param nbPlaces    Nombre de places disponibles
     * @param idCinema    Identifiant du cinéma auquel appartient cette salle
     */
    public Salle(int idSalle, int numero, String description, int nbPlaces, int idCinema) {
        this.idSalle = idSalle;
        this.numero = numero;
        this.description = description;
        this.nbPlaces = nbPlaces;
        this.idCinema = idCinema;
    }

    /** @return L'identifiant unique de la salle */
    public int getIdSalle() { return idSalle; }

    /** @return Le numéro de la salle */
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    /** @return La description / caractéristiques de la salle */
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /** @return Le nombre de places de la salle */
    public int getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(int nbPlaces) { this.nbPlaces = nbPlaces; }

    /** @return L'identifiant du cinéma auquel appartient cette salle */
    public int getIdCinema() { return idCinema; }
    public void setIdCinema(int idCinema) { this.idCinema = idCinema; }
}