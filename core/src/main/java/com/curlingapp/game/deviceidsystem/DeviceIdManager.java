package com.curlingapp.game.deviceidsystem;

public class DeviceIdManager {
    private static DeviceIdProvider deviceIdProvider;

    // Setze den DeviceIdProvider
    public static void setDeviceIdProvider(DeviceIdProvider provider) {
        deviceIdProvider = provider;
    }

    // Diese Methode gibt die Geräte-ID zurück
    public static String getDeviceId() {
        if (deviceIdProvider == null) {
            throw new IllegalStateException("DeviceIdProvider wurde nicht erstellt.");
        }
        return deviceIdProvider.getDeviceId();
    }
}
