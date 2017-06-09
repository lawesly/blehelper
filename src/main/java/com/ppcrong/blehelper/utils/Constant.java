package com.ppcrong.blehelper.utils;

/**
 * Constant.
 */

public class Constant {

    // Lib Name
    public static final String LIB_NAME = "com.ppcrong.blehelper";

    // region [SP Name]
    public static final String SP_NAME_SCAN_SETTINGS = "scan_settings";
    // endregion [SP Name]

    // region [SP Key]
    public static final String DEVICE_AUTO_CONNECT = "device_auto_connect";
    public static final String DEVICE_PAIR_ADDRESS = "device_pair_address";
    public static final String DEVICE_PAIR_ENABLE = "device_pair_enable";
    public static final String DEVICE_IS_CONNECTED_IN_SCANNER = "device_is_connected_in_scanner";
    // endregion [SP Key]

    // region [Action]
    public static final String ACTION_REQUEST_CONNECT = LIB_NAME + ".ACTION_REQUEST_CONNECT";
    public static final String ACTION_REQUEST_DISCONNECT = LIB_NAME + ".ACTION_REQUEST_DISCONNECT";
    // endregion [Action]

    // region [BLE Scan]
    public static final long SCAN_PERIOD = 60000L;
    public static final long RESCAN_PERIOD = 12000L;
    public static final long SCAN_REPORT_DELAY = 2000L;
    // endregion [BLE Scan]
}
