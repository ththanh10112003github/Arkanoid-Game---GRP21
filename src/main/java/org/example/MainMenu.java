package org.example;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainMenu {

    private static boolean isFirstShow = true;

    public static void show(Stage stage) {
        Text title = new Text("ARKANOID");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        title.setFill(Color.ORANGE);

        Button playButton = new Button("Play");
        Button settingsButton = new Button("Settings");
        Button instructionsButton = new Button("Instructions");
        Button quitButton = new Button("Quit");

        // Basic button styling
        playButton.setFont(Font.font(20));
        settingsButton.setFont(Font.font(20));
        instructionsButton.setFont(Font.font(20));
        quitButton.setFont(Font.font(20));

        playButton.setMinWidth(200);
        settingsButton.setMinWidth(200);
        instructionsButton.setMinWidth(200);
        quitButton.setMinWidth(200);

        // Create layout
        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: black; -fx-alignment: center;");
        layout.getChildren().addAll(title, playButton, settingsButton, instructionsButton, quitButton);

        Scene menuScene = new Scene(layout, 800, 600);

        // Button actions
        playButton.setOnAction(e -> {
            Game game = new Game(GameSettings.getDifficulty());
            game.start(stage);
        });

        settingsButton.setOnAction(e -> {
            showSettings(stage);
        });

        instructionsButton.setOnAction(e -> {
            showInstructions(stage);
        });

        quitButton.setOnAction(e -> {
            stage.close();
        });

        stage.setScene(menuScene);
        stage.setTitle("Arkanoid - Main Menu");
        
        // Only play menu music if this is the first show or coming back from game
        if (isFirstShow || SoundManager.oneAndOnly().getCurrentBackgroundMusic() == null) {
            SoundManager.oneAndOnly().playBackgroundMusic("menu_theme");
            isFirstShow = false;
        }

        stage.show();
    }

    private static void showSettings(Stage stage) {
        Text title = new Text("Settings");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        title.setFill(Color.ORANGE);

        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");
        Button backButton = new Button("Back to Menu");

        easyButton.setFont(Font.font(20));
        mediumButton.setFont(Font.font(20));
        hardButton.setFont(Font.font(20));
        backButton.setFont(Font.font(18));

        easyButton.setMinWidth(200);
        mediumButton.setMinWidth(200);
        hardButton.setMinWidth(200);
        backButton.setMinWidth(200);

        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: black; -fx-alignment: center;");
        layout.getChildren().addAll(title, easyButton, mediumButton, hardButton, backButton);

        Scene settingsScene = new Scene(layout, 800, 600);

        easyButton.setOnAction(e -> GameSettings.setDifficulty(Difficulty.EASY));
        mediumButton.setOnAction(e -> GameSettings.setDifficulty(Difficulty.MEDIUM));
        hardButton.setOnAction(e -> GameSettings.setDifficulty(Difficulty.HARD));
        backButton.setOnAction(e -> show(stage));

        stage.setScene(settingsScene);
    }

    private static void showInstructions(Stage stage) {
        Text text = new Text("""
                Controls:
                - Move: ← / → or A / D
                - Pause: P
                - Restart: R
                - Return to Menu: ESC
                
                Destroy all bricks and survive!
                """);
        text.setFont(Font.font("Arial", 20));
        text.setFill(Color.WHITE);

        Button backButton = new Button("Back to Menu");
        backButton.setFont(Font.font(18));

        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: black; -fx-alignment: center;");
        layout.getChildren().addAll(text, backButton);

        Scene instructionsScene = new Scene(layout, 800, 600);

        backButton.setOnAction(e -> show(stage));

        stage.setScene(instructionsScene);
    }
}
