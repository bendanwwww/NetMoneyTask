package com.vovo.handler;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * @Auther: liuzeheng@zhihu.com
 * @Date: 2024/11/15
 * @Description:
 */
public class AppUtils {

    // 检测抖音是否安装
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            // 尝试获取抖音应用的信息
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return packageInfo != null;  // 如果能找到抖音应用，返回 true
        } catch (PackageManager.NameNotFoundException e) {
            // 如果抖音未安装，抛出异常
            return false;
        }
    }

    public static String getDeviceId(Context context) {
//        return Build.SERIAL;
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


//    public static boolean isAppRunning(Context context, String packageName) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (activityManager == null) return false;
//
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//        if (appProcesses == null) return false;
//
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            if (appProcess.processName.equals(packageName)) {
//                // 如果包名匹配，说明应用正在运行
//                return true;
//            }
//        }
//        return false;
//    }


    // 安装 APK
    public static void installAPK(Context context, File apkFile) {
        // 使用 Intent 启动 APK 安装流程
        // 如果使用的是 Android 7.0 及以上版本，需要用 FileProvider 来获取文件 Uri
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Uri fileUri = FileProvider.getUriForFile(context, "com.vovo.netmoneytask.fileprovider", apkFile);
//            Uri fileUri = Uri.fromFile(apkFile);
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(installIntent);
        }, 1000); // Wait 10 seconds before attempting install
    }

}
