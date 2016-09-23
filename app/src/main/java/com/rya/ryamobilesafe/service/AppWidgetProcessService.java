package com.rya.ryamobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.engin.ProcessProvider;
import com.rya.ryamobilesafe.receiver.MyAppWidgetReceiver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppWidgetProcessService extends Service {

    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        startTimer();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        InnerReceiver innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, intentFilter);
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAppWidget();
            }
        }, 100, 5000);
    }


    private void updateAppWidget() {
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        int runningProcess = ProcessProvider.getRunningProcess(this);
        long availProcessSpace = ProcessProvider.getAvailProcessSpace(this);
        String avail = Formatter.formatFileSize(this, availProcessSpace);
        remoteViews.setTextViewText(R.id.tv_widgetprocess_count, "应用总数：" + runningProcess);
        remoteViews.setTextViewText(R.id.tv_widgetprocess_memory, "内存剩余：" + avail);

        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_widget_root, pendingIntent);

        Intent broadCastIntent = new Intent("android.appwidget.action.WIDGET_PROCESS_CLEAR");
        PendingIntent broadcastPending = PendingIntent.getBroadcast(getApplicationContext(), 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcastPending);

        ComponentName componentName = new ComponentName(this, MyAppWidgetReceiver.class);
        aWM.updateAppWidget(componentName, remoteViews);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    //开启定时
                    startTimer();
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    //关闭定时
                    cancelTimer();
                    break;
            }
        }
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
