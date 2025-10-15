package org.example.powerup;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.Paddle;
import org.example.ball.Ball;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TripleBallPowerUp extends PowerUp {

    private static final Random random = new Random();

    public TripleBallPowerUp(double x, double y, double size) {
        super(x, y, size);
    }

    @Override
    public String getId() {
        return "TripleBall";
    }

    @Override
    public void apply(Ball ball) {
        // Logic to add new balls will be in Game.java
    }

    public void apply(List<Ball> balls) {
        if (collected) return;

        List<Ball> newBalls = new ArrayList<>();
        for (Ball originalBall : balls) {
            double originalX = originalBall.getX();
            double originalY = originalBall.getY();
            double speed = Math.sqrt(originalBall.getDx() * originalBall.getDx() + originalBall.getDy() * originalBall.getDy());

            for (int i = 0; i < 2; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double dx = speed * Math.cos(angle);
                double dy = speed * Math.sin(angle);
                newBalls.add(new Ball(originalX, originalY, originalBall.getSize(), dx, dy));
            }
        }

        balls.addAll(newBalls);
        collected = true;
    }


    @Override
    public void reset(Ball ball, Paddle paddle) {}

    @Override
    public void draw(GraphicsContext gc) {
        if (collected) return;
        gc.setFill(Color.RED); // Choose a color for the power-up
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x, y, width, height);
    }
}