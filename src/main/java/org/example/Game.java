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
import org.example.powerup.BreakerBall;
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
    private double powerUpDropRate = 0.4;
    private double fastBallSpawnRate = 0.4;
    private double tripleBallSpawnRate = 0.3;
    private double biggerPaddleDurationRemaining = 0.0;
    private double fastBallDurationRemaining = 0.0;
    private boolean isPaused = false;
    private double breakerBallDurationRemaining = 0.0;
    private AnimationTimer gameLoop;

    // Input tracking
    private boolean leftPressed, rightPressed;


    private enum GameState {
        PLAYING,
        LEVEL_TRANSITION,
        GAME_OVER,
        GAME_WON
    }

    private GameState gameState = GameState.PLAYING;
    private int currentLevel = 1;

    Image heartImage;
    Image heartEmptyImage;
    private final Difficulty difficulty;

    public Game(Difficulty difficulty) {
        this.difficulty = difficulty;
    }


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
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 40, 120, 15);
        paddle.scaleWidth(difficulty.getPaddleWidthMultiplier());
        balls = new ArrayList<>();
        balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.5 * difficulty.getBallSpeedMultiplier(), 1.5 * difficulty.getBallSpeedMultiplier()));
        bricks = Level.loadLevel(difficulty, currentLevel);
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
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPaused) {
                    drawPauseOverlay(gc);
                    return;
                }

                update();
                render(gc);
            }
        };
        gameLoop.start();
    }

    /**
     * Configures keyboard input.
     * - LEFT/RIGHT or A/D move the paddle
     * - R restarts the game after game over
     */
    private void setupControls(Scene scene, Canvas canvas) {
        scene.setOnKeyPressed(e -> {
            if (gameState == GameState.PLAYING) {
                if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = true;
                if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = true;
            } else if (gameState == GameState.LEVEL_TRANSITION) {
                currentLevel++;
                bricks = Level.loadLevel(difficulty, currentLevel);
                balls.clear();
                balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.5 * difficulty.getBallSpeedMultiplier(), 1.5 * difficulty.getBallSpeedMultiplier()));
                paddle.resetWidth();
                paddle.scaleWidth(difficulty.getPaddleWidthMultiplier());
                gameState = GameState.PLAYING;
                soundManager.playBackgroundMusic("main_theme");
            }

            // Return to Main Menu when ESC is pressed
            if (e.getCode() == KeyCode.ESCAPE) {
                stopGame();
                soundManager.oneAndOnly().stopAllSounds();
                MainMenu.show((Stage) canvas.getScene().getWindow());
            }

            if (e.getCode() == KeyCode.R && gameState == GameState.GAME_OVER) {
                restart();
            }

            // Pause(UnPause) the game when P is pressed
            if (e.getCode() == KeyCode.P) {
                if (isPaused) {
                    // Resume game
                    isPaused = false;
                    soundManager.resumeBackgroundMusic();
                } else {
                    // Pause game
                    isPaused = true;
                    soundManager.pauseBackgroundMusic();
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = false;
        });

        canvas.setOnKeyPressed(e -> {
            if (gameState == GameState.PLAYING) {
                if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) leftPressed = true;
                if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) rightPressed = true;
            } else if (gameState == GameState.LEVEL_TRANSITION) {
                currentLevel++;
                bricks = Level.loadLevel(difficulty, currentLevel);
                balls.clear();
                balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.5 * difficulty.getBallSpeedMultiplier(), 1.5 * difficulty.getBallSpeedMultiplier()));
                paddle.resetWidth();
                paddle.scaleWidth(difficulty.getPaddleWidthMultiplier());
                gameState = GameState.PLAYING;
                soundManager.playBackgroundMusic("main_theme");
            }
            if (e.getCode() == KeyCode.R && gameState == GameState.GAME_OVER) {
                restart();
            }

            // Return to Main Menu when ESC is pressed
            if (e.getCode() == KeyCode.ESCAPE) {
                stopGame();
                soundManager.oneAndOnly().stopAllSounds();
                MainMenu.show((Stage) canvas.getScene().getWindow());
            }

            // Pause(UnPause) the game when P is pressed
            if (e.getCode() == KeyCode.P) {
                if (isPaused) {
                    // Resume game
                    isPaused = false;
                    soundManager.resumeBackgroundMusic();
                } else {
                    // Pause game
                    isPaused = true;
                    soundManager.pauseBackgroundMusic();
                }
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
        if (gameState != GameState.PLAYING) return;

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
        // Drop power-up if a brick is destroyed
        if (destroyed != null && Math.random() < powerUpDropRate) {
            double size = 18;
            double px = destroyed.x + destroyed.width / 2 - size / 2;
            double py = destroyed.y + destroyed.height / 2 - size / 2;

            double rand = Math.random();
            if (rand < 0.25) {
                powerUps.add(new FastBall(px, py, size));
            } else if (rand < 0.5) {
                powerUps.add(new TripleBallPowerUp(px, py, size));
            } else if (rand < 0.75) {
                powerUps.add(new BiggerPaddle(px, py, size));
            } else {
                powerUps.add(new BreakerBall(px, py, size));
            }
        }

        // Check collision between paddle and power-ups
        for (PowerUp p : powerUps) {
            if (!p.isCollected() && CollisionManager.isColliding(p, paddle)) {
                String powerUpType = p.getId();

                switch (powerUpType) {
                    case "BiggerPaddle":
                        if (biggerPaddleDurationRemaining <= 0) {
                            ((BiggerPaddle) p).applyToPaddle(paddle);
                            soundManager.playSoundEffect(p.getSoundEffect());
                        }
                        biggerPaddleDurationRemaining = p.getDuration();
                        p.setCollected();
                        break;

                    case "FastBall":
                        if (fastBallDurationRemaining <= 0) {
                            for (Ball ball : balls) {
                                ((FastBall) p).applyToBall(ball);
                            }
                            soundManager.playSoundEffect(p.getSoundEffect());
                        }
                        fastBallDurationRemaining = p.getDuration();
                        p.setCollected();
                        break;

                    case "TripleBall":
                        ((TripleBallPowerUp) p).apply(balls);
                        soundManager.playSoundEffect(p.getSoundEffect());
                        p.setCollected();
                        break;

                    case "BreakerBall":
                        if (breakerBallDurationRemaining <= 0) {
                            for (Ball ball : balls) {
                                ((BreakerBall) p).applyToBall(ball);
                            }
                            soundManager.playSoundEffect(p.getSoundEffect());
                        }
                        breakerBallDurationRemaining = p.getDuration();
                        p.setCollected();
                        break;
                }

                break;
            }
        }

        // Remove collected power-ups
        powerUps.removeIf(PowerUp::isCollected);

        // Handle balls falling below screen
        balls.removeIf(ball -> ball.getY() > HEIGHT);
        if (balls.isEmpty() && gameState == GameState.PLAYING) {
            if (scoreManager.loseLife()) {
                soundManager.playSoundEffect("life_lost");
                balls.add(new Ball(WIDTH / 2, HEIGHT / 2, 10, 1.5, 1.5));
            } else {
                gameState = GameState.GAME_OVER;
                soundManager.oneAndOnly().stopAllSounds();
                soundManager.stopAllSoundEffects();
                soundManager.playSoundEffect("game_over");
            }
        }

        // Check win condition (all breakable bricks destroyed)
        if (bricks.stream().filter(b -> !(b instanceof UnbreakableBrick)).allMatch(Brick::isDestroyed)) {
            if (currentLevel == 3) {
                gameState = GameState.GAME_WON;
            } else {
                gameState = GameState.LEVEL_TRANSITION;
            }
            soundManager.pauseBackgroundMusic();
            soundManager.stopAllSoundEffects();
            soundManager.playSoundEffect("victory");
        }


        double delta = 1.0 / 60.0;

        // Bigger Paddle timer
        if (biggerPaddleDurationRemaining > 0) {
            biggerPaddleDurationRemaining -= delta;
            if (biggerPaddleDurationRemaining <= 0 && !balls.isEmpty()) {
                new BiggerPaddle(0, 0, 0).reset(balls.get(0), paddle);
                biggerPaddleDurationRemaining = 0.0;
            }
        }

        // Fast Ball timer
        if (fastBallDurationRemaining > 0) {
            fastBallDurationRemaining -= delta;
            if (fastBallDurationRemaining <= 0 && !balls.isEmpty()) {
                new FastBall(0, 0, 0).reset(balls.get(0), paddle);
                fastBallDurationRemaining = 0.0;
            }
        }

        // Breaker Ball timer
        if (breakerBallDurationRemaining > 0) {
            breakerBallDurationRemaining -= delta;
            if (breakerBallDurationRemaining <= 0 && !balls.isEmpty()) {
                new BreakerBall(0, 0, 0).reset(balls.get(0), paddle);
                breakerBallDurationRemaining = 0.0;
            }
        }
    }

    /**
     * Renders the current frame to the screen.
     */
    private void render(GraphicsContext gc) {
        // Reset any transform / state from previous frames or operations
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw game objects
        paddle.draw(gc);
        for (Ball ball : balls) ball.draw(gc);
        for (Brick b : bricks) b.draw(gc);
        for (PowerUp p : powerUps) p.draw(gc);

        // Display score and lives
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Consolas", 16));
        
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
        gc.setTextBaseline(javafx.geometry.VPos.TOP);

        gc.fillText("Score: " + scoreManager.getScoreString(), 30, 10);

        scoreManager.drawLives(gc, heartImage, heartEmptyImage, WIDTH / 2 - 50, 8);
        gc.fillText("High Score: " + scoreManager.getHighScoreString(), WIDTH - 180, 10);

        // Display Game Over / Win overlay
        if (gameState == GameState.GAME_OVER) {
            
            gc.setFill(Color.WHITE);
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.setTextBaseline(javafx.geometry.VPos.CENTER);

            gc.setFont(javafx.scene.text.Font.font("Consolas", 28));
            gc.fillText("GAME OVER", WIDTH / 2, HEIGHT / 2 - 20);

            gc.setFont(javafx.scene.text.Font.font("Consolas", 16));
            gc.fillText("Final Score: " + scoreManager.getScoreString(), WIDTH / 2, HEIGHT / 2 + 10);
            gc.fillText("High Score: " + scoreManager.getHighScoreString(), WIDTH / 2, HEIGHT / 2 + 30);
            gc.fillText("Press R to Restart", WIDTH / 2, HEIGHT / 2 + 60);
            gc.fillText("Press ESC to Return to Main Menu", WIDTH / 2, HEIGHT / 2 + 85);
        } else if (gameState == GameState.GAME_WON) {
            gc.setFill(Color.WHITE);
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.setTextBaseline(javafx.geometry.VPos.CENTER);

            gc.setFont(javafx.scene.text.Font.font("Consolas", 28));
            gc.fillText("YOU WIN!", WIDTH / 2, HEIGHT / 2 - 20);

            gc.setFont(javafx.scene.text.Font.font("Consolas", 16));
            gc.fillText("Final Score: " + scoreManager.getScoreString(), WIDTH / 2, HEIGHT / 2 + 10);
            gc.fillText("High Score: " + scoreManager.getHighScoreString(), WIDTH / 2, HEIGHT / 2 + 30);
            gc.fillText("Press ESC to Return to Main Menu", WIDTH / 2, HEIGHT / 2 + 60);
        } else if (gameState == GameState.LEVEL_TRANSITION) {
            gc.setFill(Color.WHITE);
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.setTextBaseline(javafx.geometry.VPos.CENTER);

            gc.setFont(javafx.scene.text.Font.font("Consolas", 28));
            gc.fillText("LEVEL " + currentLevel + " CLEARED", WIDTH / 2, HEIGHT / 2 - 20);

            gc.setFont(javafx.scene.text.Font.font("Consolas", 16));
            gc.fillText("Press any key to continue", WIDTH / 2, HEIGHT / 2 + 20);
        }
    }

    /**
     * Draws the overlay when the game is paused.
     * Called when the player presses P.
     */
    private void drawPauseOverlay(GraphicsContext gc) {
       
        gc.setTransform(1, 0, 0, 1, 0, 0);

        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 32));
        gc.fillText("PAUSED", canvas.getWidth() / 2, canvas.getHeight() / 2 - 20);

        gc.setFont(javafx.scene.text.Font.font("Arial", 18));
        gc.fillText("Press P to Resume", canvas.getWidth() / 2, canvas.getHeight() / 2 + 20);
        gc.fillText("Press ESC to Return to Main Menu", canvas.getWidth() / 2, canvas.getHeight() / 2 + 50);
    }

    
    private void restart() {
        stopGame();
        Stage stage = (Stage) canvas.getScene().getWindow();
        Game newGame = new Game(difficulty);
        newGame.start(stage);
    }

    
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        soundManager.pauseBackgroundMusic();
        soundManager.stopAllSoundEffects();
    }

}
