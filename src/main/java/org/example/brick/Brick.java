package org.example.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.GameObject;

public abstract class Brick extends GameObject {
    protected boolean destroyed = false;
    protected String brickType;
    protected int points;
    protected int hitToBreak;
    protected int currentHits;
    protected Color brickColor;

    
    public Brick(double x, double y, double width, double height, 
                 String brickType, int points, int hitToBreak) {
        super(x, y, width, height);
        this.brickType = brickType;
        this.points = points;
        this.hitToBreak = hitToBreak;
        this.currentHits = 0;
        this.brickColor = Color.WHITE;
    }


    public boolean hit() {
        currentHits++;
        if (currentHits >= hitToBreak) {
            destroyed = true;
            return true;
        }
        return false;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
    }

    public String getBrickType() {
        return brickType;
    }

    public int getPoints() {
        return points;
    }

    public int getHitToBreak() {
        return hitToBreak;
    }

    public int getCurrentHits() {
        return currentHits;
    }

    public Color getBrickColor() {
        return brickColor;
    }

    public abstract void draw(GraphicsContext gc);

    public String getStatusInfo() {
        return String.format("%s Brick - Hits: %d/%d - Points: %d", 
                           brickType, currentHits, hitToBreak, points);
    }
}
