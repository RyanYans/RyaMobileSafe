package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText et_safe_phone;
    private Button bt_safe_last_setup2;
    private Button bt_safe_next_setup2;
    private Button bt_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_setup3);

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
                String phone = et_safe_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    SPUtil.putString(getApplicationContext(), ConstantValues.CONTACT_PHONE, phone);
                    nextActivity();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入安全号码！");
                }
            }
        });
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SafeContactActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void initUI() {
        bt_safe_last_setup2 = (Button) findViewById(R.id.bt_safe_last_setup3);
        bt_safe_next_setup2 = (Button) findViewById(R.id.bt_safe_next_setup3);
        bt_select_number = (Button) findViewById(R.id.bt_select_number);
        et_safe_phone = (EditText) findViewById(R.id.et_safe_phone);

        String phone = SPUtil.getString(getApplicationContext(), ConstantValues.CONTACT_PHONE, "");
        et_safe_phone.setText(phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data!=null) && (resultCode == 0)) {
            String phone = data.getStringExtra("phone");
            et_safe_phone.setText(phone);
        }
    }

    @Override
    protected void nextActivity() {
        String phone = et_safe_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            SPUtil.putString(getApplicationContext(), ConstantValues.CONTACT_PHONE, phone);

            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            //设置平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(getApplicationContext(), "请输入安全号码！");
        }
    }

    @Override
    protected void lastActivity() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        //设置平移(上一页)动画
        overridePendingTransition(R.anim.last_in_anim, R.anim.last_out_anim);
    }
}
