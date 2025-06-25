package com.curlingapp.game.settings.gametouch;

import com.curlingapp.game.settings.Difficulty;

public class GameTouchModiSettings {
    private Difficulty difficulty;
    private float stoneSize;
    private int timeLimit;
    private float stoneSpeedMultiplier;
    private float targetRadius;

    public GameTouchModiSettings(Difficulty difficulty) {
        this.difficulty = difficulty;
        applyDifficultySettings();
    }

    private void applyDifficultySettings() {
        switch (difficulty) {
            case EASY:
                stoneSize = 150f;
                timeLimit = 120;
                stoneSpeedMultiplier = 1.2f;
                targetRadius = 350f;
                break;
            case NORMAL:
                stoneSize = 120f;
                timeLimit = 90;
                stoneSpeedMultiplier = 1.0f;
                targetRadius = 315f;
                break;
            case HARD:
                stoneSize = 100f;
                timeLimit = 60;
                stoneSpeedMultiplier = 0.8f;
                targetRadius = 280f;
                break;
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public float getStoneSize() {
        return stoneSize;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public float getStoneSpeedMultiplier() {
        return stoneSpeedMultiplier;
    }

    public float getTargetRadius() {
        return targetRadius;
    }
}
