package org.example;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.ball.Ball;
import org.example.brick.NormalBrick;

import java.util.List;

/**
 * Main controller class for the Arkanoid-style game.
 *
 * This class manages the entire game lifecycle:
 * - Initializes game objects (paddle, ball, bricks)
 * - Handles keyboard input
 * - Runs the update and render loop using AnimationTimer
 * - Detects collisions and manages game over/restart
 */
public class Game {
    // Window dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Core game objects
    private Paddle paddle;
    private Ball ball;
    private List<NormalBrick> normalBricks;

    // Input state tracking
    private boolean leftPressed, rightPressed;

    // Game state flag
    private boolean gameOver = false;

    /**
     * Initializes the JavaFX scene, sets up the game, and starts the main loop.
     *
     * @param stage the JavaFX stage (main window) to render the game
     */
    public void start(Stage stage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Initialize core game elements
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 120, 15);
        ball = new Ball(WIDTH / 2, HEIGHT / 2, 10, 3, 3);
        normalBricks = Level.createLevel1();

        // Create the scene and bind controls
        Scene scene = new Scene(root);
        setupControls(scene);

        // Ensure canvas receives focus for keyboard input
        canvas.setFocusTraversable(true);
        canvas.setOnMouseClicked(e -> canvas.requestFocus());
        canvas.requestFocus();

        // Configure stage (window)
        stage.setTitle("Arkanoid JavaFX OOP");
        stage.setScene(scene);
        stage.show();

        // Main game loop using AnimationTimer
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();    // Update object positions and check collisions
                render(gc);  // Draw updated frame
            }
        }.start();
    }

    /**
     * Configures keyboard input.
     * - LEFT and RIGHT move the paddle
     * - R restarts the game after game over
     *
     * @param scene the JavaFX Scene used to register input events
     */
    private void setupControls(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = true;

            // Restart the game when R is pressed after game over
            if (e.getCode() == KeyCode.R && gameOver) {
                restart();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = false;
        });
    }

    /**
     * Updates all game objects each frame:
     * - Moves the paddle and ball
     * - Checks collisions between the ball, paddle, bricks, and walls
     * - Updates the game state (win/lose)
     */
    private void update() {
        if (gameOver) return;

        // Move paddle based on user input
        paddle.update(leftPressed, rightPressed);

        // Update ball position
        ball.update();

        // Collision detection handled through CollisionManager
        CollisionManager.handleBallWallCollision(ball, WIDTH, HEIGHT);
        CollisionManager.handleBallPaddleCollision(ball, paddle);
        CollisionManager.handleBallBrickCollision(ball, normalBricks);

        // Game over if ball falls below the screen
        if (ball.getY() > HEIGHT) {
            gameOver = true;
        }

        // Game won if all bricks are destroyed
        if (normalBricks.stream().allMatch(NormalBrick::isDestroyed)) {
            gameOver = true;
        }
    }

    /**
     * Renders the current frame to the screen.
     * Clears the canvas, draws all game objects, and displays "Game Over" text if needed.
     *
     * @param gc the GraphicsContext used for drawing on the Canvas
     */
    private void render(GraphicsContext gc) {
        // Clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw game objects
        paddle.draw(gc);
        ball.draw(gc);
        for (NormalBrick b : normalBricks) {
            b.draw(gc);
        }

        // Display Game Over text
        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.fillText("Game Over! Press R to Restart", WIDTH / 2 - 100, HEIGHT / 2);
        }
    }

    /**
     * Resets the game state and recreates paddle, ball, and bricks.
     * Called when the player presses R after game over.
     */
    private void restart() {
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 120, 15);
        ball = new Ball(WIDTH / 2, HEIGHT / 2, 10, 3, 3);
        normalBricks = Level.createLevel1();
        gameOver = false;
    }
}
