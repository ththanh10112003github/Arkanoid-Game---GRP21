package org.example.core;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.Game;
import org.example.MainMenu;

/**
 * Main entry point of the game.
 * This class initializes the JavaFX environment and launches the Game instance.
 */
public class Main extends Application {

    /**
     * Called when JavaFX starts.
     * Initializes and starts the Game on the provided Stage.
     */
    @Override
    public void start(Stage stage) {
        MainMenu.show(stage);
    }

    /**
     * Launches JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
