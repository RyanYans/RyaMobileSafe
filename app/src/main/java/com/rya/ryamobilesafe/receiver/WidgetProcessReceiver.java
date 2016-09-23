package com.rya.ryamobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rya.ryamobilesafe.engin.ProcessProvider;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class WidgetProcessReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessProvider.killProcessAll(context);
    }
}
