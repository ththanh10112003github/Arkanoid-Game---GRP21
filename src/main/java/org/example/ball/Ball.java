package org.example.ball;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.GameObject;

public class Ball extends GameObject {
    private double dx, dy;
    private double baseSpeed;

    public Ball(double x, double y, double size, double dx, double dy) {
        super(x, y, size, size);
        this.dx = dx;
        this.dy = dy;
        this.baseSpeed = Math.sqrt(this.dx * this.dx + this.dy * this.dy);
    }

    /**
     * Updates the ball’s position based on its dx and dy velocity.
     */
    public void update() {
        x += dx;
        y += dy;
    }

    /**
     * Scales the current speed by the multilpier.
     */
    public void scaleSpeed(double mult) {
        dx *= mult;
        dy *= mult;
    }

    /**
     * Resets speed to base speed when power-up expires.
     */
    public void resetSpeed() {
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        
        if (Math.abs(currentSpeed - baseSpeed) > 0.001) {
            // Get current direction
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

    public double getX() { return x; }
    public double getY() { return y; }

    public double getDx() { return dx; }
    public double getDy() { return dy; }

    public double getSize() { return width; }

    /**
     * Draws the ball on the canvas.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, width, height);
    }
}

