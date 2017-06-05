package com.ppcrong.blescanner.utils;

import android.content.Context;
import android.content.Intent;

import com.socks.library.KLog;

/**
 * Utils
 */
public class Utils {
    /**
     * Start an intent with try-catch to avoid activity not found
     *
     * @param context
     * @param intent
     */
    public static void startSafeIntent(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            KLog.d("[startSafeIntent1] start intent error...");
            e.printStackTrace();
        }
    }

    /**
     * Start an intent with try-catch to avoid activity not found
     *
     * @param context
     * @param action
     */
    public static void startSafeIntent(Context context, String action) {
        try {
            Intent intent = new Intent(action);
            context.startActivity(intent);
        } catch (Exception e) {
            KLog.d("[startSafeIntent2] start intent error...");
            e.printStackTrace();
        }
    }

    /**
     * Convert integer to bit array string
     *
     * @param num integer number
     * @return bit array string
     */
    public static String getIntToBitArrayString(int num) {
        return String.format("%8s", Integer.toBinaryString(num & 0xFF)).replace(' ', '0');
    }

    /**
     * Convert byte array to hex string
     * <br/>
     * ex: 0x1C, 0x2B, 0x3D, 0x4A => 1C:2B:3D:4A
     *
     * @param bytes           The byte array to convert
     * @param separator       The separator between 2 bytes
     * @param appendSeparator true to append separator, false not append
     * @return The string value
     */
    public static String getByteToHexString(byte[] bytes, String separator, boolean appendSeparator) {
        KLog.d("length = " + bytes.length);

        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {

            if (sb.length() > 0 && appendSeparator) sb.append(separator);
            sb.append(String.format("%02X", b));
        }

        String str = sb.toString();
        KLog.d(str);
        return str;
    }
}
