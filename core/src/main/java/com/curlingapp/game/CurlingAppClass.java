package com.curlingapp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.curlingapp.game.deviceidsystem.DeviceIdManager;

public class CurlingAppClass extends Game {
    private String deviceId; // Geräte-ID speichern

    @Override
    public void create() {
        deviceId = DeviceIdManager.getDeviceId();
        setScreen(new MainMenuScreen(this));  // Übergabe der ID an den Screen
    }

    public String getDeviceId() { // Getter für die Geräte-ID
        return deviceId;
    }
}
