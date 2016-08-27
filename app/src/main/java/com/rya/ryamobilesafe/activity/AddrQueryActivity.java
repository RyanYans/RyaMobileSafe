package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AddrQueryActivity extends Activity {

    private EditText et_advtools_addrquery_phone;
    private Button btn_advtools_addrquery_submit;
    private TextView tv_advtools_addrquery_result;

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
                String phone = et_advtools_addrquery_phone.getText().toString();
                //归属地查询
            }
        });
    }

    private void initUI() {
        et_advtools_addrquery_phone = (EditText) findViewById(R.id.et_advtools_addrquery_phone);
        btn_advtools_addrquery_submit = (Button) findViewById(R.id.btn_advtools_addrquery_submit);
        tv_advtools_addrquery_result = (TextView) findViewById(R.id.tv_advtools_addrquery_result);
    }
}
