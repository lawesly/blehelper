package com.ppcrong.blescanner.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.socks.library.KLog;

import java.util.List;

/**
 * BLE Gatt Callback
 */
public class BleGattCb extends BluetoothGattCallback {

    // region [Variable]
    public BluetoothDevice mDevice;
    public int mConnectState;
    // endregion [Variable]

    // region [Ctor]
    /**
     * Ctor
     */
    public BleGattCb() {

        init();
    }
    // endregion [Ctor]

    // region [Override]
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        KLog.i("oldStatus : " + status + " newState : " + getConnectString(newState) + "(" + newState + ")");

        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {

            mDevice = gatt.getDevice();

            mConnectState = BluetoothProfile.STATE_CONNECTED;

            gatt.discoverServices();

        } else {

            mConnectState = BluetoothProfile.STATE_DISCONNECTED;

            init();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        KLog.i("status : " + status);

        if (status == BluetoothGatt.GATT_SUCCESS) {

            // Print log for all service/characteristic/descriptor
            List<BluetoothGattService> listSvc = gatt.getServices();
            for (BluetoothGattService s : listSvc) {
                KLog.d("Service = " + s.getUuid());
                List<BluetoothGattCharacteristic> listCharacteristic = s.getCharacteristics();
                for (BluetoothGattCharacteristic c : listCharacteristic) {
                    KLog.d("Characteristic = " + c.getUuid());
                    List<BluetoothGattDescriptor> listDescriptor = c.getDescriptors();
                    for (BluetoothGattDescriptor d : listDescriptor) {
                        KLog.d("Descriptor =             " + d.getUuid());
                    }
                }
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        KLog.d("status = " + status + " UUID : " + characteristic.getUuid());

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        KLog.d("status = " + status + " UUID : " + characteristic.getUuid());

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        KLog.i("UUID : " + characteristic.getUuid());

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        KLog.d("status = " + status + " UUID : " + descriptor.getUuid());

    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        KLog.d("status = " + status + " UUID : " + descriptor.getUuid());

    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        KLog.i("status : " + status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        KLog.i("rssi : " + rssi + "dBm, status : " + status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        KLog.i("mtu : " + mtu + " status : " + status);
    }
    // endregion [Override]

    // region [Public Function]
    // endregion [Public Function]

    // region [Private Function]
    private void init() {

        mDevice = null;
        mConnectState = BluetoothProfile.STATE_DISCONNECTED;
    }

    private String getConnectString(int state) {
        String s = "";
        switch (state) {
            case (BluetoothProfile.STATE_DISCONNECTED):
                s = "Disconnected";
                break;
            case (BluetoothProfile.STATE_CONNECTING):
                s = "Connecting";
                break;
            case (BluetoothProfile.STATE_CONNECTED):
                s = "Connected";
                break;
            case (BluetoothProfile.STATE_DISCONNECTING):
                s = "Disconnecting";
                break;
            default:
                break;
        }
        return s;
    }
    // endregion [Private Function]

}
