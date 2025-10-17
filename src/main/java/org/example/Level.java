package org.example;

import org.example.brick.Brick;
import org.example.brick.NormalBrick;
import org.example.brick.StrongBrick;
import org.example.brick.UnbreakableBrick;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * The Level class responsible for creating and managing game levels.
 * Each level defines the layout and number of bricks displayed in the game.
 */
public class Level {

    public static List<Brick> createLevel1() {
        int rows = 8;
        int cols = 10;
        int brickWidth = 70;
        int brickHeight = 25;
        int startX = 35;
        int startY = 50;

        Brick[][] brickGrid = new Brick[rows][cols];
        Random random = new Random();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + 5);
                double y = startY + row * (brickHeight + 5);
                double rand = random.nextDouble();

                if (rand < 0.10) {
                    brickGrid[row][col] = new UnbreakableBrick(x, y, brickWidth, brickHeight);
                } else if (rand < 0.55) {
                    brickGrid[row][col] = new StrongBrick(x, y, brickWidth, brickHeight);
                } else {
                    brickGrid[row][col] = new NormalBrick(x, y, brickWidth, brickHeight);
                }
            }
        }

        int expandedRows = rows + 2;
        int expandedCols = cols + 2;
        boolean[][] visited = new boolean[expandedRows][expandedCols];
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{0, 0});
        visited[0][0] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0];
            int c = current[1];

            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];

                if (nr >= 0 && nr < expandedRows && nc >= 0 && nc < expandedCols && !visited[nr][nc]) {
                    boolean isUnbreakable = false;
                    if (nr > 0 && nr <= rows && nc > 0 && nc <= cols) {
                        if (brickGrid[nr - 1][nc - 1] instanceof UnbreakableBrick) {
                            isUnbreakable = true;
                        }
                    }

                    if (!isUnbreakable) {
                        visited[nr][nc] = true;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
        }

        List<Brick> bricks = new ArrayList<>();
        int breakableBrickCount = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!visited[row + 1][col + 1]) {
                    double x = brickGrid[row][col].x;
                    double y = brickGrid[row][col].y;
                    bricks.add(new UnbreakableBrick(x, y, brickWidth, brickHeight));
                } else {
                    Brick brick = brickGrid[row][col];
                    if (!(brick instanceof UnbreakableBrick)) {
                        breakableBrickCount++;
                    }
                    bricks.add(brick);
                }
            }
        }

        if (breakableBrickCount == 0) {
            return createLevel1();
        }

        return bricks;
    }
}
