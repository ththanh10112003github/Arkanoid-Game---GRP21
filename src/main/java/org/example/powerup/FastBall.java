package org.example.powerup;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.Paddle;
import org.example.ball.Ball;

/**
 * FastBall power-up: increases ball speed by x times for a limited time.
 */
public class FastBall extends PowerUp {
    private static final double SPEED_MULTIPLIER = 1.8;

    public FastBall(double x, double y, double size) {
        super(x, y, size);
        this.soundEffect = "fast_ball";
    }

    @Override
    public String getId() {
        return "FastBall";
    }

    public void applyToBall(Ball ball) {
        if (collected) return;
        ball.scaleSpeed(SPEED_MULTIPLIER);
        collected = true;
    }

    @Override
    public void reset(Ball ball, Paddle paddle) {
        ball.resetSpeed();
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (collected) return;
        gc.setFill(Color.GOLD);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x, y, width, height);
    }
}


