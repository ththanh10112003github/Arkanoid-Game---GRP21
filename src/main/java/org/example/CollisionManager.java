package org.example;

import org.example.ball.Ball;
import org.example.brick.NormalBrick;

public class CollisionManager {

    /**
     * Generic AABB overlap check for any two GameObject instances.
     *
     * @param a first object
     * @param b second object
     * @return true if AABB boxes overlap
     */
    public static boolean isColliding(GameObject a, GameObject b) {
        return (a.x + a.width) >= b.x && a.x <= (b.x + b.width)
                && (a.y + a.height) >= b.y && a.y <= (b.y + b.height);
    }

    /**
     * Handles collision detection and response between the ball and the paddle.
     * Uses AABB collision detection with overlap resolution to determine bounce direction.
     *
     * @param ball the ball object
     * @param paddle the paddle object
     */
    public static void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!isColliding(ball, paddle)) {
            return;
        }
        
        // Calculate ball boundaries
        double ballLeft = ball.getX();
        double ballRight = ball.getX() + ball.getSize();
        double ballTop = ball.getY();
        double ballBottom = ball.getY() + ball.getSize();

        // Calculate paddle boundaries
        double paddleLeft = paddle.x;
        double paddleRight = paddle.x + paddle.width;
        double paddleTop = paddle.y;
        double paddleBottom = paddle.y + paddle.height;

        // Calculate overlap on each side
        double overlapLeft = ballRight - paddleLeft;
        double overlapRight = paddleRight - ballLeft;
        double overlapTop = ballBottom - paddleTop;
        double overlapBottom = paddleBottom - ballTop;

        // Find minimum overlap to determine collision direction
        double minOverlap = Math.min(
                Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom)
        );

        // Determine collision direction and bounce accordingly
        boolean shouldBounce = false;

        if (minOverlap == overlapTop && ball.getDy() > 0) {
            shouldBounce = true;
            ball.reverseY();
            ball.y = paddleTop - ball.getSize() - 5;
        } else if (minOverlap == overlapBottom && ball.getDy() < 0) {
            shouldBounce = true;
            ball.reverseY();
            ball.y = paddleBottom + 5;
        } else if (minOverlap == overlapLeft && ball.getDx() > 0) {
            shouldBounce = true;
            ball.reverseX();
            ball.x = paddleLeft - ball.getSize() - 5;
        } else if (minOverlap == overlapRight && ball.getDx() < 0) {
            shouldBounce = true;
            ball.reverseX();
            ball.x = paddleRight + 5;
        }

        // If no bounce is needed, update the ball position to the paddle
        if (!shouldBounce) {
            if (minOverlap == overlapTop) {
                // Ball hit brick from top
                ball.y = paddleTop - ball.getSize() - 5;
            } else if (minOverlap == overlapBottom) {
                // Ball hit brick from bottom
                ball.y = paddleBottom + 5;
            } else if (minOverlap == overlapLeft) {
                // Ball hit brick from left
                ball.x = paddleLeft - ball.getSize() - 5;
            } else if (minOverlap == overlapRight) {
                // Ball hit brick from right
                ball.x = paddleRight + 5;
            }
        }
    }

    /**
     * Handles collision detection and response between the ball and a brick.
     * Uses AABB collision detection with overlap resolution to determine bounce direction.
     * Destroys the brick if collision occurs.
     *
     * @param ball the ball object
     * @param brick the brick object
     */
    public static void handleBallBrickCollision(Ball ball, NormalBrick brick) {
        if (brick.isDestroyed() || !isColliding(ball, brick)) {
            return;
        }

        // Calculate ball boundaries
        double ballLeft = ball.getX();
        double ballRight = ball.getX() + ball.getSize();
        double ballTop = ball.getY();
        double ballBottom = ball.getY() + ball.getSize();

        // Calculate brick boundaries
        double brickLeft = brick.x;
        double brickRight = brick.x + brick.width;
        double brickTop = brick.y;
        double brickBottom = brick.y + brick.height;

        // Calculate overlap on each side
        double overlapLeft = ballRight - brickLeft;
        double overlapRight = brickRight - ballLeft;
        double overlapTop = ballBottom - brickTop;
        double overlapBottom = brickBottom - ballTop;

        // Find minimum overlap to determine collision direction
        double minOverlap = Math.min(
                Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom)
        );

        // Determine collision direction and bounce accordingly
        if (ball.isBreakerMode()) {
            // In breaker mode, destroy the brick instantly and keep moving straight
            brick.destroy();
            return;
        }

        if (minOverlap == overlapTop) {
            // Ball hit brick from top
            ball.reverseY();
            ball.y = brickTop - ball.getSize() - 1;
        } else if (minOverlap == overlapBottom) {
            // Ball hit brick from bottom
            ball.reverseY();
            ball.y = brickBottom + 1;
        } else if (minOverlap == overlapLeft) {
            // Ball hit brick from left
            ball.reverseX();
            ball.x = brickLeft - ball.getSize() - 1;
        } else if (minOverlap == overlapRight) {
            // Ball hit brick from right
            ball.reverseX();
            ball.x = brickRight + 1;
        }

// Destroy the brick
        brick.destroy();
    }

    /**
     * Handles collision detection between the ball and the screen boundaries (walls).
     * Bounces the ball when it hits the left, right, or top wall.
     *
     * @param ball the ball object
     * @param screenWidth the width of the game screen
     * @param screenHeight the height of the game screen (not used, but kept for consistency)
     */
    public static void handleBallWallCollision(Ball ball, int screenWidth, int screenHeight) {
        // Left and right walls
        if (ball.getX() <= 0 || ball.getX() + ball.getSize() >= screenWidth) {
            ball.reverseX();
        }
        
        // Top wall
        if (ball.getY() <= 0) {
            ball.reverseY();
        }
    }
}
