package com.curlingapp.game.android;

import android.content.Context;
import android.provider.Settings;

import com.curlingapp.game.deviceidsystem.DeviceIdProvider;

public class AndroidDeviceIdProvider implements DeviceIdProvider {
    private Context context;

    public AndroidDeviceIdProvider(Context context) {
        this.context = context;
    }

    @Override
    public String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
