package org.example.core;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.Game;

/**
 * Main entry point of the game.
 * This class initializes the JavaFX environment and launches the Game instance.
 * It extends the JavaFX Application class, as required for all JavaFX programs.
 */
public class Main extends Application {

    /**
     * Called automatically when the JavaFX application starts.
     * Initializes and starts the Game on the provided Stage.
     * @param stage the primary window of the application
     */
    @Override
    public void start(Stage stage) {
        Game game = new Game();
        game.start(stage);
    }

    /**
     * Launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
