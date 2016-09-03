package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SmogActivity extends Activity {
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            SmogActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smog);

        ImageView iv_smog_top = (ImageView) findViewById(R.id.iv_smog_top);
        ImageView iv_smog_bottom = (ImageView) findViewById(R.id.iv_smog_bottom);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(800);
        iv_smog_top.startAnimation(alphaAnimation);
        iv_smog_bottom.startAnimation(alphaAnimation);

        mHandler.sendEmptyMessageDelayed(1, 1000);
    }
}
