package com.curlingapp.game.utils;

import com.badlogic.gdx.utils.Timer;

public class GameTimerUtils {

    private Timer timer;
    private boolean isRunning; // Füge eine Statusvariable hinzu

    public GameTimerUtils() {
        timer = new Timer();
        isRunning = false; // Initialisiere den Status als "nicht laufend"
    }

    /**
     * Führt eine Aufgabe nach einer bestimmten Verzögerung aus.
     *
     * @param delay Die Verzögerung in Sekunden.
     * @param task  Die Aufgabe, die ausgeführt werden soll.
     */
    public void scheduleTask(float delay, Timer.Task task) {
        timer.scheduleTask(task, delay);
        isRunning = true; // Setze den Status auf "laufend"
    }

    /**
     * Führt eine Aufgabe wiederholt nach einer bestimmten Verzögerung aus.
     *
     * @param delay    Die Verzögerung in Sekunden.
     * @param interval Das Intervall zwischen den Wiederholungen in Sekunden.
     * @param task     Die Aufgabe, die ausgeführt werden soll.
     */
    public void scheduleTask(float delay, float interval, Timer.Task task) {
        timer.scheduleTask(task, delay, interval);
        isRunning = true; // Setze den Status auf "laufend"
    }

    /**
     * Stoppt alle geplanten Aufgaben.
     */
    public void stop() {
        timer.stop();
        timer.clear(); // Alle Aufgaben entfernen
        isRunning = false; // Setze den Status auf "nicht laufend"
    }

    /**
     * Startet den Timer, falls er gestoppt wurde.
     */
    public void start() {
        timer.start();
        isRunning = true; // Setze den Status auf "laufend"
    }

    /**
     * Setzt den Timer zurück.
     */
    public void clear() {
        timer.clear();
        isRunning = false; // Setze den Status auf "nicht laufend"
    }

    /**
     * Überprüft, ob der Timer läuft.
     *
     * @return true, wenn der Timer läuft, false sonst.
     */
    public boolean isRunning() {
        return isRunning;
    }
}
