package com.curlingapp.game.data;

import com.curlingapp.game.gamemodes.GameMode;

import java.io.Serializable;

public class GameResult implements Serializable {
    private int score;
    private long timestamp;

    private GameMode gameMode; // Füge den GameMode hier hinzu

    // No-Argument-Konstruktor hinzufügen
    public GameResult() {
        // Leerer Konstruktor
    }

    public GameResult(int score, long timestamp, GameMode gameMode) {
        this.score = score;
        this.timestamp = timestamp;
        this.gameMode = gameMode; // Speichere den GameMode
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameResult(int score, long timestamp) {
        this.score = score;
        this.timestamp = timestamp;
    }

    public int getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
