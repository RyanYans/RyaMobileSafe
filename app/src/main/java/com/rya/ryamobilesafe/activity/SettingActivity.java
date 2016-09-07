package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.service.AddressService;
import com.rya.ryamobilesafe.service.RocketService;
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

    private String[] mDesColor;
    private int mIndex;
    private SettingClickView scv_toast_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initBelong();
        initToastStyle();
        initToastLocation();
        initRocket();
    }

    private void initRocket() {
        final SettingItemView siv_rocket = (SettingItemView) findViewById(R.id.siv_rocket);
        boolean is_checked = ServiceUtil.checkService(getApplicationContext(), "com.rya.ryamobilesafe.service.RocketService");
        siv_rocket.setCheck(is_checked);

        siv_rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb_ischeck = (CheckBox) siv_rocket.findViewById(R.id.cb_ischeck);
                boolean checked = !cb_ischeck.isChecked();
                siv_rocket.setCheck(checked);
                if (checked) {
                    Intent intent = new Intent(getApplicationContext(), RocketService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), RocketService.class);
                    stopService(intent);
                }
            }
        });
    }

    private void initToastLocation() {
        SettingClickView scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);

        scv_toast_location.setDesc("设置归属地提示框位置");
        scv_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToastLocationActicity.class);
                startActivity(intent);
            }
        });
    }

    private void initToastStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);

        //1.设置描述Toast样式的类型数组
        mDesColor = new String[]{"透明", "黄色", "蓝色", "灰色", "绿色"};
        //2.根据存在SP中的样式号来获取样式类型
        mIndex = SPUtil.getInt(getApplicationContext(), ConstantValues.TOAST_STYLE, 0);
        //3.通过String[]索引，把Toast样式显示在Des栏中
        scv_toast_style.setDesc(mDesColor[mIndex]);
        //4.监听点击事件，弹出对话框
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStyleDialog();
            }
        });
    }

    private void showStyleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("设置样式");
        builder.setSingleChoiceItems(mDesColor, mIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtil.putInt(getApplicationContext(), ConstantValues.TOAST_STYLE, which);
                dialog.dismiss();
                scv_toast_style.setDesc(mDesColor[which]);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    /**
     * 归属地查询
     */
    private void initBelong() {
        final SettingItemView siv_belong = (SettingItemView) findViewById(R.id.siv_belong);

        //每次打开设置界面检测服务是否开启
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
