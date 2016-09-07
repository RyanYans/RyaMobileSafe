package com.rya.ryamobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.activity.SmogActivity;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class RocketService extends Service {

    public static final int ROCKET_OK = 200;
    private WindowManager mWindowManager;
    private Display mDefaultDisplay;
    private int screenWidth;
    private int screenHeight;
    private WindowManager.LayoutParams mParams;
    private View mRocketView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ROCKET_OK:
                    mWindowManager.updateViewLayout(mRocketView, params);
                    break;
            }
        }
    };
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDefaultDisplay = mWindowManager.getDefaultDisplay();
        screenWidth = mDefaultDisplay.getWidth();
        screenHeight = mDefaultDisplay.getHeight();

        showToast();

    }

    private void showToast() {
        mParams = new WindowManager.LayoutParams();

        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置控件位置
        params.gravity = Gravity.TOP + Gravity.LEFT;

        mRocketView = View.inflate(getApplicationContext(), R.layout.view_rocket, null);
        ImageView iv_rocket = (ImageView) mRocketView.findViewById(R.id.iv_rocket);

        mWindowManager.addView(mRocketView, params);

        //手动获取Iv.background，运行动画
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_rocket.getBackground();
        animationDrawable.start();

        mRocketView.setOnTouchListener(new View.OnTouchListener() {
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
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }

                        if (params.x + mRocketView.getWidth() > screenWidth) {
                            params.x = screenWidth - mRocketView.getWidth();
                        }
                        if (params.y + mRocketView.getHeight() + 70 > screenHeight) {
                            params.y = screenHeight - mRocketView.getHeight() - 70;
                        }

                        mWindowManager.updateViewLayout(mRocketView, params);

                        //重置坐标
                        startX = event.getRawX();
                        startY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("RocketLocation", "params.x >>> " + params.x + "params.y >>> " + params.y);
                        if (params.x > (screenWidth / 5) && params.x < ((2 * screenWidth) / 3) && params.y > ((3 * screenHeight) / 5)) {
                            sendRocket();

                            //产生Rocket尾气Activity
                            Intent intent = new Intent(getApplicationContext(), SmogActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }

                //若有多个点击事件，返回false，让系统执行回掉
                return false;
            }

        });
    }

    private void sendRocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i >= 0; i--) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    params.y = ((i * screenHeight) / 10);
                    Message msg = Message.obtain();
                    msg.what = ROCKET_OK;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWindowManager != null && mRocketView != null) {
            mWindowManager.removeView(mRocketView);
        }
    }
}
