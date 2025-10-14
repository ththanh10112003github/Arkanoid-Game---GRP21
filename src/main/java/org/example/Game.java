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
import org.example.powerup.FastBall;
import org.example.powerup.PowerUp;

import java.util.List;
import java.util.ArrayList;

/**
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
    private Canvas canvas;
    private List<PowerUp> powerUps = new ArrayList<>();
    private double powerUpDropRate = 1.0;
    private String activePowerUpId = null;
    private double activePowerUpRemainingSec = 0.0;

    // Input tracking
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
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Initialize core game elements
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 120, 15);
        ball = new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.2, 1.2);
        normalBricks = Level.createLevel1();

        // Create the scene and bind controls
        Scene scene = new Scene(root);
        setupControls(scene, canvas);

        // Ensure canvas receives focus for keyboard input
        canvas.setFocusTraversable(true);
        canvas.setOnMouseClicked(e -> canvas.requestFocus());
        canvas.requestFocus();

        // Configure window
        stage.setTitle("Arkanoid Game");
        stage.setScene(scene);
        stage.show();

        // Ensure focus goes to our input target when the window gains focus
        root.requestFocus();

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
     * - LEFT/RIGHT or A/D move the paddle
     * - R restarts the game after game over
     *
     * @param scene the JavaFX Scene used to register input events
     */
    private void setupControls(Scene scene, Canvas canvas) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = true;

            // Restart the game when R is pressed after game over
            if (e.getCode() == KeyCode.R && gameOver) {
                restart();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = false;
        });

        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = true;
            if (e.getCode() == KeyCode.R && gameOver) {
                restart();
            }
        });

        canvas.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = false;
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

        // Update power-ups falling and handle pickup (Paddle collects)
        for (PowerUp p : powerUps) {
            p.update();
        }
        

        // Check collision between ball and walls
        CollisionManager.handleBallWallCollision(ball, WIDTH, HEIGHT);
        
        // Check collision between ball and paddle
        CollisionManager.handleBallPaddleCollision(ball, paddle);

        // Check collision between ball and bricks
        NormalBrick destroyed = null;
        for (NormalBrick b : normalBricks) {
            if (!b.isDestroyed()) {
                CollisionManager.handleBallBrickCollision(ball, b);
                // Check if brick was destroyed by collision
                if (b.isDestroyed()) {
                    destroyed = b;
                    break;
                }
            }
        }
        // Drop power-up if a brick was destroyed
        if (destroyed != null && Math.random() < powerUpDropRate) {
            double size = 18;
            double px = destroyed.x + destroyed.width / 2 - size / 2;
            double py = destroyed.y + destroyed.height / 2 - size / 2;
            powerUps.add(new FastBall(px, py, size));
        }

        // Check collision between paddle and power-ups
        for (PowerUp p : powerUps) {
            if (!p.isCollected() && CollisionManager.isColliding(p, paddle)) {
                // Only apply effect if this power-up type is not already active
                if (activePowerUpId == null || !activePowerUpId.equals(p.getId())) {
                    p.apply(ball);
                    activePowerUpId = p.getId();
                    activePowerUpRemainingSec = p.getDurationSeconds();
                } else {
                    // Same power-up already active, just mark as collected without applying effect
                    p.setCollected();
                }
                break;  // Only collect one power-up per frame
            }
        }
        powerUps.removeIf(PowerUp::isCollected);

        // Game over if ball falls below the screen
        if (ball.getY() > HEIGHT) {
            gameOver = true;
        }

        // Game won if all bricks are destroyed
        if (normalBricks.stream().allMatch(NormalBrick::isDestroyed)) {
            gameOver = true;
        }

        // Handle active power-up timer (60fps; decrement with fixed timestep)
        if (activePowerUpId != null) {
            activePowerUpRemainingSec -= 1.0 / 60.0;
            if (activePowerUpRemainingSec <= 0) {
                // Revert by constructing a temporary instance to call revert logic
                if ("FastBall".equals(activePowerUpId)) {
                    new FastBall(0, 0, 0).revert(ball);
                }
                activePowerUpId = null;
                activePowerUpRemainingSec = 0.0;
            }
        }
    }

    /**
     * Renders the current frame to the screen.
     * Clears the canvas, draws all game objects, and displays "Game Over" text if needed.
     *
     * @param gc the GraphicsContext used for drawing on the Canvas
     */
    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw game objects
        paddle.draw(gc);
        ball.draw(gc);
        for (NormalBrick b : normalBricks) {
            b.draw(gc);
        }
        for (PowerUp p : powerUps) {
            p.draw(gc);
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
        ball = new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.2, 1.2);
        normalBricks = Level.createLevel1();
        powerUps.clear();
        activePowerUpId = null;
        activePowerUpRemainingSec = 0.0;
        gameOver = false;
        leftPressed = false;
        rightPressed = false;
        if (canvas != null) {
            canvas.requestFocus();
        }
    }
}
