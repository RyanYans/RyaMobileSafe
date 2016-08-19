package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
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
    }

    private void initBelong() {
        final SettingItemView siv_belong = (SettingItemView) findViewById(R.id.siv_belong);

        boolean isupdate = SPUtil.getBoolean(this, ConstantValues.ISUPDATE, false);
        siv_belong.setCheck(isupdate);

        siv_belong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isupdate = !siv_belong.isCheck();
                siv_belong.setCheck(isupdate);
                SPUtil.putBoolean(getApplicationContext(), ConstantValues.ISUPDATE, isupdate);
                //ToastUtil.show(getApplicationContext(), "点击！");
            }
        });
    }

    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);

        boolean isupdate = SPUtil.getBoolean(this, ConstantValues.ISUPDATE, false);
        siv_update.setCheck(isupdate);

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isupdate = !siv_update.isCheck();
                siv_update.setCheck(isupdate);
                SPUtil.putBoolean(getApplicationContext(), ConstantValues.ISUPDATE, isupdate);
                //ToastUtil.show(getApplicationContext(), "点击！");
            }
        });
    }
}
