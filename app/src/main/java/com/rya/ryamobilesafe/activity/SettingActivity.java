package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.service.AddressService;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ServiceUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;
import com.rya.ryamobilesafe.view.SettingClickView;
import com.rya.ryamobilesafe.view.SettingItemView;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initBelong();
        initToastStyle();
    }

    private void initToastStyle() {
        SettingClickView scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);

        //设置描述Toast样式的类型数组
        String[] des_color = {"透明", "黄色", "蓝色", "灰色", "绿色"};
        //根据存在SP中的样式号来获取样式类型
        int index = SPUtil.getInt(getApplicationContext(), ConstantValues.TOAST_STYLE, 0);
        //通过String[]索引，把Toast样式显示在Des栏中
        scv_toast_style.setDesc(des_color[index]);
    }

    /**
     * 归属地查询
     */
    private void initBelong() {
        final SettingItemView siv_belong = (SettingItemView) findViewById(R.id.siv_belong);

        boolean isbelong = ServiceUtil.checkService(getApplicationContext(), "com.rya.ryamobilesafe.service.AddressService");
        siv_belong.setCheck(isbelong);

        siv_belong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(SettingActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    }
                }

                boolean isbelong = !siv_belong.isCheck();
                siv_belong.setCheck(isbelong);
                if (isbelong) {
                    startService(new Intent(getApplicationContext(), AddressService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
                //ToastUtil.show(getApplicationContext(), "点击！");
            }
        });
    }

    /**
     * 启动更新检测
     */
    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);

        boolean isupdate = SPUtil.getBoolean(this, ConstantValues.ISUPDATE, false);
        siv_update.setCheck(isupdate);

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUpdate = !siv_update.isCheck();
                siv_update.setCheck(isUpdate);
                SPUtil.putBoolean(getApplicationContext(), ConstantValues.ISUPDATE, isUpdate);
                //ToastUtil.show(getApplicationContext(), "点击！");
            }
        });
    }
}
