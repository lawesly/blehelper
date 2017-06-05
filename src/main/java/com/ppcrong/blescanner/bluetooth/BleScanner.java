package com.ppcrong.blescanner.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.socks.library.KLog;

import java.util.concurrent.CopyOnWriteArrayList;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * BleScanner
 */
public class BleScanner {

    // region [Variable]
    protected final Builder mBuilder;
    // endregion [Variable]

    // region [Ctor]
    /**
     * Ctor
     * @param builder The Builder
     */
    @SuppressLint("InflateParams")
    protected BleScanner(@NonNull Builder builder) {
        this.mBuilder = builder;
    }
    // endregion [Ctor]

    // region [Builder]
    /**
     * The class used to construct a BleScanner.
     */
    public static class Builder {

        protected Context mContext;
        protected CopyOnWriteArrayList<ScanFilter> mScanFilters = new CopyOnWriteArrayList<>();
        protected ScanSettings mScanSettings;
        protected ScanCallback mScanCb;

        public Builder(@NonNull Context context) {
            this.mContext = context;
        }

        public BleScanner build() {
            return new BleScanner(this);
        }

        public Builder addScanFilter(@NonNull ScanFilter filter) {
            mScanFilters.add(filter);
            return this;
        }

        public Builder setScanSettings(@NonNull ScanSettings settings) {
            mScanSettings = settings;
            return this;
        }

        public Builder setScanCb(@NonNull ScanCallback scanCb) {
            mScanCb = scanCb;
            return this;
        }
    }
    // endregion [Builder]

    // region [Public Function]
    public void startScan() {

        if (isBluetoothEnable(mBuilder.mContext)) {

            BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.startScan(mBuilder.mScanFilters, mBuilder.mScanSettings, mBuilder.mScanCb);
        }
    }

    public void stopScan() {

        if (isBluetoothEnable(mBuilder.mContext)) {

            BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(mBuilder.mScanCb);
        }
    }

    // endregion [Public Function]

    // region [Private Function]
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
    // endregion [Private Function]

}
