package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.engin.AddressDao;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AddrQueryActivity extends Activity {

    private EditText et_advtools_addrquery_phone;
    private Button btn_advtools_addrquery_submit;
    private TextView tv_advtools_addrquery_result;
    private String location;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_advtools_addrquery_result.setText(location);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advtools_addrquery);

        initUI();
        initListener();
    }

    private void initListener() {
        btn_advtools_addrquery_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = et_advtools_addrquery_phone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    shakeET();
                    return;
                }

                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        query(phone);
                    }
                }.start();
            }
        });

        //实时查询
        et_advtools_addrquery_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = et_advtools_addrquery_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    shakeET();
                    return;
                }
                query(phone);
            }
        });
    }

    private void shakeET() {
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        et_advtools_addrquery_phone.startAnimation(shake);
    }

    private void query(String phone) {
        location = AddressDao.checkAddress(phone);
        mHandler.sendEmptyMessage(0);
    }

    private void initUI() {
        et_advtools_addrquery_phone = (EditText) findViewById(R.id.et_advtools_addrquery_phone);
        btn_advtools_addrquery_submit = (Button) findViewById(R.id.btn_advtools_addrquery_submit);
        tv_advtools_addrquery_result = (TextView) findViewById(R.id.tv_advtools_addrquery_result);
    }
}
