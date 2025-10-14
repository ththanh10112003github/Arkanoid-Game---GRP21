package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
/**
 * The Paddle class represents the player's paddle in the game.
 * It can move left and right based on user input and prevents moving outside the screen.
 */
public class Paddle extends GameObject {
    // The speed at which the paddle moves horizontally
    private double speed = 6;
    // Store the base width without power-up effects
    private double baseWidth;

    /**
     * Constructor initializes the paddle with position and size.
     * @param x the x-coordinate of the paddle
     * @param y the y-coordinate of the paddle
     * @param width the width of the paddle
     * @param height the height of the paddle
     */
    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.baseWidth = width;
    }

    /**
     * Updates the paddle’s position based on key inputs.
     * @param leftPressed true if the left arrow key is pressed
     * @param rightPressed true if the right arrow key is pressed
     */
    public void update(boolean leftPressed, boolean rightPressed) {
        // Move left or right depending on key input
        if (leftPressed) x -= speed;
        if (rightPressed) x += speed;

        // Prevent paddle from moving beyond the left and right edges of the screen
        if (x < 0) x = 0;
        if (x + width > 800) x = 800 - width;
    }

    /**
     * Scales the paddle width by the given factor.
     * Used for power-up BiggerPaddle.
     * @param factor multiplicative factor to scale width
     */
    public void scaleWidth(double mult) {
        width *= mult;
    }

    /**
     * Resets width to base width (used when power-up expires).
     */
    public void resetWidth() {
        width = baseWidth;
    }

    /**
     * Draws the paddle on the canvas.
     * @param gc the GraphicsContext used for rendering shapes
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(x, y, width, height);
    }
}

