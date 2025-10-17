package org.example.brick;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StrongBrick extends Brick {
    Random random = new Random();

    public StrongBrick(double x, double y, double width, double height) {
        super(x, y, width, height, "Strong", 300, 3);

        double rand = random.nextDouble();
        if (rand < 0.5) this.hitToBreak = 2;

        if (this.hitToBreak == 3)
            this.brickColor = Color.RED;
        else this.brickColor = Color.ORANGE;
    }

    @Override
    public boolean hit() {
        boolean isDestroyed = super.hit();

        int remainingHits = hitToBreak - currentHits;
        if (remainingHits == 2) {
            this.brickColor = Color.ORANGE;
        } else if (remainingHits == 1) {
            this.brickColor = Color.YELLOW;
        }

        return isDestroyed;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (!destroyed) {
            gc.setFill(brickColor);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }
}

