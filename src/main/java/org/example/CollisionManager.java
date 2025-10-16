package org.example;

import org.example.ball.Ball;
import org.example.brick.Brick;

public class CollisionManager {

    /**
     * AABB collision check for any two GameObject.
     */
    public static boolean isColliding(GameObject a, GameObject b) {
        return (a.x + a.width) >= b.x && a.x <= (b.x + b.width)
                && (a.y + a.height) >= b.y && a.y <= (b.y + b.height);
    }

    /**
     * Handles collision detection and response between the ball and the paddle.
     * Uses overlap check to determine bounce direction.
     */
    public static void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!isColliding(ball, paddle)) {
            return;
        }
        
        double ballLeft = ball.getX();
        double ballRight = ball.getX() + ball.getSize();
        double ballTop = ball.getY();
        double ballBottom = ball.getY() + ball.getSize();

        double paddleLeft = paddle.x;
        double paddleRight = paddle.x + paddle.width;
        double paddleTop = paddle.y;
        double paddleBottom = paddle.y + paddle.height;

        double overlapLeft = ballRight - paddleLeft;
        double overlapRight = paddleRight - ballLeft;
        double overlapTop = ballBottom - paddleTop;
        double overlapBottom = paddleBottom - ballTop;

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

        // If no bounce needed, update the ball position to the paddle
        if (!shouldBounce) {
            if (minOverlap == overlapTop) 
                ball.y = paddleTop - ball.getSize() - 5;
            } else if (minOverlap == overlapBottom) {
                ball.y = paddleBottom + 5;
            } else if (minOverlap == overlapLeft) {
                ball.x = paddleLeft - ball.getSize() - 5;
            } else if (minOverlap == overlapRight) {
                ball.x = paddleRight + 5;
            }
        }
    

    /**
     * Handles collision detection and response between the ball and a brick.
     * Uses overlap resolution to determine bounce direction.
     * Calls brick.hit() to handle brick damage.
     */
    public static void handleBallBrickCollision(Ball ball, Brick brick) {
        if (brick.isDestroyed() || !isColliding(ball, brick)) {
            return;
        }

        double ballLeft = ball.getX();
        double ballRight = ball.getX() + ball.getSize();
        double ballTop = ball.getY();
        double ballBottom = ball.getY() + ball.getSize();

        double brickLeft = brick.x;
        double brickRight = brick.x + brick.width;
        double brickTop = brick.y;
        double brickBottom = brick.y + brick.height;

        double overlapLeft = ballRight - brickLeft;
        double overlapRight = brickRight - ballLeft;
        double overlapTop = ballBottom - brickTop;
        double overlapBottom = brickBottom - ballTop;

        double minOverlap = Math.min(
                Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom)
        );

        // Determine collision direction and bounce accordingly
        if (minOverlap == overlapTop) {
            ball.reverseY();
            ball.y = brickTop - ball.getSize() - 1;
        } else if (minOverlap == overlapBottom) {
            ball.reverseY();
            ball.y = brickBottom + 1;
        } else if (minOverlap == overlapLeft) {
            ball.reverseX();
            ball.x = brickLeft - ball.getSize() - 1;
        } else if (minOverlap == overlapRight) {
            ball.reverseX();
            ball.x = brickRight + 1;
        }

        brick.hit();
    }

    /**
     * Handles collision detection between the ball and walls.
     * Bounces the ball when it hits the left, right, or top wall.
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
