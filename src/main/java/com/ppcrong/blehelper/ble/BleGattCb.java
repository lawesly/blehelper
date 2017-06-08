package com.ppcrong.blehelper.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.ppcrong.blehelper.BleHelper;
import com.socks.library.KLog;

import java.util.List;

/**
 * BLE Gatt Callback
 */
public class BleGattCb extends BluetoothGattCallback {

    // region [Variable]
    public BluetoothDevice mDevice;
    private int mConnectState;
    private Context mContext;
    // endregion [Variable]

    // region [Ctor]
    /**
     * Ctor
     */
    public BleGattCb(Context ctx) {

        mContext = ctx;
        mConnectState = BluetoothProfile.STATE_DISCONNECTED;
    }
    // endregion [Ctor]

    // region [Override]
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        KLog.i("oldStatus : " + status + ", newState : " + getConnectString(newState) + "(" + newState + ")"
                + ", device : " + gatt.getDevice().getAddress());

        mDevice = gatt.getDevice();

        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {

            mConnectState = BluetoothProfile.STATE_CONNECTED;

            // Discover Services before read/write characteristics
            gatt.discoverServices();

        } else {

            // Reset variables
            mConnectState = BluetoothProfile.STATE_DISCONNECTED;
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
    public boolean isConnected() {
        return mConnectState == BluetoothProfile.STATE_CONNECTED;
    }
    // endregion [Public Function]

    // region [Private Function]

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
