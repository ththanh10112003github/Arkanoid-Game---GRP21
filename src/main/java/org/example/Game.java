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
import org.example.brick.Brick;
import org.example.powerup.BiggerPaddle;
import org.example.powerup.FastBall;
import org.example.powerup.PowerUp;
import org.example.powerup.TripleBallPowerUp;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class manages the entire game lifecycle:
 * - Initializes game objects
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
    private List<Ball> balls = new ArrayList<>();
    private List<Brick> bricks;
    private Canvas canvas;
    private List<PowerUp> powerUps = new ArrayList<>();
    private double powerUpDropRate = 1.0;
    private double fastBallSpawnRate = 0.33;
    private double tripleBallSpawnRate = 0.33;
    private double biggerPaddleDurationRemaining = 0.0;
    private double fastBallDurationRemaining = 0.0;

    // Input tracking
    private boolean leftPressed, rightPressed;

    // Game state
    private boolean gameOver = false;
    private boolean gameWon = false;
    /**
     * Initializes the JavaFX scene, sets up the game, and starts the game loop.
     */
    public void start(Stage stage) {
        Pane root = new Pane();
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Initialize core game elements
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 120, 15);
        balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.2, 1.2));
        bricks = Level.createLevel1();

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

        // Ensure focus goes to input target when the window gains focus
        root.requestFocus();

        // Main game loop using AnimationTimer
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(); 
                render(gc);
            }
        }.start();
    }

    /**
     * Configures keyboard input.
     * - LEFT/RIGHT or A/D move the paddle
     * - R restarts the game after game over
     */
    private void setupControls(Scene scene, Canvas canvas) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = true;

            if (e.getCode() == KeyCode.R && (gameOver || gameWon)) {
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
            if (e.getCode() == KeyCode.R && (gameOver || gameWon)) {
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
     * - Updates the game state (if its game over or notnot)
     */
    private void update() {
        if (gameOver || gameWon) return;

        // Move paddle based on user input
        paddle.update(leftPressed, rightPressed);

        // Update ball position
        for (Ball ball : balls) {
            ball.update();
        }

        // Update power-ups falling and handle pickup (Paddle collects)
        for (PowerUp p : powerUps) {
            p.update();
        }

        // Check collision between ball and walls
        for (Ball ball : balls) {
            CollisionManager.handleBallWallCollision(ball, WIDTH, HEIGHT);
        }

        // Check collision between ball and paddle
        for (Ball ball : balls) {
            CollisionManager.handleBallPaddleCollision(ball, paddle);
        }

        // Check collision between ball and bricks
        Brick destroyed = null;
        for (Ball ball : balls) {
            for (Brick b : bricks) {
                if (!b.isDestroyed()) {
                    CollisionManager.handleBallBrickCollision(ball, b);
                    // Check if brick was destroyed by collision
                    if (b.isDestroyed()) {
                        destroyed = b;
                        break;
                    }
                }
            }
            if (destroyed != null) {
                break;
            }
        }

        // Drop power-up if a brick destroyed
        if (destroyed != null && Math.random() < powerUpDropRate) {
            double size = 18;
            double px = destroyed.x + destroyed.width / 2 - size / 2;
            double py = destroyed.y + destroyed.height / 2 - size / 2;

            // Spawn power-up based on spawn rates
            double rand = Math.random();
            if (rand < fastBallSpawnRate) {
                powerUps.add(new FastBall(px, py, size));
            } else if (rand < fastBallSpawnRate + tripleBallSpawnRate) {
                powerUps.add(new TripleBallPowerUp(px, py, size));
            } else {
                powerUps.add(new BiggerPaddle(px, py, size));
            }
        }

        // Check collision between paddle and power-ups
        for (PowerUp p : powerUps) {
            if (!p.isCollected() && CollisionManager.isColliding(p, paddle)) {
                String powerUpType = p.getId();

                if ("BiggerPaddle".equals(powerUpType)) {
                    if (biggerPaddleDurationRemaining <= 0) {
                        // If BiggerPaddle not active yet, apply effect
                        ((BiggerPaddle) p).applyToPaddle(paddle);
                    }
                    // Reset BiggerPaddle duration
                    biggerPaddleDurationRemaining = p.getDuration();
                    p.setCollected();

                } else if ("FastBall".equals(powerUpType)) {
                    if (fastBallDurationRemaining <= 0) {
                        // If FastBall not active yet, apply effect
                        for (Ball ball : balls) {
                            ((FastBall) p).applyToBall(ball);
                        }
                    }
                    // Reset FastBall duration
                    fastBallDurationRemaining = p.getDuration();
                    p.setCollected();
                } else if ("TripleBall".equals(powerUpType)) {
                    ((TripleBallPowerUp) p).apply(balls);
                    p.setCollected();
                }

                break;
            }
        }
        powerUps.removeIf(PowerUp::isCollected);

        // Game over if ball falls below the screen
        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            if (ball.getY() > HEIGHT) {
                ballIterator.remove();
            }
        }

        if (balls.isEmpty()) {
            gameOver = true;
        }

        // Game won if all bricks are destroyed
        if (bricks.stream().allMatch(Brick::isDestroyed)) {
            gameWon = true;
        }

        // Handle active power-up timers (60fps)
        // Update BiggerPaddle timer
        if (biggerPaddleDurationRemaining > 0) {
            biggerPaddleDurationRemaining -= 1.0 / 60.0;
            if (biggerPaddleDurationRemaining <= 0) {
                // Reset paddle to normal size
                new BiggerPaddle(0, 0, 0).reset(null, paddle);
                biggerPaddleDurationRemaining = 0.0;
            }
        }

        // Update FastBall timer
        if (fastBallDurationRemaining > 0) {
            fastBallDurationRemaining -= 1.0 / 60.0;
            if (fastBallDurationRemaining <= 0) {
                // Reset ball to normal speed
                for (Ball ball : balls) {
                    new FastBall(0, 0, 0).reset(ball, paddle);
                }
                fastBallDurationRemaining = 0.0;
            }
        }
    }

    /**
     * Renders the current frame to the screen.
     * Clears the canvas, draws all game objects, and displays "Game Over" text if needed.
     */
    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw game objects
        paddle.draw(gc);
        for (Ball ball : balls) {
            ball.draw(gc);
        }
        for (Brick b : bricks) {
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
        
        // Display Game Won text
        if (gameWon) {
            gc.setFill(Color.WHITE);
            gc.fillText("YOU WIN! Press R to Restart", WIDTH / 2 - 100, HEIGHT / 2);
        }
    }

    /**
     * Resets the game state and recreates paddle, ball, and bricks.
     * Called when the player presses R after game over or game won.
     */
    private void restart() {
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 120, 15);
        balls.clear();
        balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.2, 1.2));
        bricks = Level.createLevel1();
        powerUps.clear();
        biggerPaddleDurationRemaining = 0.0;
        fastBallDurationRemaining = 0.0;
        gameOver = false;
        gameWon = false;
        leftPressed = false;
        rightPressed = false;
        if (canvas != null) {
            canvas.requestFocus();
        }
    }
}
