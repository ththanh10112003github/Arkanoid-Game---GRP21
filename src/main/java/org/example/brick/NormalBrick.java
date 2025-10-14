package org.example.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.GameObject;
/**
 * The NormalBrick class represents a single breakable brick in the game.
 * Each brick can be destroyed when hit by the ball and will no longer be drawn afterward.
 */
public class NormalBrick extends GameObject {
    private boolean destroyed = false;

    /**
     * Constructs a NormalBrick with a given position and size.
     * @param x the x-coordinate of the brick
     * @param y the y-coordinate of the brick
     * @param width the width of the brick
     * @param height the height of the brick
     */
    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Returns whether the brick has been destroyed.
     * @return true if destroyed, false otherwise
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Marks the brick as destroyed, preventing it from being drawn again.
     */
    public void destroy() {
        destroyed = true;
    }

    /**
     * Draws the brick on the canvas if it has not been destroyed.
     * Uses an orange fill and black border for visibility.
     * @param gc the GraphicsContext used for drawing
     */
    @Override
    public void draw(GraphicsContext gc) {
        if (!destroyed) {
            gc.setFill(Color.ORANGE);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }
}

