package com.ppcrong.blehelper.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.socks.library.KLog;

import java.util.UUID;

/**
 * BLE utilities
 */

public class BleUtils {

    // region [Check]
    /**
     * Check if bluetooth is enabled or not
     *
     * @param context The context
     * @return true for enable, false for disable
     */
    public static boolean isBluetoothEnable(Context context) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        boolean b = false;
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            b = true;
        }
        KLog.d("isBluetoothEnable = " + b);
        return b;
    }

    /**
     * Check if device supports BLE or not
     *
     * @param context        The context
     * @param id_not_support The string id to show not support string
     */
    public static void isBleSupport(Context context, int id_not_support) {
        boolean b = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        KLog.d("isBleSupport = " + b);
        if (!b) {
            Toast.makeText(context, id_not_support, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if device supports BLE or not
     *
     * @param activity       The activity
     * @param id_not_support The string id to show not support string
     */
    public static void isBleSupport(Activity activity, int id_not_support) {
        boolean b = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        KLog.d("isBleSupport = " + b);
        if (!b) {
            Toast.makeText(activity, id_not_support, Toast.LENGTH_SHORT).show();
            KLog.d("Finish activity");
            activity.finish();
        }
    }
    // endregion [Check]

    // region [Set BLE Properties]
    /**
     * Set connection priority
     *
     * @param gatt               The gatt
     * @param connectionPriority The priority
     */
    public static void setConnectionPriority(BluetoothGatt gatt, int connectionPriority) {
        boolean b = false;
        KLog.i("Priority : " + connectionPriority);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            b = gatt.requestConnectionPriority(connectionPriority);
        }
        KLog.i("requestConnectionPriority return : " + b);
    }

    /**
     * Set MTU
     *
     * @param gatt The gatt
     * @param mtu  The mtu value
     */
    public static void setMTU(BluetoothGatt gatt, int mtu) {
        boolean b = false;
        KLog.i("MTU : " + mtu);
        if (gatt != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            b = gatt.requestMtu(mtu);
        }
        KLog.i("requestMtu return : " + b);
    }
    // endregion [Set BLE Properties]

    // region [Common]
    /**
     * Read characteristic from device.
     *
     * @param gatt     The gatt
     * @param uuidSvc  The service UUID
     * @param uuidChar The characteristic UUID
     * @return True OK, False FAIL
     */
    public static boolean readCharacteristic(BluetoothGatt gatt, UUID uuidSvc, UUID uuidChar) {

        KLog.d();

        boolean b = false;

        if (gatt == null) {
            KLog.d("gatt is null...");
            return b;
        }

        BluetoothGattService service = gatt.getService(uuidSvc);

        if (service != null) {

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidChar);

            if (characteristic != null) {

                // Check characteristic properties
                int properties = characteristic.getProperties();
                KLog.d(String.format("properties = 0x%02X", properties));

                // Read
                b = gatt.readCharacteristic(characteristic);
                KLog.d("readCharacteristic " + (b ? "OK" : "FAIL"));
            }
        }

        return b;
    }

    /**
     * Write characteristic to device.
     *
     * @param gatt     The gatt
     * @param uuidSvc  The service UUID
     * @param uuidChar The characteristic UUID
     * @param bytes    The data
     * @return True OK, False FAIL
     */
    public static boolean writeCharacteristic(BluetoothGatt gatt, UUID uuidSvc, UUID uuidChar, byte[] bytes) {

        KLog.d();

        boolean b = false;

        if (gatt == null) {
            KLog.d("gatt is null...");
            return b;
        }

        BluetoothGattService service = gatt.getService(uuidSvc);

        if (service != null) {

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidChar);

            if (characteristic != null) {

                // Check characteristic properties
                int properties = characteristic.getProperties();
                KLog.d(String.format("properties = 0x%02X", properties));

                // Write
                characteristic.setValue(bytes);
                b = gatt.writeCharacteristic(characteristic);
                KLog.d("writeCharacteristic " + (b ? "OK" : "FAIL"));
            }
        }

        return b;
    }

    /**
     * Write descriptor to device.
     *
     * @param gatt     The gatt
     * @param uuidSvc  The service UUID
     * @param uuidChar The characteristic UUID
     * @param enable   Enable flag
     * @return True OKs, False FAIL
     */
    public static boolean writeDescriptor(BluetoothGatt gatt, UUID uuidSvc, UUID uuidChar, boolean enable) {

        KLog.d();

        boolean b = false;

        if (gatt == null) {
            KLog.d("gatt is null...");
            return b;
        }

        BluetoothGattService service = gatt.getService(uuidSvc);

        if (service != null) {

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidChar);

            if (characteristic != null) {

                gatt.setCharacteristicNotification(characteristic, enable);

                BluetoothGattDescriptor desc = characteristic.getDescriptor(UUIDs.CLIENT_CHARACTERISTIC_CONFIGURATION);
                if (desc == null) {
                    KLog.d("descriptor is null...");
                    return b;
                }

                desc.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                b = gatt.writeDescriptor(desc);
                KLog.d("writeDescriptor " + (b ? "OK" : "FAIL"));
            }
        }

        return b;
    }
    // endregion [Common]

    // region [Connection]
    /**
     * Connect BLE device by MAC address directly
     *
     * @param ctx The context
     * @param cb  The callback
     * @return The gatt
     */
    public static BluetoothGatt connectGatt(Context ctx, BluetoothGattCallback cb, String pairAddress, boolean autoConnect) {
        BluetoothGatt gatt = null;

        // Connect
        if (!pairAddress.isEmpty()) {
            BluetoothManager bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(pairAddress);
            gatt = device.connectGatt(ctx, autoConnect, cb);
            KLog.d("########## CONNECT BY MAC DIRECTLY.....");
        }

        return gatt;
    }
    // endregion [Connection]
}
