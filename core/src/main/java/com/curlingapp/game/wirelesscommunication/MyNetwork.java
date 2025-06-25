package com.curlingapp.game.wirelesscommunication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class MyNetwork {

    // Methode zum Überprüfen der Verbindung
    public void checkConnection(final ConnectionCallback callback) {
        HttpHelper.sendGetRequest("https://example.com/api.php?action=check_connection", new HttpCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onConnectionResult(result);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    // Methode zum Abrufen des Leaderboards
    public void getLeaderboard(final LeaderboardCallback callback) {
        HttpHelper.sendGetRequest("https://example.com/api.php?action=get_leaderboard", new HttpCallback<String>() {
            @Override
            public void onSuccess(String result) {
                handleLeaderboardResponse(result, callback);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    private void handleLeaderboardResponse(String result, LeaderboardCallback callback) {
        if (result != null && !result.isEmpty()) {
            JsonValue json = new JsonReader().parse(result);
            if (json != null && json.getString("status", "").equals("success")) {
                JsonValue leaderboardJson = json.get("leaderboard");
                Array<Map<String, String>> leaderboard = parseLeaderboardJson(leaderboardJson);
                callback.onLeaderboardReceived(leaderboard);
            } else {
                Gdx.app.error("MyNetwork", "Ungültige JSON-Antwort: " + json);
                callback.onError(new Exception("Ungültige JSON-Antwort"));
            }
        } else {
            Gdx.app.error("MyNetwork", "Leere oder ungültige Server-Antwort.");
            callback.onError(new Exception("Leere oder ungültige Server-Antwort."));
        }
    }

    private Array<Map<String, String>> parseLeaderboardJson(JsonValue leaderboardJson) {
        Array<Map<String, String>> leaderboard = new Array<>();
        if (leaderboardJson != null && leaderboardJson.isArray()) {
            for (int i = 0; i < leaderboardJson.size; i++) {
                JsonValue entry = leaderboardJson.get(i);
                if (entry != null) {
                    Map<String, String> leaderboardEntry = new HashMap<>();
                    leaderboardEntry.put("name", entry.getString("name", "Unbekannt"));
                    leaderboardEntry.put("best_score", entry.getString("best_score", "0"));
                    leaderboard.add(leaderboardEntry);
                }
            }
        }
        return leaderboard;
    }

    // Methode zur Registrierung eines Geräts
    public void registerDevice(final String deviceId, final NameCallback callback) {
        String url = "https://example.com/api.php?action=register_device&device_id=" + deviceId;
        HttpHelper.sendGetRequest(url, new HttpCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JsonValue json = new JsonReader().parse(result);
                if (json.getString("status").equals("success")) {
                    callback.onNameReceived(json.getString("name"));
                } else {
                    callback.onError(new Exception("Fehler: " + json.getString("message")));
                }
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    // Methode zum Speichern des Spielergebnisses
    public void saveGameResult(final String deviceId, final int score, final String gameMode, final ResultCallback callback) {
        String content = "{\"device_id\":\"" + deviceId + "\", \"score\":" + score + ", \"game_mode\":\"" + gameMode + "\"}";
        HttpHelper.sendPostRequest("https://example.com/api.php?action=save_game_result", content, new HttpCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JsonValue json = new JsonReader().parse(result);
                if (json.getString("status").equals("success")) {
                    callback.onResultSaved();
                } else {
                    callback.onError(new Exception("Fehler: " + json.getString("message")));
                }
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    // Callback-Interfaces
    public interface LeaderboardCallback {
        void onLeaderboardReceived(Array<Map<String, String>> leaderboard);
        void onError(Throwable t);
    }

    public interface ResultCallback {
        void onResultSaved();
        void onError(Throwable t);
    }

    public interface NameCallback {
        void onNameReceived(String name);
        void onError(Throwable t);
    }

    public interface ConnectionCallback {
        void onConnectionResult(String response);
        void onError(Throwable t);
    }
}
