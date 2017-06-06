package com.ppcrong.blehelper;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

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
    private SPUtils mScanSp;
    // endregion [Variable]

    // region [Ctor]
    /**
     * Ctor
     * @param builder The Builder
     */
    @SuppressLint("InflateParams")
    protected BleHelper(@NonNull Builder builder) {
        this.mBuilder = builder;
        mScanSp = new SPUtils(mBuilder.mContext, Constant.SP_NAME_SCAN_SETTINGS);
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

    public void init() {
        String pairAddress = mScanSp.getString(Constant.DEVICE_PAIR_ADDRESS, "");
        boolean pairEnabled = mScanSp.getBoolean(Constant.DEVICE_PAIR_ENABLE, false);
        boolean autoConnect = mScanSp.getBoolean(Constant.DEVICE_AUTO_CONNECT, false);
        KLog.d("pairAddress: " + pairAddress + ", pairEnabled: " + pairEnabled + ", autoConnect: " + autoConnect);
        if (!pairAddress.isEmpty() && pairEnabled) {
            mGatt = BleUtils.connectGatt(mBuilder.mContext, mBuilder.mBleGattCb, pairAddress, autoConnect);
        }
    }

    public BluetoothGatt getBleGatt() {
        return mGatt;
    }
    // endregion [Public Function]
}
