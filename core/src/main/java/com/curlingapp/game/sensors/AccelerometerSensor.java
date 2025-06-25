package com.curlingapp.game.sensors;

import com.badlogic.gdx.Gdx;

public class AccelerometerSensor {

    private float accelerometerX, accelerometerY, accelerometerZ;
    private float smoothedX, smoothedY, smoothedZ;
    private float smoothingFactor = 0.1f; // Anpassen nach Bedarf

    public AccelerometerSensor() {
        // Initialisierung (leer, da die Initialisierung im SensorManager erfolgt)
    }

    public void update() {
        // Aktualisiere die Sensorwerte
        accelerometerX = Gdx.input.getAccelerometerX();
        accelerometerY = Gdx.input.getAccelerometerY();
        accelerometerZ = Gdx.input.getAccelerometerZ();

        // Glättung anwenden
        smoothedX = smoothedX + smoothingFactor * (accelerometerX - smoothedX);
        smoothedY = smoothedY + smoothingFactor * (accelerometerY - smoothedY);
        smoothedZ = smoothedZ + smoothingFactor * (accelerometerZ - smoothedZ);
    }

    public float getAccelerometerX() {
        return accelerometerX;
    }

    public float getAccelerometerY() {
        return accelerometerY;
    }

    public float getAccelerometerZ() {
        return accelerometerZ;
    }

    public float getSmoothedAccelerometerX() {
        return smoothedX;
    }

    public float getSmoothedAccelerometerY() {
        return smoothedY;
    }

    public float getSmoothedAccelerometerZ() {
        return smoothedZ;
    }
}
