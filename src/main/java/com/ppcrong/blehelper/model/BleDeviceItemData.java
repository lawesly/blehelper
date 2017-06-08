package com.ppcrong.blehelper.model;

import android.bluetooth.BluetoothDevice;

/**
 * BleDeviceItemData.
 */

public class BleDeviceItemData {
    private BluetoothDevice mDevice;
    private int mBatteryPercentage = -1;
    private boolean mIsConnecting;
    private int mCscType;

    public BleDeviceItemData() {

    }

    public BleDeviceItemData(BluetoothDevice device) {
        this.mDevice = device;
    }

    public BleDeviceItemData(BluetoothDevice device, boolean isConnecting) {
        this.mIsConnecting = isConnecting;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getBatteryPercentage() {
        return mBatteryPercentage;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        mBatteryPercentage = batteryPercentage;
    }
    public boolean getIsConnecting() {
        return mIsConnecting;
    }

    public void setIsConnecting(boolean isConnecting) {
        mIsConnecting = isConnecting;
    }

    public int getCscType() {
        return mCscType;
    }

    public void setCscType(int type) {
        mCscType = type;
    }
}
