package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AdvToolsActivity extends Activity {

    private TextView tv_advtools_addrquery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advtools);

        initUI();

        initListener();
    }

    private void initUI() {
        tv_advtools_addrquery = (TextView) findViewById(R.id.tv_advtools_addrquery);
    }

    private void initListener() {
        tv_advtools_addrquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddrQueryActivity.class);
                startActivity(intent);
            }
        });
    }
}
