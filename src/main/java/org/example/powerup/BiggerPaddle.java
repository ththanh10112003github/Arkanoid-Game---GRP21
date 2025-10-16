package org.example.powerup;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.Paddle;
import org.example.ball.Ball;

/**
 * BiggerPaddlePowerUp power-up: increases paddle width by x times for a limited time.
 */
public class BiggerPaddle extends PowerUp {
    private static final double SIZE_MULTIPLIER = 1.5;

    public BiggerPaddle(double x, double y, double size) {
        super(x, y, size);
    }

    @Override
    public String getId() {
        return "BiggerPaddle";
    }

    public void applyToPaddle(Paddle paddle) {
        if (collected) return;
        paddle.scaleWidth(SIZE_MULTIPLIER);
        collected = true;
    }

    @Override
    public void reset(Ball ball, Paddle paddle) {
        paddle.resetWidth();
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (collected) return;
        gc.setFill(Color.CYAN);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x, y, width, height);
    }
}

