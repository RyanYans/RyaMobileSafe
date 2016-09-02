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
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
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
    private int screenWidth;
    private int screenHeight;
    private Display mDefaultDisplay;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView tv_service_address = (TextView) mView.findViewById(R.id.tv_service_address);
            tv_service_address.setText(location);
        }
    };
    private String location;
    private TextView tv_service_address;

    @Override
    public void onCreate() {
        super.onCreate();

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        mDefaultDisplay = mWindowManager.getDefaultDisplay();
        screenWidth = mDefaultDisplay.getWidth();
        screenHeight = mDefaultDisplay.getHeight();

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

        //指定属性 --位置
        params.gravity = Gravity.TOP + Gravity.LEFT;

        //自定义View
        mView = View.inflate(getApplicationContext(), R.layout.view_address_toast, null);
        tv_service_address = (TextView) mView.findViewById(R.id.tv_service_address);

            //设置左上角坐标  --相对于Gravity
        params.x = SPUtil.getInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_X, 0);
        params.y = SPUtil.getInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_Y, 0);

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

        mView.setOnTouchListener(new View.OnTouchListener() {
            private float startY;
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取开始点击点到远点距离
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        float moveY = event.getRawY();

                        float disX = moveX - startX;
                        float disY = moveY - startY;

                        params.x = (int) (params.x + disX);
                        params.y = (int) (params.y + disY);

                        // 容错处理， 提示框不得移出屏幕
                        if ((params.x < 0) || (params.y < 0)
                                || (params.x + mView.getWidth() > screenWidth)
                                || (params.y + mView.getHeight() + 70 > screenHeight)) {
                            return true;
                        }

                        mWindowManager.updateViewLayout(mView, params);

                        //重置坐标
                        startX = event.getRawX();
                        startY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:

                        SPUtil.putInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_X, params.x);
                        SPUtil.putInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_Y, params.y);

                        break;
                }

                //若有多个点击事件，返回false，让系统执行回掉
                return false;
            }

        });

    }
}
