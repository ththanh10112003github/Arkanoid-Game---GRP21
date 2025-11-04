package org.example.powerup;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import org.example.ball.Ball;
import org.example.Paddle;

/**
 * Power-up that allows the ball to destroy all bricks in its path
 * without bouncing for a limited time.
 */
public class BreakerBall extends PowerUp {
    private final Color color = Color.GREEN;
    private boolean active = false;

    public BreakerBall(double x, double y, double size) {
        super(x, y, size);
        duration = 6.0; // lasts 6 seconds
    }

    @Override
    public String getId() {
        return "BreakerBall";
    }

    /** Activate breaker mode on a given ball */
    public void applyToBall(Ball ball) {
        if (ball == null) return;
        active = true;
        ball.setBreakerMode(true);
        setCollected();
    }

    /** Reset the effect when duration expires */
    @Override
    public void reset(Ball ball, Paddle paddle) {
        if (ball == null) return;
        ball.setBreakerMode(false);
        active = false;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (collected) return;
        gc.setFill(color);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x, y, width, height);
    }

    @Override
    public String getSoundEffect() {
        return "powerup_breaker";
    }
}
