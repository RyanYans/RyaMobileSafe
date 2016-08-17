package com.rya.ryamobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private TextView tv_versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        
        //初始化UI
        initUI();
        String versionText = "版本号：" + getVersionName();
        tv_versionName.setText(versionText);
    }



    /**
     *  初始化UI方法     Alt +Shift +F
     */
    private void initUI() {
        tv_versionName = (TextView) findViewById(R.id.tv_version_name);
    }

    public String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
