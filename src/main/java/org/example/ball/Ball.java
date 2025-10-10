package org.example.ball;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.GameObject;
/**
 * The Ball class represents the moving ball in the game.
 * It extends GameObject and includes velocity components for movement.
 */
public class Ball extends GameObject {
    // Horizontal and vertical velocity components
    private double dx, dy;

    /**
     * Constructs a Ball object with a given position, size, and initial velocity.
     * @param x the initial x-coordinate of the ball
     * @param y the initial y-coordinate of the ball
     * @param size the diameter of the ball
     * @param dx the initial horizontal velocity
     * @param dy the initial vertical velocity
     */
    public Ball(double x, double y, double size, double dx, double dy) {
        super(x, y, size, size);
        // The speed is reduced to make the movement slower and smoother
        this.dx = dx * 0.5;
        this.dy = dy * 0.5;
    }

    /**
     * Updates the ball’s position based on its velocity.
     * Called once per frame in the game loop.
     */
    public void update() {
        x += dx;
        y += dy;
    }

    /**
     * Reverses the ball’s horizontal direction (used for wall or paddle collisions).
     */
    public void reverseX() { dx *= -1; }

    /**
     * Reverses the ball’s vertical direction (used for wall, paddle, or brick collisions).
     */
    public void reverseY() { dy *= -1; }

    // Getter methods for position and size
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return width; }

    /**
     * Draws the ball as a white circle on the canvas.
     * @param gc the GraphicsContext used for drawing
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, width, height);
    }
}

