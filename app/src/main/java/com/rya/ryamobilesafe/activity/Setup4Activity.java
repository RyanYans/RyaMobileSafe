package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class Setup4Activity extends Activity {

    private Button bt_safe_last_setup2;
    private Button bt_safe_next_setup2;
    private CheckBox cb_safe_isopen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_setup4);

        initUI();

        bt_safe_last_setup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastActivity();
            }
        });

        bt_safe_next_setup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        cb_safe_isopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOpen = cb_safe_isopen.isChecked();
                SPUtil.putBoolean(getApplicationContext(), ConstantValues.SAFE_ISOPEN, isOpen);
            }
        });
    }

    private void initUI() {
        bt_safe_last_setup2 = (Button) findViewById(R.id.bt_safe_last_setup4);
        bt_safe_next_setup2 = (Button) findViewById(R.id.bt_safe_next_setup4);
        cb_safe_isopen = (CheckBox) findViewById(R.id.cb_safe_isopen);

        boolean isChecked = SPUtil.getBoolean(getApplicationContext(), ConstantValues.SAFE_ISOPEN, false);
        cb_safe_isopen.setChecked(isChecked);
    }

    private void nextActivity() {

        boolean isOpen = SPUtil.getBoolean(getApplicationContext(), ConstantValues.SAFE_ISOPEN, false);
        if (isOpen) {
            SPUtil.putBoolean(getApplicationContext(), ConstantValues.SAFE_ISSETTING, true);
            Intent intent = new Intent(getApplicationContext(), SafeActivity.class);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.show(getApplicationContext(), "请勾选安全防盗保护！");
        }
    }

    private void lastActivity() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
    }
}
