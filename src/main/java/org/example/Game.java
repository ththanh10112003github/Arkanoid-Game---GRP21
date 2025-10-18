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
import org.example.powerup.BiggerPaddle;
import org.example.powerup.BreakerBall;
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
    private double fastBallSpawnRate = 0.5;
    private double biggerPaddleDurationRemaining = 0.0;
    private double fastBallDurationRemaining = 0.0;
    private boolean isPaused = false;
    private double breakerBallDurationRemaining = 0.0;



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
                if (isPaused) {
                    drawPauseOverlay(gc);
                    return; // Skip update logic
                }

                update();
                render(gc);
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

            // Return to Main Menu when ESC is pressed
            if (e.getCode() == KeyCode.ESCAPE) {
                MainMenu.show((Stage) canvas.getScene().getWindow());
            }

            // Restart the game when R is pressed after game over
            if (e.getCode() == KeyCode.R && gameOver) {
                restart();
            }

            // Pause(UnPause) the game when P is pressed
            if (e.getCode() == KeyCode.P) {
                isPaused = !isPaused;
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

            // Return to Main Menu when ESC is pressed
            if (e.getCode() == KeyCode.ESCAPE) {
                MainMenu.show((Stage) canvas.getScene().getWindow());
            }

            // Pause(UnPause) the game when P is pressed
            if (e.getCode() == KeyCode.P) {
                isPaused = !isPaused;
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
        // Drop power-up if a brick destroyed
        if (destroyed != null && Math.random() < powerUpDropRate) {
            double size = 18;
            double px = destroyed.x + destroyed.width / 2 - size / 2;
            double py = destroyed.y + destroyed.height / 2 - size / 2;

            double rand = Math.random();
            if (rand < 0.33) {
                powerUps.add(new FastBall(px, py, size));
            } else if (rand < 0.66) {
                powerUps.add(new BiggerPaddle(px, py, size));
            } else {
                powerUps.add(new BreakerBall(px, py, size));
            }
        }

        // Check collision between paddle and power-ups
        for (PowerUp p : powerUps) {
            if (!p.isCollected() && CollisionManager.isColliding(p, paddle)) {
                String powerUpType = p.getId();

                if ("BiggerPaddle".equals(powerUpType)) {
                    if (biggerPaddleDurationRemaining <= 0) {
                        p.applyToPaddle(paddle);
                    }
                    biggerPaddleDurationRemaining = p.getDuration();
                    p.setCollected();

                } else if ("FastBall".equals(powerUpType)) {
                    if (fastBallDurationRemaining <= 0) {
                        p.apply(ball);
                    }
                    fastBallDurationRemaining = p.getDuration();
                    p.setCollected();

                } else if ("breakerBall".equals(powerUpType)) {
                    if (breakerBallDurationRemaining <= 0) {
                        p.apply(ball);
                    }
                    breakerBallDurationRemaining = p.getDuration();
                    p.setCollected();
                }

                break;
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

        // Handle active power-up timers (60fps)
        // Update BiggerPaddle timer
        if (biggerPaddleDurationRemaining > 0) {
            biggerPaddleDurationRemaining -= 1.0 / 60.0;
            if (biggerPaddleDurationRemaining <= 0) {
                // Reset paddle to normal size
                new BiggerPaddle(0, 0, 0).reset(ball, paddle);
                biggerPaddleDurationRemaining = 0.0;
            }
        }
        
        // Update FastBall timer
        if (fastBallDurationRemaining > 0) {
            fastBallDurationRemaining -= 1.0 / 60.0;
            if (fastBallDurationRemaining <= 0) {
                // Reset ball to normal speed
                new FastBall(0, 0, 0).reset(ball, paddle);
                fastBallDurationRemaining = 0.0;
            }
        }

        // Update BreakerBall timer
        if (breakerBallDurationRemaining > 0) {
            breakerBallDurationRemaining -= 1.0 / 60.0;
            if (breakerBallDurationRemaining <= 0) {
                new BreakerBall(0, 0, 0).reset(ball, paddle);
                breakerBallDurationRemaining = 0.0;
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
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 28));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.fillText("GAME OVER", WIDTH / 2, HEIGHT / 2 - 10);

            gc.setFont(javafx.scene.text.Font.font("Arial", 18));
            gc.fillText("Press R to Restart", WIDTH / 2, HEIGHT / 2 + 25);
            gc.fillText("Press ESC to Return to Main Menu", WIDTH / 2, HEIGHT / 2 + 55);
        }
    }

    /**
     * Draw an overlay when the game is pause
     * Called when the player presses P .
     */
    private void drawPauseOverlay(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5)); // translucent dark overlay
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 32));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText("PAUSED", canvas.getWidth() / 2, canvas.getHeight() / 2 - 20);

        gc.setFont(javafx.scene.text.Font.font("Arial", 18));
        gc.fillText("Press P to Resume", canvas.getWidth() / 2, canvas.getHeight() / 2 + 20);
        gc.fillText("Press ESC to Return to Main Menu", canvas.getWidth() / 2, canvas.getHeight() / 2 + 50);
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
        biggerPaddleDurationRemaining = 0.0;
        fastBallDurationRemaining = 0.0;
        gameOver = false;
        leftPressed = false;
        rightPressed = false;
        if (canvas != null) {
            canvas.requestFocus();
        }
    }
}
