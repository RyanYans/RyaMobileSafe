package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ToastLocationActicity extends Activity {

    private ImageView iv_toast_drag;
    private Button btn_toast_top;
    private Button btn_toast_bottom;
    private WindowManager mWindowManager;
    private Display mDefaultDisplay;
    private int screenWidth;
    private int screenHeight;
    private long[] clickNum = new long[2];

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_toast_location);

        mWindowManager = getWindowManager();
        mDefaultDisplay = mWindowManager.getDefaultDisplay();
        screenWidth = mDefaultDisplay.getWidth();
        screenHeight = mDefaultDisplay.getHeight();


        initUI();

        iv_toast_drag.setOnTouchListener(new View.OnTouchListener() {

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

                        int newLeft = (int) (iv_toast_drag.getLeft() + disX);
                        int newTop = (int) (iv_toast_drag.getTop() + disY);
                        int newRight = (int) (iv_toast_drag.getRight() + disX);
                        int newBottom = (int) (iv_toast_drag.getBottom() + disY);

                        //提示框不能超过屏幕边缘区域
                        if ((newLeft < 0) || (newRight > screenWidth) || (newTop < 0) || (newBottom > screenHeight-70)) {
                            return true;
                        }
                        if (newTop > screenHeight / 2) {
                            btn_toast_top.setVisibility(View.VISIBLE);
                            btn_toast_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            btn_toast_top.setVisibility(View.INVISIBLE);
                            btn_toast_bottom.setVisibility(View.VISIBLE);
                        }

                        iv_toast_drag.layout(newLeft, newTop, newRight, newBottom);

                        //重置坐标
                        startX = event.getRawX();
                        startY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        int location_X = iv_toast_drag.getLeft();
                        int location_Y = iv_toast_drag.getTop();

                        SPUtil.putInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_X, location_X);
                        SPUtil.putInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_Y, location_Y);

                        break;
                }

                //若有多个点击事件，返回false，让系统执行回掉
                return false;
            }
        });

        //双击居中
        iv_toast_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(clickNum, 1, clickNum, 0, clickNum.length - 1);
                clickNum[clickNum.length - 1] =  SystemClock.uptimeMillis();
                if (clickNum[clickNum.length - 1] - clickNum[0] < 500) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.leftMargin = (screenWidth / 2) - (iv_toast_drag.getWidth()/2);
                    layoutParams.topMargin = (screenHeight / 2) - (iv_toast_drag.getHeight() / 2);

                    //保存提示框位置数据
                    SPUtil.putInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_X, layoutParams.leftMargin);
                    SPUtil.putInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_Y, layoutParams.topMargin);

                    iv_toast_drag.setLayoutParams(layoutParams);
                }
            }
        });

    }

    private void initUI() {
        iv_toast_drag = (ImageView) findViewById(R.id.iv_toast_drag);
        btn_toast_top = (Button) findViewById(R.id.btn_toast_top);
        btn_toast_bottom = (Button) findViewById(R.id.btn_toast_bottom);

        //获取并显示TOAST控件位置
        int location_x = SPUtil.getInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_X, 0);
        int location_y = SPUtil.getInt(getApplicationContext(), ConstantValues.TOAST_LOCATION_Y, 0);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = location_x;
        layoutParams.topMargin = location_y;

        iv_toast_drag.setLayoutParams(layoutParams);

        if (location_y > screenHeight / 2) {
            btn_toast_top.setVisibility(View.VISIBLE);
            btn_toast_bottom.setVisibility(View.INVISIBLE);
        } else {
            btn_toast_top.setVisibility(View.INVISIBLE);
            btn_toast_bottom.setVisibility(View.VISIBLE);
        }

    }
}
