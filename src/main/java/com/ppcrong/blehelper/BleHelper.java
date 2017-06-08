package com.ppcrong.blehelper;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.ppcrong.blehelper.ble.BleGattCb;
import com.ppcrong.blehelper.ble.BleUtils;
import com.ppcrong.blehelper.ui.BleScannerActivity;
import com.ppcrong.blehelper.utils.Constant;
import com.ppcrong.blehelper.utils.SPUtils;
import com.ppcrong.blehelper.utils.Utils;
import com.socks.library.KLog;

/**
 * BLE Helper
 */

public class BleHelper {

    // region [Variable]
    protected final Builder mBuilder;
    private BluetoothGatt mGatt;
    private boolean mManualDisconnect;
    // endregion [Variable]

    // region [Ctor]
    /**
     * Ctor
     * @param builder The Builder
     */
    @SuppressLint("InflateParams")
    protected BleHelper(@NonNull Builder builder) {
        this.mBuilder = builder;
    }
    // endregion [Ctor]

    // region [Builder]
    /**
     * The class used to construct a BleScanner.
     */
    public static class Builder {

        protected Context mContext;
        protected BluetoothGattCallback mBleGattCb;

        public Builder(@NonNull Context context) {
            this.mContext = context;
        }

        public BleHelper build() {
            return new BleHelper(this);
        }

        public Builder setBleGattCb(@NonNull BluetoothGattCallback bleGattCb) {
            mBleGattCb = bleGattCb;
            return this;
        }
    }
    // endregion [Builder]

    // region [Public Function]
    @UiThread
    public void showScanner() {

        Intent pageIntent = new Intent();
        pageIntent.setClass(mBuilder.mContext, BleScannerActivity.class);
        Utils.startSafeIntent(mBuilder.mContext, pageIntent);
    }

    // Get Gatt when activity is shown
    public BluetoothGatt initConnection() {
        String pairAddress = getPairAddress(mBuilder.mContext);
        boolean pairEnabled = getIsPairEnabled(mBuilder.mContext);
        boolean isNeedConnected = getIsConnected(mBuilder.mContext);
        boolean isDeviceConnected = isDeviceConnected();
        boolean autoConnect = getIsAutoConnect(mBuilder.mContext);
        KLog.d("pairAddress: " + pairAddress + ", pairEnabled: " + pairEnabled
                + ", isNeedConnected: " + isNeedConnected + ", isDeviceConnected: " + isDeviceConnected
                + ", autoConnect: " + autoConnect);

        if (isDeviceConnected) { // Now device is connected
            if (!isNeedConnected) {
                // If device isn't connected in Scanner page, we need to make other page status the same as Scanner page, so we disconnect device here
                mGatt.disconnect();
                mGatt = null;
            }
        } else {
            // Directly connectGatt when pairEnabled or device is connected
            if (!pairAddress.isEmpty() && (pairEnabled || isNeedConnected)) {
                mGatt = BleUtils.connectGatt(mBuilder.mContext, mBuilder.mBleGattCb, pairAddress, autoConnect);
            }
        }

        return mGatt;
    }

    // Get Gatt when tap available list to connect
    public BluetoothGatt connectGatt(BluetoothDevice device) {

        setPairAddress(mBuilder.mContext, device.getAddress()); // Save pair device address
        boolean autoConnect = getIsAutoConnect(mBuilder.mContext);
        KLog.i("Connecting to " + device.getName() + " Address: " + device.getAddress() + " AutoConnect: " + autoConnect);

        mGatt = device.connectGatt(mBuilder.mContext, autoConnect, mBuilder.mBleGattCb);
        return mGatt;
    }

    public boolean isDeviceConnected() {
        if (mBuilder.mBleGattCb instanceof BleGattCb) {
            return ((BleGattCb) mBuilder.mBleGattCb).isConnected();
        }
        return false;
    }

    public boolean isManualDisconnect() {
        KLog.d("mManualDisconnect: " + mManualDisconnect);
        return mManualDisconnect;
    }

    public void setManualDisconnect(boolean manualDisconnect) {
        mManualDisconnect = manualDisconnect;
    }
    // region [static]
    public static String getPairAddress(Context ctx) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        return sp.getString(Constant.DEVICE_PAIR_ADDRESS, "");
    }

    public static void setPairAddress(Context ctx, String pairAddress) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        sp.put(Constant.DEVICE_PAIR_ADDRESS, pairAddress);
    }

    public static boolean getIsPairEnabled(Context ctx) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        return sp.getBoolean(Constant.DEVICE_PAIR_ENABLE, false);
    }

    public static void setIsPairEnabled(Context ctx, boolean isPairEnabled) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        sp.put(Constant.DEVICE_PAIR_ENABLE, isPairEnabled);
    }

    public static boolean getIsAutoConnect(Context ctx) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        return sp.getBoolean(Constant.DEVICE_AUTO_CONNECT, false);
    }

    public static void setIsAutoConnect(Context ctx, boolean isAutoConnect) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        sp.put(Constant.DEVICE_AUTO_CONNECT, isAutoConnect);
    }

    public static boolean getIsConnected(Context ctx) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        return sp.getBoolean(Constant.DEVICE_IS_CONNECTED, false);
    }

    public static void setIsConnected(Context ctx, boolean isConnected) {
        SPUtils sp = new SPUtils(ctx, Constant.SP_NAME_SCAN_SETTINGS);
        sp.put(Constant.DEVICE_IS_CONNECTED, isConnected);
    }
    // endregion [static]

    // endregion [Public Function]
}
