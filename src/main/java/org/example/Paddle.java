package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle extends GameObject {
    private double speed = 6;
    private double baseWidth;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.baseWidth = width;
    }

    public void update(boolean leftPressed, boolean rightPressed) {
        if (leftPressed) x -= speed;
        if (rightPressed) x += speed;

        if (x < 0) x = 0;
        if (x + width > 800) x = 800 - width;
    }

    public void scaleWidth(double mult) {
        width *= mult;
    }

    public void resetWidth() {
        width = baseWidth;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(x, y, width, height);
    }
}

