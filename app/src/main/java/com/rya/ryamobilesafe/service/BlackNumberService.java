package com.rya.ryamobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.internal.telephony.ITelephony;
import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.activity.SmogActivity;
import com.rya.ryamobilesafe.db.dao.BlackNumberDao;
import com.rya.ryamobilesafe.db.domain.BlackNumber;
import com.rya.ryamobilesafe.utils.ToastUtil;

import java.lang.reflect.Method;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class BlackNumberService extends Service {

    private innerBrocastReceiver mReceiver;
    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener listener;
    private BlackNumberDao mBlackNumberDao;

    @Override
    public void onCreate() {
        super.onCreate();
        mBlackNumberDao = BlackNumberDao.create(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(9999);

        mReceiver = new innerBrocastReceiver();
        registerReceiver(mReceiver, intentFilter);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private class innerBrocastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String phone = sms.getOriginatingAddress();


                String state = mBlackNumberDao.getState(phone);
                ToastUtil.show(getApplicationContext(),">>>>>>>>>>>  " + state);
                if ("0".equals(state) || "2".equals(state)) {
                    //终止短信广播
                    ToastUtil.show(getApplicationContext(),">>>>>>>>>>>  拦截短信 OK!");
                    abortBroadcast();
                }
            }

        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:  //空闲中

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:  //通话中:

                    break;
                case TelephonyManager.CALL_STATE_RINGING:  //响铃中
                    String sta = mBlackNumberDao.getState(incomingNumber);
                    if ("1".equals(sta) || "2".equals(sta)) {
                        endcall();
                    }
                    break;
            }
        }
    }

    private void endcall() {
//        ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
        try {
            ToastUtil.show(getApplicationContext(), "拦截！拦截~");
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method getService = clazz.getMethod("getService", String.class);
            IBinder iBinder = (IBinder) getService.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}