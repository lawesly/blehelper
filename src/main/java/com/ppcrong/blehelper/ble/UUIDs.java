package com.ppcrong.blehelper.ble;

import java.util.UUID;

public class UUIDs {

    // region [Battery]
    public static final UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    // endregion [Battery]

    // region [GATT Descriptors]
    public static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    // endregion [GATT Descriptors]
}