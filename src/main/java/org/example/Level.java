package org.example;

import org.example.brick.NormalBrick;

import java.util.ArrayList;
import java.util.List;
/**
 * The Level class is responsible for creating and managing game levels.
 * Each level defines the layout and number of bricks displayed in the game.
 */
public class Level {

    /**
     * Creates the first level of the game with a simple grid of orange bricks.
     * @return a list of NormalBrick objects arranged in rows and columns
     */
    public static List<NormalBrick> createLevel1() {
        List<NormalBrick> normalBricks = new ArrayList<>();

        // Define the layout parameters
        int rows = 5;              // Number of rows of bricks
        int cols = 10;             // Number of columns of bricks
        int brickWidth = 70;       // Width of each brick
        int brickHeight = 25;      // Height of each brick
        int startX = 35;           // Starting X position of the first brick
        int startY = 50;           // Starting Y position of the first brick

        // Creates a grid of bricks with small gaps between them
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + 5);  // Add spacing between columns
                double y = startY + row * (brickHeight + 5); // Add spacing between rows
                normalBricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
            }
        }

        return normalBricks;
    }
}
