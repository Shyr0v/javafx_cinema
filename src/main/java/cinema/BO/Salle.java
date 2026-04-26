package cinema.BO;

public class Salle {

    private int idSalle; // identifiant unique de la salle, généré automatiquement par la base
    private int numero; // numéro de la salle dans le cinéma
    private String description; // description textuelle de la salle (max 255 caractères)
    private int nbPlaces; // nombre de places disponibles dans la salle
    private int idCinema; // identifiant du cinéma auquel appartient la salle (clé étrangère)

    public Salle(int idSalle, int numero, String description, int nbPlaces, int idCinema) {
        this.idSalle = idSalle; // initialise l'identifiant
        this.numero = numero; // initialise le numéro
        this.description = description; // initialise la description
        this.nbPlaces = nbPlaces; // initialise le nombre de places
        this.idCinema = idCinema; // initialise l'id du cinéma lié
    }

    public int getIdSalle() { return idSalle; } // retourne l'id de la salle

    public int getNumero() { return numero; } // retourne le numéro
    public void setNumero(int numero) { this.numero = numero; } // modifie le numéro

    public String getDescription() { return description; } // retourne la description
    public void setDescription(String description) { this.description = description; } // modifie la description

    public int getNbPlaces() { return nbPlaces; } // retourne le nombre de places
    public void setNbPlaces(int nbPlaces) { this.nbPlaces = nbPlaces; } // modifie le nombre de places

    public int getIdCinema() { return idCinema; } // retourne l'id du cinéma
    public void setIdCinema(int idCinema) { this.idCinema = idCinema; } // modifie l'id du cinéma

}