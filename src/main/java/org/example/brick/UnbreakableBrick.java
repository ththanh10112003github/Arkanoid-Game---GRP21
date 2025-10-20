package org.example.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UnbreakableBrick extends Brick {

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height, "Unbreakable", 0, Integer.MAX_VALUE);
        this.brickColor = Color.GRAY;
    }

    @Override
    public boolean hit() {
        return false;
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
