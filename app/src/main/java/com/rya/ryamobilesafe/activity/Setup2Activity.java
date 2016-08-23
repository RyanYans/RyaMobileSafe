package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;
import com.rya.ryamobilesafe.view.SettingItemView;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_setup2);

        Button bt_safe_last_setup2 = (Button) findViewById(R.id.bt_safe_last_setup2);
        Button bt_safe_next_setup2 = (Button) findViewById(R.id.bt_safe_next_setup2);
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);

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
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBound = !siv_sim_bound.isCheck();
                siv_sim_bound.setCheck(isBound);

                if (isBound) {
                    TelephonyManager telePhoneMg = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = telePhoneMg.getSimSerialNumber();
                    SPUtil.putString(getApplicationContext(), ConstantValues.SAFE_SIM, simSerialNumber);
                } else {
                    SPUtil.remove(getApplicationContext(), ConstantValues.SAFE_SIM);
                }
            }
        });
    }

    @Override
    protected void nextActivity() {
        String safe_sim = SPUtil.getString(getApplicationContext(), ConstantValues.SAFE_SIM, "");
        if (TextUtils.isEmpty(safe_sim)) {
            ToastUtil.show(getApplicationContext(), "请绑定SIM卡！");
        } else {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            //设置平移(下一页)动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }

    }

    @Override
    protected void lastActivity() {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        //设置平移(上一页)动画
        overridePendingTransition(R.anim.last_in_anim, R.anim.last_out_anim);
    }

    private void initUI() {
        String simseri = SPUtil.getString(getApplicationContext(), ConstantValues.SAFE_SIM, "");
        if (simseri.isEmpty()) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }
    }

}
