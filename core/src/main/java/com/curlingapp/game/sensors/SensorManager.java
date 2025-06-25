package com.curlingapp.game.sensors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class SensorManager {

    private AccelerometerSensor accelerometerSensor;
    private GyroscopeSensor gyroscopeSensor;

    private float pitch = 0, roll = 0; // Geschätzte Orientierungswinkel (Komplementärfilter)
    private float timeConstant = 0.98f; // Anpassbar (Komplementärfilter)

    // Kalman-Filter-Variablen
    private float[] state = {0, 0}; // Zustand: [Pitch, Roll]
    private float[][] covariance = {{1, 0}, {0, 1}}; // Kovarianzmatrix
    private float[][] processNoise = {{0.001f, 0}, {0, 0.001f}}; // Prozessrauschen
    private float[][] measurementNoise = {{0.1f, 0}, {0, 0.1f}}; // Messrauschen

    private static final String TAG = "SensorManager";

    public SensorManager() {
        // Initialisiere die Sensoren
        Gdx.input.setInputProcessor(null); // Setze den Input-Prozessor zurück, um Konflikte zu vermeiden

        // Initialisiere die Sensoren
        accelerometerSensor = new AccelerometerSensor();
        gyroscopeSensor = new GyroscopeSensor();

        logSensorAvailability();
    }

    public void update() {
        accelerometerSensor.update();
        gyroscopeSensor.update();

        logSensorValues();
        applyComplementaryFilter();
        applyKalmanFilter();
        logOrientation();
    }

    private void applyComplementaryFilter() {
        // Beschleunigungsmesser-Winkel berechnen
        float accPitch = (float) Math.atan2(accelerometerSensor.getSmoothedAccelerometerY(), accelerometerSensor.getSmoothedAccelerometerZ());
        float accRoll = (float) Math.atan2(-accelerometerSensor.getSmoothedAccelerometerX(), Math.sqrt(accelerometerSensor.getSmoothedAccelerometerY() * accelerometerSensor.getSmoothedAccelerometerY() + accelerometerSensor.getSmoothedAccelerometerZ() * accelerometerSensor.getSmoothedAccelerometerZ()));

        // Gyroskop-Winkeländerung berechnen
        float gyroPitch = gyroscopeSensor.getGyroscopeX() * Gdx.graphics.getDeltaTime();
        float gyroRoll = gyroscopeSensor.getGyroscopeY() * Gdx.graphics.getDeltaTime();

        // Komplementärfilter anwenden
        pitch = timeConstant * (pitch + gyroPitch) + (1 - timeConstant) * accPitch;
        roll = timeConstant * (roll + gyroRoll) + (1 - timeConstant) * accRoll;
    }

    private void applyKalmanFilter() {
        try {
            // Vorhersage
            float[] predictedState = predictState(state, gyroscopeSensor.getGyroscopeX(), gyroscopeSensor.getGyroscopeY());
            float[][] predictedCovariance = predictCovariance(covariance, processNoise);

            // Messung
            float[] measurement = {
                (float) Math.atan2(accelerometerSensor.getSmoothedAccelerometerY(), accelerometerSensor.getSmoothedAccelerometerZ()),
                (float) Math.atan2(-accelerometerSensor.getSmoothedAccelerometerX(), Math.sqrt(accelerometerSensor.getSmoothedAccelerometerY() * accelerometerSensor.getSmoothedAccelerometerY() + accelerometerSensor.getSmoothedAccelerometerZ() * accelerometerSensor.getSmoothedAccelerometerZ()))
            };

            // Aktualisierung
            float[][] kalmanGain = calculateKalmanGain(predictedCovariance, measurementNoise);
            state = updateState(predictedState, kalmanGain, measurement);
            covariance = updateCovariance(predictedCovariance, kalmanGain);
        } catch (Exception e) {
            logError("Fehler bei der Kalman-Filter-Anwendung", e);
        }
    }

    // Hilfsmethoden für den Kalman-Filter (müssen implementiert werden)
    private float[] predictState(float[] state, float gyroX, float gyroY) {
        // Vorhersage des Zustands basierend auf Gyroskopwerten
        // Hier müsste die Logik implementiert werden
        return state;
    }

    private float[][] predictCovariance(float[][] covariance, float[][] processNoise) {
        // Vorhersage der Kovarianz
        // Hier müsste die Logik implementiert werden
        return covariance;
    }

    private float[][] calculateKalmanGain(float[][] covariance, float[][] measurementNoise) {
        // Berechnung des Kalman-Verstärkungsfaktors
        // Hier müsste die Logik implementiert werden
        return covariance;
    }

    private float[] updateState(float[] predictedState, float[][] kalmanGain, float[] measurement) {
        // Aktualisierung des Zustands basierend auf der Messung
        // Hier müsste die Logik implementiert werden
        return predictedState;
    }

    private float[][] updateCovariance(float[][] predictedCovariance, float[][] kalmanGain) {
        // Aktualisierung der Kovarianz
        // Hier müsste die Logik implementiert werden
        return predictedCovariance;
    }

    // Getter-Methoden für die geschätzten Winkel
    public float getPitch() {
        return (float) Math.toDegrees(pitch);
    }

    public float getRoll() {
        return (float) Math.toDegrees(roll);
    }

    // Getter-Methoden für die rohen Sensorwerte
    public float getAccelerometerX() {
        return accelerometerSensor.getAccelerometerX() * 14;
    }

    public float getAccelerometerY() {
        return accelerometerSensor.getAccelerometerY() * 14;
    }

    public float getAccelerometerZ() {
        return accelerometerSensor.getAccelerometerZ();
    }

    private void logSensorAvailability() {
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            Gdx.app.log(TAG, "Beschleunigungssensor verfügbar");
        } else {
            Gdx.app.error(TAG, "Beschleunigungssensor nicht verfügbar");
        }

        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope)) {
            Gdx.app.log(TAG, "Gyroskop verfügbar");
        } else {
            Gdx.app.error(TAG, "Gyroskop nicht verfügbar");
        }
    }

    private void logSensorValues() {
        Gdx.app.debug(TAG, "Beschleunigungsmesser: X=" + accelerometerSensor.getAccelerometerX() +
            ", Y=" + accelerometerSensor.getAccelerometerY() +
            ", Z=" + accelerometerSensor.getAccelerometerZ());
        Gdx.app.debug(TAG, "Gyroskop: X=" + gyroscopeSensor.getGyroscopeX() +
            ", Y=" + gyroscopeSensor.getGyroscopeY() +
            ", Z=" + gyroscopeSensor.getGyroscopeZ());
    }

    private void logOrientation() {
        Gdx.app.debug(TAG, "Orientierung: Pitch=" + getPitch() + ", Roll=" + getRoll());
    }

    private void logError(String message, Throwable exception) {
        Gdx.app.error(TAG, message, exception);
    }
}
