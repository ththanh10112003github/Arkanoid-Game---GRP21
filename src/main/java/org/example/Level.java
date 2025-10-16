package org.example;

import org.example.brick.Brick;
import org.example.brick.NormalBrick;

import java.util.ArrayList;
import java.util.List;
/**
 * The Level class responsible for creating and managing game levels.
 * Each level defines the layout and number of bricks displayed in the game.
 */
public class Level {

    public static List<Brick> createLevel1() {
        List<Brick> bricks = new ArrayList<>();

        int rows = 5;           // Number of rows of bricks
        int cols = 10;          // Number of columns of bricks
        int brickWidth = 70;    // Width of each brick
        int brickHeight = 25;   // Height of each brick
        int startX = 35;        // Starting X position of the first brick
        int startY = 50;        // Starting Y position of the first brick

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + 5);
                double y = startY + row * (brickHeight + 5);
                bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
            }
        }

        return bricks;
    }
}
