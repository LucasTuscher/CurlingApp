package com.curlingapp.game.sensors;

import com.badlogic.gdx.Gdx;

public class GyroscopeSensor {

    private float gyroscopeX, gyroscopeY, gyroscopeZ;

    public GyroscopeSensor() {
        // Initialisierung (leer, da die Initialisierung im SensorManager erfolgt)
    }

    public void update() {
        // Aktualisiere die Sensorwerte
        gyroscopeX = Gdx.input.getGyroscopeX();
        gyroscopeY = Gdx.input.getGyroscopeY();
        gyroscopeZ = Gdx.input.getGyroscopeZ();
    }

    public float getGyroscopeX() {
        return gyroscopeX;
    }

    public float getGyroscopeY() {
        return gyroscopeY;
    }

    public float getGyroscopeZ() {
        return gyroscopeZ;
    }
}
