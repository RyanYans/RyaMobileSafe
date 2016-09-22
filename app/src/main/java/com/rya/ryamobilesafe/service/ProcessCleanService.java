package com.rya.ryamobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rya.ryamobilesafe.engin.ProcessProvider;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ProcessCleanService extends Service {

    private IntentFilter intentFilter;
    private ProcessCleanService.innerBroadcast innerBroadcast;

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerBroadcast = new innerBroadcast();
        registerReceiver(innerBroadcast, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (innerBroadcast != null) {
            unregisterReceiver(innerBroadcast);
        }
    }

    private class innerBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessProvider.killProcessAll(getApplicationContext());
        }
    }
}
