package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.service.ProcessCleanService;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ServiceUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ProcessSettingActivity extends Activity {

    private CheckBox cb_process_system_hide;
    private CheckBox cb_process_lockscreen_clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initUI();

        initSystemHide();

        initLockClean();

    }

    private void initUI() {
        cb_process_system_hide = (CheckBox) findViewById(R.id.cb_process_system_hide);
        cb_process_lockscreen_clean = (CheckBox) findViewById(R.id.cb_process_lockscreen_clean);
    }

    private void initLockClean() {
        boolean isService = ServiceUtil.checkService(getApplicationContext(), "com.rya.ryamobilesafe.service.ProcessCleanService");
        cb_process_lockscreen_clean.setChecked(isService);

        cb_process_lockscreen_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(getApplicationContext(), ProcessCleanService.class);
                    startService(intent);
                    ToastUtil.show(getApplicationContext(), "开启Service！");
                } else {
                    Intent intent = new Intent(getApplicationContext(), ProcessCleanService.class);
                    stopService(intent);
                }
            }
        });
    }

    private void initSystemHide() {
        boolean isHideSystem = SPUtil.getBoolean(getApplicationContext(), ConstantValues.SYSTEMPROCESS_HIDE, false);
        cb_process_system_hide.setChecked(isHideSystem);
        cb_process_system_hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtil.putBoolean(getApplicationContext(), ConstantValues.SYSTEMPROCESS_HIDE, isChecked);
                cb_process_system_hide.setChecked(isChecked);
            }
        });
    }
}
