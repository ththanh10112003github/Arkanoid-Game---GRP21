package org.example;

import org.example.ball.Ball;
import org.example.brick.NormalBrick;
import java.util.List;
/**
 * The CollisionManager class handles all collision detection in the Arkanoid game.
 * It checks and responds to collisions between the ball, walls, paddle, and bricks.
 */
public class CollisionManager {

    /**
     * Handles collisions between the ball and the window boundaries.
     * The ball bounces back when it hits the left, right, or top wall.
     *
     * @param ball the ball object
     * @param width the width of the game area
     * @param height the height of the game area
     */
    public static void handleBallWallCollision(Ball ball, double width, double height) {
        // Bounce horizontally when hitting left or right wall
        if (ball.getX() <= 0 || ball.getX() + ball.getSize() >= width) {
            ball.reverseX();
        }
        // Bounce vertically when hitting the top wall
        if (ball.getY() <= 0) {
            ball.reverseY();
        }
    }

    /**
     * Handles collision detection between the ball and the paddle.
     * If the ball hits the paddle, it reverses its vertical direction.
     *
     * @param ball the moving ball
     * @param paddle the player's paddle
     */
    public static void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (ball.getY() + ball.getSize() >= paddle.y &&
                ball.getX() + ball.getSize() >= paddle.x &&
                ball.getX() <= paddle.x + paddle.width &&
                ball.getY() <= paddle.y + paddle.height) {

            // Reverse the vertical direction when hitting the paddle
            ball.reverseY();
        }
    }

    /**
     * Handles collision detection between the ball and all bricks.
     * When a collision occurs, the brick is destroyed and the ball bounces back.
     *
     * @param ball the ball object
     * @param normalBricks the list of all active bricks
     */
    public static void handleBallBrickCollision(Ball ball, List<NormalBrick> normalBricks) {
        for (NormalBrick b : normalBricks) {
            if (!b.isDestroyed() &&
                    ball.getX() + ball.getSize() >= b.x &&
                    ball.getX() <= b.x + b.width &&
                    ball.getY() + ball.getSize() >= b.y &&
                    ball.getY() <= b.y + b.height) {

                // Destroy the brick and make the ball bounce
                b.destroy();
                ball.reverseY();
                break; // Stop checking after the first collision
            }
        }
    }
}
