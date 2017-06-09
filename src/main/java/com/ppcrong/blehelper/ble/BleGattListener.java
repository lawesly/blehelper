package com.ppcrong.blehelper.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * BLE Gatt Listener
 */
public interface BleGattListener {

    // region [Variable]
    int NOT_AVAILABLE = -1;
    // endregion [Variable]

    // region [Function]

    void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

    void onServicesDiscovered(BluetoothGatt gatt, int status);

    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    void onReliableWriteCompleted(BluetoothGatt gatt, int status);

    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

    void onMtuChanged(BluetoothGatt gatt, int mtu, int status);

    // endregion [Function]
}
