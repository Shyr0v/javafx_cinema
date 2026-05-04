package cinema.BO;

/**
 * Classe métier (Business Object) représentant un cinéma.
 * Correspond à la table "cinema" en base de données.
 * Contient uniquement les données et leurs accesseurs, sans logique métier.
 */
public class Cinema {

    private int idCinema;       // clé primaire, généré par auto-incrément SQL
    private String denomination; // nom commercial du cinéma (ex: "UGC Nation")
    private String adresse;     // rue et numéro
    private String ville;       // ville de localisation
    private int idFranchise;    // clé étrangère vers la table franchise

    /**
     * Constructeur principal utilisé par les DAO lors de la lecture depuis la base
     * et par les contrôleurs lors de la création ou modification d'un cinéma.
     * @param idCinema      Identifiant unique (0 pour un nouvel objet avant insertion)
     * @param denomination  Nom commercial du cinéma
     * @param adresse       Adresse physique
     * @param ville         Ville
     * @param idFranchise   Identifiant de la franchise à laquelle appartient ce cinéma
     */
    public Cinema(int idCinema, String denomination, String adresse, String ville, int idFranchise) {
        this.idCinema = idCinema;
        this.denomination = denomination;
        this.adresse = adresse;
        this.ville = ville;
        this.idFranchise = idFranchise;
    }

    /** @return L'identifiant unique du cinéma */
    public int getIdCinema() {
        return idCinema;
    }

    /** @return Le nom commercial du cinéma */
    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    /** @return L'adresse du cinéma */
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /** @return La ville du cinéma */
    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    /** @return L'identifiant de la franchise associée */
    public int getIdFranchise() {
        return idFranchise;
    }

    public void setIdFranchise(int idFranchise) {
        this.idFranchise = idFranchise;
    }

    @Override
    public String toString() {
        return denomination;
    }
}