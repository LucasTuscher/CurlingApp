package com.curlingapp.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.curlingapp.game.data.GameResult;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Array;

public class SaveGameUtils {

    private static final String PREFS_NAME = "curling_save"; // Einmal definieren
    private static final String RESULTS_KEY = "game_results";
    private static final String STONE_X_KEY = "stoneX";
    private static final String STONE_Y_KEY = "stoneY";
    private static final String VELOCITY_X_KEY = "velocityX";
    private static final String VELOCITY_Y_KEY = "velocityY";
    private static final String ROTATION_KEY = "rotation";
    private static final String POINTS_KEY = "points";

    // Methoden für GameResult Speicherung
    public static void saveGameResult(GameResult result) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        Json json = new Json();
        Array<GameResult> results = getGameResults();
        results.add(result);
        prefs.putString(RESULTS_KEY, json.toJson(results));
        prefs.flush();
    }

    public static Array<GameResult> getGameResults() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        Json json = new Json();
        String resultsJson = prefs.getString(RESULTS_KEY, json.toJson(new Array<GameResult>()));
        return json.fromJson(Array.class, GameResult.class, resultsJson);
    }

    public static void clearGameResults() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.remove(RESULTS_KEY);
        prefs.flush();
    }

    // Methoden für Spielstand Speicherung (GameState)
    public static void saveGame(float stoneX, float stoneY, float velocityX, float velocityY, float rotation, int points) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putFloat(STONE_X_KEY, stoneX);
        prefs.putFloat(STONE_Y_KEY, stoneY);
        prefs.putFloat(VELOCITY_X_KEY, velocityX);
        prefs.putFloat(VELOCITY_Y_KEY, velocityY);
        prefs.putFloat(ROTATION_KEY, rotation);
        prefs.putInteger(POINTS_KEY, points);
        prefs.flush(); // Speichern erzwingen
    }

    public static GameState loadGame() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        if (prefs.contains(STONE_X_KEY)) {
            float stoneX = prefs.getFloat(STONE_X_KEY);
            float stoneY = prefs.getFloat(STONE_Y_KEY);
            float velocityX = prefs.getFloat(VELOCITY_X_KEY);
            float velocityY = prefs.getFloat(VELOCITY_Y_KEY);
            float rotation = prefs.getFloat(ROTATION_KEY);
            int points = prefs.getInteger(POINTS_KEY);
            return new GameState(stoneX, stoneY, velocityX, velocityY, rotation, points);
        }
        return null; // Kein gespeicherter Spielstand gefunden
    }

    public static void clearSaveGame() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.clear();
        prefs.flush();
    }

    // Datenklasse für den Spielzustand
    public static class GameState {
        public float stoneX, stoneY;
        public float velocityX, velocityY;
        public float rotation;
        public int points;

        public GameState(float stoneX, float stoneY, float velocityX, float velocityY, float rotation, int points) {
            this.stoneX = stoneX;
            this.stoneY = stoneY;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
            this.points = points;
        }
    }
}


/*
// Speichern des Spielstands
SaveGameUtils.saveGame(stoneX, stoneY, velocityX, velocityY, rotation, points);

// Laden des Spielstands
SaveGameUtils.GameState gameState = SaveGameUtils.loadGame();
if (gameState != null) {
    stoneX = gameState.stoneX;
    stoneY = gameState.stoneY;
    velocityX = gameState.velocityX;
    velocityY = gameState.velocityY;
    rotation = gameState.rotation;
    points = gameState.points;
}

// Löschen des Spielstands
SaveGameUtils.clearSaveGame();
 */
