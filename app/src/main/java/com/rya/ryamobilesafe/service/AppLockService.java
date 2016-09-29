package com.rya.ryamobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.rya.ryamobilesafe.activity.EnterPsdActivity;
import com.rya.ryamobilesafe.db.dao.AppLockDao;

import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppLockService extends Service {

    private boolean iswatch;
    private AppLockDao mdao;
    private List<String> mPkgList;
    private InnerReceiver innerReceiver;
    private MyContentObserver myContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        iswatch = true;

        startWarchDog();

        //创建InnerReceiver用于接收密码输入页面发来的广播
        // 创捷intent广播过滤器
        // 模拟。
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Android.action...SKIP");
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, intentFilter);

        //获取内容接收者，调用注册内容观察者, 一旦接受到数据库内容更新请求， 重新获取mPkgList集合
        myContentObserver = new MyContentObserver(new Handler());
        this.getContentResolver().registerContentObserver(Uri.parse("content://applock/datachange"), true, myContentObserver);
    }

    private void startWarchDog() {
        Log.i("AppLock", "watchDog Going..");
        mdao = AppLockDao.create(getApplicationContext());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPkgList = mdao.searchAll();
                Log.i("AppLock", mPkgList.toString());
                while (iswatch) {
                    Log.i("AppLock", "watchDog Going..");
                    /*ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningTasks = activityManager.getRunningAppProcesses();
                    ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningTasks.get(0);
                    String packageName = runningAppProcessInfo.processName;*/
                    String packageName = getCurrentAppPackage(getApplicationContext());
                    Log.i("AppLock", packageName);
                    if (mPkgList.contains(packageName)) {
                        // 判断应用包名是否已通过密码验证
                        // if(! mRelievePkg.equals(packageName))
                        // ...
                        Log.i("AppLock", "匹配成功！");
                        Intent intent = new Intent(getApplicationContext(), EnterPsdActivity.class);
                        intent.putExtra("pkgName", packageName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static String getCurrentAppPackage(Context context) {
        String result = "";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT < 21) {
            // 如果没有就用老版本
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
            if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
                result = runningTaskInfos.get(0).topActivity.getPackageName();
            }
        } else {
            List<ActivityManager.RunningAppProcessInfo> runningApp = manager.getRunningAppProcesses();
            if (runningApp != null && runningApp.size() > 0) {
                result = runningApp.get(0).processName;
            }
        }
        if (TextUtils.isEmpty(result)) {
            result = "";
        }
        return result;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iswatch = false;
        //注销广播接收者
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
        }
        //注销内容观察者
        if (myContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(myContentObserver);
        }
    }

    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            // 解除应用检测
            // mRelievePkg = intent.getExtu()
        }
    }

    private class MyContentObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPkgList = mdao.searchAll();
                }
            }).start();
        }
    }
}
