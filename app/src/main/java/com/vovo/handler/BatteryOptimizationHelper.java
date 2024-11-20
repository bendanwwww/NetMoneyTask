package com.vovo.handler;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.PowerManager;

public class BatteryOptimizationHelper {

    public static boolean isBatteryOptimized(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true; // Android 6.0 以下默认返回 true（无电池优化限制）
    }

}
