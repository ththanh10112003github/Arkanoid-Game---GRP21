package org.example;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    
    protected double x, y, width, height;

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Abstract method to draw the game object
    public abstract void draw(GraphicsContext gc);
}
