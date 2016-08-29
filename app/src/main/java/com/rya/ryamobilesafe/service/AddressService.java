package com.rya.ryamobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.engin.AddressDao;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AddressService extends Service {

    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener listener;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private View mView;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView tv_service_address = (TextView) mView.findViewById(R.id.tv_service_address);
            tv_service_address.setText(location);
        }
    };
    private String location;

    @Override
    public void onCreate() {
        super.onCreate();

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTelephonyManager != null && listener != null) {
            mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:  //空闲中
                    ToastUtil.show(getApplicationContext(), "空闲中。。。");
                    if (mWindowManager != null && mView != null) {
                        mWindowManager.removeView(mView);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:  //通话中
                    ToastUtil.show(getApplicationContext(), "通话中。。。");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:  //响铃中
                    ToastUtil.show(getApplicationContext(), "响铃中。。。" + incomingNumber);
                    showToast(incomingNumber);

                    break;
            }
        }
    }

    private void query(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                location = AddressDao.checkAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void showToast(String incomingNumber) {
        mParams = new WindowManager.LayoutParams();

        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //指定位置
        params.gravity = Gravity.CENTER_HORIZONTAL + Gravity.BOTTOM;

        //自定义View
        mView = View.inflate(getApplicationContext(), R.layout.view_address_toast, null);

        TextView tv_service_address = (TextView) mView.findViewById(R.id.tv_service_address);
        int index = SPUtil.getInt(getApplicationContext(), ConstantValues.TOAST_STYLE, 0);
        int[] draColor = {R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green,
        };
        tv_service_address.setBackgroundResource(draColor[index]);

        query(incomingNumber);

        //添加至窗体对象
        mWindowManager.addView(mView, params);

    }
}
