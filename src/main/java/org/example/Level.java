package org.example;

import org.example.brick.Brick;
import org.example.brick.NormalBrick;
import org.example.brick.StrongBrick;
import org.example.brick.UnbreakableBrick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Level {

    public static List<Brick> loadLevel(Difficulty difficulty, int levelNumber) {
        String path = "assets/levels/" + difficulty.name().toLowerCase() + "/" + levelNumber + ".txt";
        List<Brick> bricks = new ArrayList<>();
        int brickHeight = 25;
        int startY = 50;
        int spacing = 5;

        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            if (lines.isEmpty()) {
                return bricks;
            }

            String[] firstLine = lines.get(0).split(" ");
            int numCols = firstLine.length;
            double brickWidth = (800.0 - (numCols + 1) * spacing) / numCols;
            double startX = spacing;

            for (int row = 0; row < lines.size(); row++) {
                String[] brickTypes = lines.get(row).split(" ");
                for (int col = 0; col < brickTypes.length; col++) {
                    int brickType = Integer.parseInt(brickTypes[col]);
                    if (brickType == 0) {
                        continue;
                    }

                    double x = startX + col * (brickWidth + spacing);
                    double y = startY + row * (brickHeight + spacing);

                    switch (brickType) {
                        case 1:
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                            break;
                        case 2:
                            bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                            break;
                        case 3:
                            bricks.add(new UnbreakableBrick(x, y, brickWidth, brickHeight));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bricks;
    }
}