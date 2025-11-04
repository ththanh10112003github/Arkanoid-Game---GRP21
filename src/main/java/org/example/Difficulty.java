package org.example;

public enum Difficulty {
    EASY(1.2, 0.8),
    MEDIUM(1.0, 1.0),
    HARD(0.8, 1.2);

    private final double paddleWidthMultiplier;
    private final double ballSpeedMultiplier;

    Difficulty(double paddleWidthMultiplier, double ballSpeedMultiplier) {
        this.paddleWidthMultiplier = paddleWidthMultiplier;
        this.ballSpeedMultiplier = ballSpeedMultiplier;
    }

    public double getPaddleWidthMultiplier() {
        return paddleWidthMultiplier;
    }

    public double getBallSpeedMultiplier() {
        return ballSpeedMultiplier;
    }
}
