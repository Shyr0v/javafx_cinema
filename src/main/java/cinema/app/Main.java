package cinema.app;

import javafx.application.Application;

/**
 * Point d'entrée de l'application.
 * Cette classe existe uniquement pour contourner une limitation des modules Java (JPMS) :
 * dans certains environnements, la classe qui contient main() ne doit pas étendre Application.
 * On délègue donc le démarrage JavaFX à MainApplication via Application.launch().
 */
public class Main {

    /**
     * Méthode principale appelée par la JVM au démarrage.
     * Application.launch() initialise le runtime JavaFX puis appelle MainApplication.start().
     * @param args Arguments de la ligne de commande (non utilisés ici)
     */
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }
}