package com.ppcrong.blescanner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.UiThread;

import com.ppcrong.blescanner.ui.BleScannerActivity;
import com.ppcrong.blescanner.utils.Utils;

/**
 * BLE Scanner Helper.
 */

public class BleScannerHelper {

    @UiThread
    public static void show(Context ctx) {

        Intent pageIntent = new Intent();
        pageIntent.setClass(ctx, BleScannerActivity.class);
        Utils.startSafeIntent(ctx, pageIntent);
    }
}
