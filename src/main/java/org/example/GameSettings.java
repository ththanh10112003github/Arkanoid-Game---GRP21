package org.example;

public class GameSettings {
    private static Difficulty difficulty = Difficulty.MEDIUM;

    public static Difficulty getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(Difficulty difficulty) {
        GameSettings.difficulty = difficulty;
    }
}
