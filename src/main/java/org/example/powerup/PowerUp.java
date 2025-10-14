package org.example.powerup;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.GameObject;
import org.example.Paddle;
import org.example.ball.Ball;

/**
 * Base class for PowerUps that fall down after a brick is destroyed.
 * When the ball touches a power-up, {@link #apply(Ball)} is invoked
 * then the power-up is marked as collected.
 */
public abstract class PowerUp extends GameObject {
    protected boolean collected = false;
    protected double fallSpeed = 1.0; // slow falling
    protected double duration = 8.0; // default duration

    public PowerUp(double x, double y, double size) {
        super(x, y, size, size);
    }

    public void update() {
        y += fallSpeed;
    }

    public boolean isCollected() {
        return collected;
    }

    /**
     * Marks this power-up as collected.
     */
    public void setCollected() {
        collected = true;
    }

    /**
     * Unique identifier used to prevent stacking the same power-up type.
     */
    public abstract String getId();

    /**
     * Applies the effect of this power-up to the ball.
     */
    public abstract void apply(Ball ball);

    /**
     * Applies the effect of this power-up to the paddle.
     * Default implementation does nothing (for ball-only power-ups).
     * @param paddle the paddle object
     */
    public void applyToPaddle(Paddle paddle) {
        // Default: do nothing
    }

    /**
     * Resets the power-up effects.
     * FastBall: resets ball speed to original
     * BiggerPaddle: resets paddle width to original
     * @param ball the ball object
     * @param paddle the paddle object
     */
    public abstract void reset(Ball ball, Paddle paddle);
    
    public double getDuration() {
        return duration;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (collected) return;
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x, y, width, height);
    }
}


