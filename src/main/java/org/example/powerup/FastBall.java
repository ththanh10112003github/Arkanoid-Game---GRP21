package org.example.powerup;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.ball.Ball;

/**
 * FastBall power-up: increases ball speed multiplier from 0.3 baseline to 0.5 equivalent.
 * Implementation: multiply current velocity by (0.5 / 0.3) ≈ 1.6667 once when collected.
 */
public class FastBall extends PowerUp {
    private static final double TARGET_MULTIPLIER = 1.8;

    public FastBall(double x, double y, double size) {
        super(x, y, size);
    }

    @Override
    public String getId() {
        return "FastBall";
    }

    @Override
    public void apply(Ball ball) {
        if (collected) return;
        ball.scaleSpeed(TARGET_MULTIPLIER);
        collected = true;
    }

    @Override
    public void revert(Ball ball) {
        // revert by resetting to original speed
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


