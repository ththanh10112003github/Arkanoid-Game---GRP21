package org.example.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class NormalBrick extends Brick {

    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height, "Normal", 100, 1);
        this.brickColor = Color.YELLOW;
    }
    
    public NormalBrick(double x, double y, double width, double height, int points) {
        super(x, y, width, height, "Normal", points, 1);
        this.brickColor = Color.YELLOW;
    }

   
    @Override
    public void draw(GraphicsContext gc) {
        if (!destroyed) {
            gc.setFill(brickColor);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }
}