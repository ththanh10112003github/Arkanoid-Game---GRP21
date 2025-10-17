package org.example;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.ball.Ball;
import org.example.brick.Brick;
import org.example.brick.UnbreakableBrick;
import org.example.powerup.BiggerPaddle;
import org.example.powerup.FastBall;
import org.example.powerup.TripleBallPowerUp;
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
    private List<Ball> balls;
    private List<Brick> bricks;
    private Canvas canvas;
    private List<PowerUp> powerUps = new ArrayList<>();
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    private double powerUpDropRate = 1.0;
    private double fastBallSpawnRate = 0.4;
    private double tripleBallSpawnRate = 0.3;
    private double biggerPaddleDurationRemaining = 0.0;
    private double fastBallDurationRemaining = 0.0;
    private boolean leftPressed, rightPressed;


    // Game state
    private boolean gameOver = false;

    Image heartImage;
    Image heartEmptyImage;
    /**
     * Initializes the JavaFX scene, sets up the game, and starts the game loop.
     */
    public void start(Stage stage) {
        Pane root = new Pane();
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Load heart images
        try {
            heartImage = new Image("file:assets/Heart.png");
            heartEmptyImage = new Image("file:assets/Heart_empty.png");
        } catch (Exception e) {
            System.err.println("Could not load heart images: " + e.getMessage());
            heartImage = null;
            heartEmptyImage = null;
        }

        // Initialize core game elements
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 500, 15);
        balls = new ArrayList<>();
        balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.5, 1.5));
        bricks = Level.createLevel1();
        scoreManager = new ScoreManager();
        soundManager = SoundManager.oneAndOnly();

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

        soundManager.playBackgroundMusic("main_theme");

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
     * - Updates the power-ups
     * - Checks collisions between the ball, paddle, bricks, power-ups, and walls
     * - Updates the game state (win/lose)
     * - Handles sound effects and background musics
     */
    private void update() {
        if (gameOver) return;

        paddle.update(leftPressed, rightPressed);

        for (Ball ball : balls) {
            ball.update();
        }

        for (PowerUp p : powerUps) {
            p.update();
        }
        

        // Check collision for balls with walls and paddle
        for (Ball ball : balls) {
            CollisionManager.handleBallWallCollision(ball, WIDTH, HEIGHT);
            CollisionManager.handleBallPaddleCollision(ball, paddle);
        }

        // Check collision between balls and bricks
        Brick destroyed = null;
        for (Brick b : bricks) {
            if (!b.isDestroyed()) {
                for (Ball ball : balls) {
                    CollisionManager.handleBallBrickCollision(ball, b);
                    if (b.isDestroyed()) {
                        destroyed = b;
                        scoreManager.addScore(100);
                        soundManager.playSoundEffect("brick_break");
                        break;
                    }
                }
                if (destroyed != null) break;
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
                        // If not active yet, applys effect
                        ((BiggerPaddle)p).applyToPaddle(paddle);
                        soundManager.playSoundEffect(p.getSoundEffect());
                    }
                    // Reset duration
                    biggerPaddleDurationRemaining = p.getDuration();
                    p.setCollected();
                } else if ("FastBall".equals(powerUpType)) {
                    if (fastBallDurationRemaining <= 0) {
                         // If not active yet, applys effect
                        for (Ball ball : balls) {
                            ((FastBall)p).applyToBall(ball);
                        }
                        soundManager.playSoundEffect(p.getSoundEffect());
                    }
                    // Reset duration
                    fastBallDurationRemaining = p.getDuration();
                    p.setCollected();
                } else if ("TripleBall".equals(powerUpType)) {
                    // Apply triple ball effect
                    ((TripleBallPowerUp)p).apply(balls);
                    p.setCollected();
                    soundManager.playSoundEffect(p.getSoundEffect());
                }
                
                break;
            }
        }

        powerUps.removeIf(PowerUp::isCollected);

        // Handle balls falling below screen - if no balls, lose life, if no more lives, game over
        balls.removeIf(ball -> ball.getY() > HEIGHT);
        if (balls.isEmpty() && !gameOver) {
            if (scoreManager.loseLife()) {
                soundManager.playSoundEffect("life_lost");
                balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.5, 1.5));
            } else {
                gameOver = true;
                soundManager.pauseBackgroundMusic();
                soundManager.stopAllSoundEffects();
                soundManager.playSoundEffect("game_over");
            }
        }

        // Game won if all bricks destroyed except unbreakable bricks
        if (bricks.stream().filter(b -> !(b instanceof UnbreakableBrick)).allMatch(Brick::isDestroyed)) {
            gameOver = true;
            soundManager.pauseBackgroundMusic();
            soundManager.stopAllSoundEffects();
            soundManager.playSoundEffect("victory");
        }

        // Update BiggerPaddle duration
        if (biggerPaddleDurationRemaining > 0) {
            biggerPaddleDurationRemaining -= 1.0 / 60.0;
            if (biggerPaddleDurationRemaining <= 0) {
                BiggerPaddle biggerPaddle = new BiggerPaddle(0, 0, 0);
                biggerPaddle.reset(balls.get(0), paddle);
                biggerPaddleDurationRemaining = 0.0;
            }
        }
        
        // Update FastBall duration
        if (fastBallDurationRemaining > 0) {
            fastBallDurationRemaining -= 1.0 / 60.0;
            if (fastBallDurationRemaining <= 0) {
                FastBall fastBall = new FastBall(0, 0, 0);
                fastBall.reset(balls.get(0), paddle);
                fastBallDurationRemaining = 0.0;
            }
        }
    }

    /**
     * Renders the current frame to the screen.
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

        // Display score and lives
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Consolas", 16));
        gc.fillText("Score: " + scoreManager.getScoreString(), 30, 30);
        scoreManager.drawLives(gc, heartImage, heartEmptyImage, WIDTH / 2 - 50, 35);
        gc.fillText("High Score: " + scoreManager.getHighScoreString(), WIDTH - 180, 30);

        // Display Game Over
        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Consolas", 24));

            if (bricks.stream().allMatch(Brick::isDestroyed)) {
                gc.fillText("YOU WIN! Press R to Restart", WIDTH / 2 - 170, HEIGHT / 2);
            } else {
                gc.fillText("Game Over! Press R to Restart", WIDTH / 2 - 180, HEIGHT / 2);
            }
            
            gc.setFont(javafx.scene.text.Font.font("Consolas", 16));
            gc.fillText("Final Score: " + scoreManager.getScoreString(), WIDTH / 2 - 80, HEIGHT / 2 + 30);
            gc.fillText("High Score:  " + scoreManager.getHighScoreString(), WIDTH / 2 - 80, HEIGHT / 2 + 60);
        }
    }

    /**
     * Resets the game state and recreates paddle, ball, and bricks.
     * Called when the player presses R after game over.
     */
    private void restart() {
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 500, 15);
        balls = new ArrayList<>();
        balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.5, 1.5));
        bricks = Level.createLevel1();
        scoreManager.reset();
        powerUps.clear();
        biggerPaddleDurationRemaining = 0.0;
        fastBallDurationRemaining = 0.0;
        soundManager.resumeBackgroundMusic();
        gameOver = false;
        leftPressed = false;
        rightPressed = false;
    
        if (canvas != null) {
            canvas.requestFocus();
        }
    }
}
