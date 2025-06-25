package com.curlingapp.game.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.curlingapp.game.CurlingAppClass;
import com.curlingapp.game.deviceidsystem.DeviceIdManager;
import com.curlingapp.game.deviceidsystem.DeviceIdProvider;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        // Initialisiere Android-spezifischen DeviceIdProvider
        DeviceIdProvider provider = new AndroidDeviceIdProvider(this);
        DeviceIdManager.setDeviceIdProvider(provider);  // Setze den Provider im DeviceIdManager

        initialize(new CurlingAppClass(), configuration);
    }

}
