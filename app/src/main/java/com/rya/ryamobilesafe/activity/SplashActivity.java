package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.StreamUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private static final int ALARMVERSION = 100;
    private static final int HOMEPAGE = 101;
    private static final int URLEXCEPTION = 401;
    private static final int IOEXCEPTION = 402;
    private static final int JSONEXCEPTION = 403;
    private TextView tv_versionName;
    private int mLocalVersionCode;
    private String mVersionName;
    private String mVersionUrl;
    private static final String TAG = "SplashActivity >>";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALARMVERSION:
                    alarmDialog();
                    break;
                case HOMEPAGE:
                    goHomePage();
                    break;
                case URLEXCEPTION:
                    ToastUtil.show(getApplicationContext(), "URL异常");
                    goHomePage();
                    break;
                case IOEXCEPTION:
                    ToastUtil.show(getApplicationContext(), "IO异常");
                    goHomePage();
                    break;
                case JSONEXCEPTION:
                    ToastUtil.show(getApplicationContext(), "JSON异常");
                    goHomePage();
                    break;
            }
        }
    };
    private RelativeLayout rl_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //初始化UI
        initUI();

        //初始化信息
        initData();

        //splash界面开启渐变动画效果
        startAnimation();

        //初始化数据库信息
        initDB();

        //创建快捷方式
        if (!SPUtil.getBoolean(getApplicationContext(), ConstantValues.ISSHORTCUP, false)) {
            initShortcup();
        }
    }

    private void initShortcup() {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        //维护快捷方式图标
        Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.head2);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
        //维护快捷方式名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Li管家");
        //快捷方式开启定向
        Intent shortcupIntent = new Intent("android.intent.action.HOME");
        shortcupIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcupIntent);

        sendBroadcast(intent);
        SPUtil.putBoolean(getApplicationContext(), ConstantValues.ISSHORTCUP, true);
    }

    private void initDB() {
        initAddressDB("address.db");

        initAddressDB("commonnum.db");
    }

    private void initAddressDB(String dbName) {
        String path = getFilesDir().toString().trim();
        File file = new File(path, dbName);
        //判断文件是否存在，不能用 ！null判断！！
        if (file.exists()) {
            return;
        }
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = getAssets().open(dbName);
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            System.out.println("Write it now!");
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null && fileOutputStream != null) {
                try {
                    inputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(4000);
        rl_root.startAnimation(alphaAnimation);
    }

    /**
     * 初始化信息，在splash界面显示版本信息
     */
    private void initData() {
        String versionText = "版本号：" + getVersionName();
        tv_versionName.setText(versionText);
        //获取本地版本号 (检测更新，本地版本号 服务器版本号)
        mLocalVersionCode = getVersionCode();
        //获取服务器版本号（客户端发请求，服务器相应(json、xml))
        if (SPUtil.getBoolean(getApplicationContext(), ConstantValues.ISUPDATE, false)) {
            checkVersion();
        } else {
            Message msg = new Message();
            msg.what = HOMEPAGE;
            mHandler.sendMessageDelayed(msg, 4000);
        }
    }

    /**
     * 弹出更新对话框
     * builder.setIcon
     * builder.setTitle
     * builder.setMessage
     * builder.setPositiveButton
     * builder.setNegativeButton
     */
    private void alarmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.head2);
        builder.setTitle("有版本更新！");
        builder.setMessage(mVersionName);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载文件
                downloadAPK();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goHomePage();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                goHomePage();
                //dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 通过开源框架 Xutils - Http下载apk文件
     */
    private void downloadAPK() {
        String path = getExternalFilesDir(null).getPath().trim() + File.separator + "RyaMobileSafe.apk";
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(mVersionUrl, path, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载成功
                Log.i(TAG, "下载成功！");
                File file = responseInfo.result;
                InstallAPK(file);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //下载失败
                Log.i(TAG, "下载失败！");
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.i(TAG, "开始下载！..");
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                Log.i(TAG, "DownLoding... ");
                Log.i(TAG, "total: " + total);
                Log.i(TAG, "current: " + current);
            }
        });
    }

    /**
     * 隐式意图 跳转安装界面
     *
     * @param file APK文件
     */
    private void InstallAPK(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    /**
     * StartActivityForResult 返回/回掉 接收函数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            goHomePage();
        }
    }

    /**
     * 跳转到主界面
     */
    private void goHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 初始化UI方法     Alt +Shift +F
     */
    private void initUI() {
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);

        tv_versionName = (TextView) findViewById(R.id.tv_version_name);

    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        final Message mesg = new Message();
        final long startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1,封装url地址
                    URL url = new URL("http://192.168.0.13:8080/update.json");
                    //2，开启一个链接
                    HttpURLConnection Connection = (HttpURLConnection) url.openConnection();
                    //3,设置请求头

                    /* 请求超时 */
                    Connection.setConnectTimeout(5000);
                    /* 读取超时 */
                    Connection.setReadTimeout(5000);
                    /* Get方式 */
                    Connection.setRequestMethod("GET");

                    if (Connection.getResponseCode() == 200) {
                        InputStream is = Connection.getInputStream();
                        String json = StreamUtil.stream2String(is);
                        //json解析
                        JSONObject jsonObject = new JSONObject(json);
                        String version_name = jsonObject.getString("version_name");
                        String version_code = jsonObject.getString("version_code");
                        mVersionName = jsonObject.getString("version_desc");
                        mVersionUrl = jsonObject.getString("version_url");
                        Log.i(TAG, version_name);
                        Log.i(TAG, version_code);
                        Log.i(TAG, mVersionName);
                        Log.i(TAG, mVersionUrl);

                        //检测版本更新
                        int remoteVersion = Integer.parseInt(version_code);
                        if (mLocalVersionCode < remoteVersion) {
                            // 新版本提示
                            mesg.what = ALARMVERSION;
                        } else {
                            mesg.what = HOMEPAGE;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    mesg.what = URLEXCEPTION;
                } catch (IOException e) {
                    e.printStackTrace();
                    mesg.what = IOEXCEPTION;
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.what = JSONEXCEPTION;
                } finally {
                    //splash界面等待4s 转向主界面
                    long endTime = System.currentTimeMillis();
                    long spendTime = endTime - startTime;
                    if (spendTime < 4000) {
                        try {
                            Thread.sleep(4000 - spendTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(mesg);
                }
            }
        }).start();
    }

    /**
     * 获取版本名称：Build.gradle
     *
     * @return versionName
     */
    public String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取版本名称：Build.gradle
     *
     * @return versionName
     */
    public int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
