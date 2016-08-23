package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SafeActivity extends Activity {

    private TextView tv_safe_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();

        if (tv_safe_reset != null) {
            tv_safe_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SPUtil.removeSafeData(getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void initUI() {
        boolean safe_issetting = SPUtil.getBoolean(getApplicationContext(), ConstantValues.SAFE_ISSETTING, false);
        if (safe_issetting) {
            setContentView(R.layout.activity_safe);
            tv_safe_reset = (TextView) findViewById(R.id.tv_safe_reset);
            TextView tv_safe_phone = (TextView) findViewById(R.id.tv_safe_phone);
            String phone = SPUtil.getString(getApplicationContext(), ConstantValues.CONTACT_PHONE, "");
            tv_safe_phone.setText(phone);
        } else {
            Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }
}