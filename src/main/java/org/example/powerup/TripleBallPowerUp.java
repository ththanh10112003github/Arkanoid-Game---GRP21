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

    public void apply(List<Ball> balls) {
        if (collected || balls.isEmpty()) return;

        // Create two new balls from the position of the first ball in the list
        Ball originalBall = balls.get(0);
        double originalX = originalBall.getX();
        double originalY = originalBall.getY();
        double speed = Math.sqrt(originalBall.getDx() * originalBall.getDx() + originalBall.getDy() * originalBall.getDy());

        for (int i = 0; i < 2; i++) {
            double angle = generateRandomAngle();
            double dx = speed * Math.cos(angle);
            double dy = speed * Math.sin(angle);
            balls.add(new Ball(originalX, originalY, originalBall.getSize(), dx, dy));
        }

        collected = true;
    }

    private double generateRandomAngle() {
        double angleInDegrees;
        double minAngle = 15.0;
        double maxAngle = 165.0;

        if (random.nextBoolean()) {
            angleInDegrees = minAngle + random.nextDouble() * (maxAngle - minAngle);
        } else {
            angleInDegrees = (minAngle + 180) + random.nextDouble() * (maxAngle - minAngle);
        }

        return Math.toRadians(angleInDegrees);
    }


    @Override
    public void reset(Ball ball, Paddle paddle) {}

    @Override
    public void draw(GraphicsContext gc) {
        if (collected) return;
        gc.setFill(Color.RED);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x, y, width, height);
    }
}