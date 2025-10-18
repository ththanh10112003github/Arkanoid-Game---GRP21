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
    // Store the base speed without power-up effects
    private double baseSpeed;
    private boolean breakerMode = false;

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
        this.dx = dx;
        this.dy = dy;
        // Calculate and store the base speed
        this.baseSpeed = Math.sqrt(this.dx * this.dx + this.dy * this.dy);
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
     * Scales the current speed by the given factor while preserving direction.
     * Useful for power-ups like FastBall.
     * @param mult multiplicative factor to scale velocity
     */
    public void scaleSpeed(double mult) {
        dx *= mult;
        dy *= mult;
    }

    /**
     * Resets speed to base speed while preserving current direction.
     * Used when power-up expires.
     */
    public void resetSpeed() {
        // Calculate current speed (magnitude)
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        
        // Only reset if current speed is different from base speed
        if (Math.abs(currentSpeed - baseSpeed) > 0.001) {
            // Get current direction (unit vector)
            double directionX = dx / currentSpeed;
            double directionY = dy / currentSpeed;
            
            // Apply base speed to current direction
            dx = directionX * baseSpeed;
            dy = directionY * baseSpeed;
        }
    }

    /**
     * Sets new speed values and updates base speed.
     * Used when ball is reset or recreated.
     */
    public void setSpeed(double newDx, double newDy) {
        this.dx = newDx;
        this.dy = newDy;
        this.baseSpeed = Math.sqrt(newDx * newDx + newDy * newDy);
    }

    /**
     * Reverses the ball’s horizontal direction.
     */
    public void reverseX() { dx *= -1; }

    /**
     * Reverses the ball’s vertical direction.
     */
    public void reverseY() { dy *= -1; }

    // Getter methods for position and size
    public double getX() { return x; }
    public double getY() { return y; }

    public double getDx() { return dx; }
    public double getDy() { return dy; }

    public double getSize() { return width; }

    public void setBreakerMode(boolean breakerMode) {
        this.breakerMode = breakerMode;
    }

    public boolean isBreakerMode() {
        return breakerMode;
    }

    /**
     * Draws the ball as a white circle on the canvas.
     * @param gc the GraphicsContext used for drawing
     */
    @Override
    public void draw(GraphicsContext gc) {
        if (breakerMode) {
            gc.setFill(Color.RED);  // Red for breaker mode
        } else {
            gc.setFill(Color.WHITE); // Normal mode
        }
        gc.fillOval(x, y, width, height);
    }

}

