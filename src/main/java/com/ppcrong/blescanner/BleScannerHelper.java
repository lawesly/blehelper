package com.ppcrong.blescanner;

import android.content.Context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import no.nordicsemi.android.support.v18.scanner.ScanFilter;

/**
 * BleScannerHelper
 */
public class BleScannerHelper {

    // region [Variable]
    private ConcurrentHashMap<String, Object> mMap = new ConcurrentHashMap<>();
    private Context mContext;
    private CopyOnWriteArrayList<ScanFilter> mScanFilters = new CopyOnWriteArrayList<>();
    // endregion [Variable]

    /**
     * Ctor
     * @param builder The Builder
     */
    private BleScannerHelper(Builder builder) {
        this.mContext = builder.mContext;
        this.mScanFilters = builder.mScanFilters;
    }

    // region [Private Function]
    // endregion [Private Function]

    // region [Builder]
    public static class Builder {

        private Context mContext;
        private CopyOnWriteArrayList<ScanFilter> mScanFilters = new CopyOnWriteArrayList<>();

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder addScanFilters(ScanFilter filter) {
            mScanFilters.add(filter);
            return this;
        }
    }
    // endregion [Builder]
}
