package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class Setup3Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_setup3);

        Button bt_safe_last_setup2 = (Button) findViewById(R.id.bt_safe_last_setup3);
        Button bt_safe_next_setup2 = (Button) findViewById(R.id.bt_safe_next_setup3);

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
    }

    private void nextActivity() {
        Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
        startActivity(intent);
        finish();
    }

    private void lastActivity() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
    }
}
