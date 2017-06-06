package com.ppcrong.blescanner.ble;

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

        if (BleUtils.isBluetoothEnable(mBuilder.mContext)) {

            BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.startScan(mBuilder.mScanFilters, mBuilder.mScanSettings, mBuilder.mScanCb);
        }
    }

    public void stopScan() {

        if (BleUtils.isBluetoothEnable(mBuilder.mContext)) {

            BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(mBuilder.mScanCb);
        }
    }

    // endregion [Public Function]

    // region [Private Function]
    // endregion [Private Function]

}
