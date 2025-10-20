package org.example;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ScoreManager {
    private int score;
    private int lives;
    private int highScore;
    private static final int DEFAULT_LIVES = 3;
 

    public ScoreManager() {
        this.score = 0;
        this.lives = DEFAULT_LIVES;
    }

    public ScoreManager(int lives) {
        this.score = 0;
        this.lives = lives;
    }

    public void addScore(int points) {
        score += points;
        if (score > highScore) {
            highScore = score;
        }
    }

    public boolean loseLife() {
        if (lives > 1) {
            lives--;
            return true;
        } else if (lives == 1) {
            lives = 0;
            return false;
        }
        return false;
    }

    public void addLife() {
        lives++;
    }

    public void reset() {
        score = 0;
        lives = DEFAULT_LIVES;
    }

    public boolean hasLives() {
        return lives > 0;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getHighScore() {
        return highScore;
    }


    public String getScoreString() {
        return String.format("%06d", score);
    }

    public String getHighScoreString() {
        return String.format("%06d", highScore);
    }

    /**
     * Draws the lives display using heart images.
     */
    public void drawLives(GraphicsContext gc, Image heartImage, Image heartEmptyImage, int x, int y) {

        double heartSize = 24;
        double spacing = 30;
        
        for (int i = 0; i < 3; i++) {
            double heartX = x + i * spacing;
            double heartY = y - heartSize;
            
            if (i < lives) {
                if (heartImage != null) {
                    gc.drawImage(heartImage, heartX, heartY, heartSize, heartSize);
                }
            } else {
                if (heartEmptyImage != null) {
                    gc.drawImage(heartEmptyImage, heartX, heartY, heartSize, heartSize);
                }
            }
        }
    }
}
