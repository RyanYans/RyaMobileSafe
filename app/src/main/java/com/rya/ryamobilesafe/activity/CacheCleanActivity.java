package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class CacheCleanActivity extends Activity {

    private static final int ADD_CACHE_VIEW = 100;
    private static final int SCAN_FINISH = 101;
    private static final int CHECK_APP_CACHE = 102;
    private static final int CLEARALL_FINISH = 103;
    private static final int DEFAULT_CACHE_SIZE = 12288;
    private Button btn_cacheclean;
    private TextView tv_cacheclean_text;
    private ProgressBar pb_cacheclean;
    private LinearLayout ll_cacheclean;
    private PackageManager mPm;
    private PackageManager mPackageManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_CACHE_VIEW:
                    initAddCacheView(msg);
                    break;
                case CHECK_APP_CACHE:
                    String pkgName = (String) msg.obj;
                    tv_cacheclean_text.setText(pkgName);
                    break;
                case SCAN_FINISH:
                    tv_cacheclean_text.setText("扫描完成");
                    break;
                case CLEARALL_FINISH:
                    ll_cacheclean.removeAllViews();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cacheclean);

        initUI();

        initData();

    }

    private void initAddCacheView(Message msg) {
        final AppCacheViewInfo info = (AppCacheViewInfo) msg.obj;
        View view = View.inflate(getApplicationContext(), R.layout.view_cache_item, null);
        ImageView iv_cache_icon = (ImageView) view.findViewById(R.id.iv_cache_icon);
        TextView tv_cache_name = (TextView) view.findViewById(R.id.tv_cache_name);
        TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
        ImageView iv_cache_clean = (ImageView) view.findViewById(R.id.iv_cache_clean);

        iv_cache_icon.setImageDrawable(info.getIcon());
        tv_cache_name.setText(info.getName());
        tv_cache_size.setText(info.getCachaSize());

        ll_cacheclean.addView(view, 0);

        iv_cache_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pkgName = info.getPkgName();

                            /*Uri packageURI = Uri.parse("package:" + pkgName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);*/

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + pkgName));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPackageManager = getPackageManager();
                List<ApplicationInfo> installedApplications = mPackageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
                pb_cacheclean.setMax(installedApplications.size());
                int index = 1;

                for (ApplicationInfo info : installedApplications) {
                    try {
                        Thread.sleep(60 + new Random().nextInt(90));
                        getPackageCache(info);
                        pb_cacheclean.setProgress(index++);

                        Message msg = Message.obtain();
                        msg.what = CHECK_APP_CACHE;
                        msg.obj = info.packageName;
                        mHandler.sendMessage(msg);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessageDelayed(msg, 2000);

            }

        }).start();
    }

    private void getPackageCache(final ApplicationInfo info) {

        mPm = getPackageManager();
        //引入IPackageStatsObserver.stub 类的aidl文件，并实现 onGetStatsCompleted 方法
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {

                Message msg = Message.obtain();
                msg.what = ADD_CACHE_VIEW;
                AppCacheViewInfo viewInfo = new AppCacheViewInfo();
                System.out.println(info.packageName + " >>> " + stats.cacheSize);
                //缓存大小的过程

                if (stats.cacheSize > DEFAULT_CACHE_SIZE) {
                    String cacheSize = Formatter.formatFileSize(getApplicationContext(), stats.cacheSize);
                    viewInfo.setName(info.loadLabel(mPackageManager).toString());
                    viewInfo.setPkgName(info.packageName);
                    viewInfo.setCachaSize(cacheSize);
                    viewInfo.setIcon(info.loadIcon(mPackageManager));

                    msg.obj = viewInfo;
                    mHandler.sendMessage(msg);
                }
            }
        };

        //函数被隐藏，需要通过反射获取方法
//        mPm.getPackageSizeInfo("com.android.browser", mStatsObserver);
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(mPm, info.packageName, mStatsObserver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        btn_cacheclean = (Button) findViewById(R.id.btn_cacheclean);
        tv_cacheclean_text = (TextView) findViewById(R.id.tv_cacheclean_text);
        pb_cacheclean = (ProgressBar) findViewById(R.id.pb_cacheclean);
        ll_cacheclean = (LinearLayout) findViewById(R.id.ll_cacheclean);

        // 无效，清除所有应用缓存 系统将freeStorageAndNotify已修改为SystemApi方法，无法反射调用
        btn_cacheclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPm = getPackageManager();

                try {
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                            Message msg = Message.obtain();
                            msg.what = CLEARALL_FINISH;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class AppCacheViewInfo {
        private String name;
        private String pkgName;
        private Drawable icon;
        private String cachaSize;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPkgName() {
            return pkgName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getCachaSize() {
            return cachaSize;
        }

        public void setCachaSize(String cachaSize) {
            this.cachaSize = cachaSize;
        }
    }
}
