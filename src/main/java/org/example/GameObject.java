package org.example;

import javafx.scene.canvas.GraphicsContext;
/**
 * Abstract base class for all drawable game objects in the Arkanoid game.
 * This class defines common properties such as position and size,
 * and enforces that all subclasses implement a draw method.
 */
public abstract class GameObject {
    // Coordinates and dimensions of the game object
    protected double x, y, width, height;

    /**
     * Constructs a GameObject with specified position and size.
     * @param x the x-coordinate of the object
     * @param y the y-coordinate of the object
     * @param width the width of the object
     * @param height the height of the object
     */
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Abstract method that each subclass must implement
     * to define how the object is drawn on the screen.
     * @param gc the GraphicsContext used for rendering
     */
    public abstract void draw(GraphicsContext gc);
}
