package cinema;

import cinema.BO.Utilisateur;

/**
 * Classe utilitaire représentant la session de l'utilisateur connecté.
 * Stocke l'objet Utilisateur de façon statique pour le rendre accessible
 * depuis n'importe quelle classe sans avoir à le passer en paramètre partout.
 *
 * Pattern "Session globale" : une seule instance partagée dans toute l'application.
 * Utilisé notamment par LogDAO pour associer chaque action à l'utilisateur connecté.
 */
public class Session {

    /**
     * Référence statique à l'utilisateur actuellement connecté.
     * null si personne n'est connecté.
     */
    private static Utilisateur utilisateurConnecte;

    /**
     * Enregistre l'utilisateur connecté en session.
     * Appelé dans ConnexionController après une authentification réussie.
     * @param utilisateur L'utilisateur authentifié
     */
    public static void setUtilisateur(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    /**
     * Retourne l'utilisateur actuellement connecté.
     * Utilisé par LogDAO pour récupérer l'id de l'utilisateur à insérer dans les logs.
     * @return L'utilisateur connecté, ou null si aucune session active
     */
    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    /**
     * Efface la session (déconnexion).
     * À appeler lors d'un logout pour s'assurer que l'utilisateur précédent
     * n'est plus accessible dans l'application.
     */
    public static void clear() {
        utilisateurConnecte = null;
    }
}