package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SafeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean safe_issetting = SPUtil.getBoolean(getApplicationContext(), ConstantValues.SAFE_ISSETTING, false);
        if (safe_issetting) {
            setContentView(R.layout.activity_safe);
        } else {
            Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }
}
