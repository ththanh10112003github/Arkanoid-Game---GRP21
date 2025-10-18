package org.example.powerup;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import org.example.ball.Ball;
import org.example.Paddle;

/**
 * Power-up that makes the ball destroy all bricks in its path
 * without bouncing for a limited time.
 */
public class BreakerBall extends PowerUp {
    private Color color = Color.RED; // visible difference
    private boolean active = false;

    public BreakerBall(double x, double y, double size) {
        super(x, y, size);
        duration = 6.0; // lasts 6 seconds
    }

    @Override
    public String getId() {
        return "breakerBall";
    }

    @Override
    public void apply(Ball ball) {
        if (ball == null) return;
        active = true;
        ball.setBreakerMode(true);
        setCollected();
    }

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
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x, y, width, height);
    }
}
