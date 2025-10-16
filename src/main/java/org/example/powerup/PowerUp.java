package org.example.powerup;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.GameObject;
import org.example.Paddle;
import org.example.ball.Ball;

public abstract class PowerUp extends GameObject {
    protected boolean collected = false;
    protected double fallSpeed = 1.0; // fall speed
    protected double duration = 8.0; // duration

    public PowerUp(double x, double y, double size) {
        super(x, y, size, size);
    }

    public void update() {
        y += fallSpeed;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected() {
        collected = true;
    }

    public abstract String getId();


    public abstract void reset(Ball ball, Paddle paddle);
    
    public double getDuration() {
        return duration;
    }

    // Draws the power-up on the canvas
    @Override
    public void draw(GraphicsContext gc) {
        if (collected) return;
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x, y, width, height);
    }
}


