package com.vovo.netmoneytask.controller;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.vovo.Constant;
import com.vovo.DBConstant;
import com.vovo.handler.AppUtils;
import com.vovo.handler.HttpUtils;
import com.vovo.handler.NotesContract;
import com.vovo.netmoneytask.R;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommonController {

    private String checkUpdate = "http://vovo.charick.com/api/netMoneyTask/check?type=update";

    private Context context;

    private TextView footerTextView;

    private ContentResolver contentResolver;

    Handler handler = new Handler(Looper.getMainLooper());

    // 构造函数，传入 Activity 作为上下文以及监听器
    public CommonController(Context context, AppCompatActivity activity) {
        this.context = context;
        footerTextView = activity.findViewById(R.id.footerTextView);
        footerTextView.setOnClickListener(v -> footerTextViewClick());

        Constant.androidId = AppUtils.getDeviceId(context);

        // 插入 androidId
        contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("_id", DBConstant.netMoneyTask_androidId_index);
        values.put("text", Constant.androidId);
        Uri uri = contentResolver.insert(NotesContract.CONTENT_URI, values);
        if (uri != null) {
            Log.d("CommonController", "Inserted note with URI: " + uri);
        }


        // 查询 androidId
        Cursor cursor = contentResolver.query(NotesContract.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int key = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex("text"));
                if (key == 1) {
                    Constant.androidIdProvider = text;
                    break;
                }
            }
            cursor.close();
        }


    }

    private void footerTextViewClick() {
        String toastInfo = "androidId=" + Constant.androidId + " \n"
                + "androidId_p=" + Constant.androidIdProvider + " \n"
                + "appVersion=" + Constant.version + " \n"
                + "dyDownlad=" + Constant.dyApkDownloadUrl + " \n";

        new AlertDialog.Builder(context)
                .setTitle("app info")
                .setMessage(toastInfo)
                .setPositiveButton("确认", (dialog, which) -> {
                })
                .setNegativeButton("取消", null)
                .show();
    }


    public void checkUpdate() {
        new Thread(() -> {
            try {
                String checkInfo = HttpUtils.requestGetAPI(checkUpdate);
                JSONObject result = new JSONObject(checkInfo);
                String newVersion = result.getString("version");
                Constant.apkDownloadUrl = result.getString("apkDownloadUrl");

                if (!Constant.version.equalsIgnoreCase(newVersion)) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context)
                                    .setTitle("应用升级")
                                    .setMessage("请立刻升级最新app，否则影响收益")
                                    .setPositiveButton("确认", (dialog, which) -> {
                                        // 执行卸载应用和安装 APK 的操作
                                        // 在下载完成后关闭进度条
                                        downloadAndInstallAPK(Constant.apkDownloadUrl);
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                    });

                }
            } catch (Exception e) {
                Log.e("CommonController", "init 时出错", e);
            }
        }).start();
    }

    // 下载 APK 并自动安装
    private void downloadAndInstallAPK(String urlString) {
        // 创建一个自定义的加载框
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在下载更新...");
        progressDialog.setCancelable(false); // 让进度条不能关闭
        progressDialog.show();

        new Thread(() -> {
            File apkFile;
            try {
                HttpUtils.disableCertificateChecking();
                // 下载 APK 文件
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                InputStream inputStream = connection.getInputStream();
                apkFile = new File(context.getExternalFilesDir(null), "netMoney_downloaded.apk");
                FileOutputStream fileOutputStream = new FileOutputStream(apkFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }

                fileOutputStream.close();
                inputStream.close();


                AppUtils.installAPK(context, apkFile);

                progressDialog.setMessage("应用安装中...");
                Thread.sleep(3000);
                progressDialog.dismiss();

            } catch (Exception e) {
                Log.e("CommonController", "下载或安装 APK 时出错", e);
                progressDialog.dismiss();
                Toast.makeText(context, "下载失败，请退出应用重试", Toast.LENGTH_SHORT).show();
                return;
            }

        }).start();
    }


}

