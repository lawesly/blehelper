package com.ppcrong.blescanner.utils;

/**
 * Contant.
 */

public class Constant {

    // Lib Name
    public static final String LIB_NAME = "com.ppcrong.blescanner";

    // region [Action]
    public static final String ACTION_SCAN_RESULT_PAIR = LIB_NAME + ".ACTION_SCAN_RESULT_PAIR";
    public static final String ACTION_REQUEST_CONNECT = LIB_NAME + ".ACTION_REQUEST_CONNECT";
    public static final String ACTION_REQUEST_DISCONNECT = LIB_NAME + ".ACTION_REQUEST_DISCONNECT";
    // endregion [Action]

    // region [Key]
    public static final String DEVICE_BLE = "device_ble";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String DEVICE_BATTERY = "device_battery";
    public static final String DEVICE_CONNECTING = "device_connecting";
    public static final String DEVICE_AUTO_CONNECT = "device_auto_connect";
    public static final String DEVICE_PAIR_ADDRESS = "device_pair_address";
    // endregion [Key]

    // region [BLE Scan]
    public static final long SCAN_PERIOD = 60000L;
    public static final long RESCAN_PERIOD = 12000L;
    public static final long SCAN_REPORT_DELAY = 2000L;
    // endregion [BLE Scan]
}
