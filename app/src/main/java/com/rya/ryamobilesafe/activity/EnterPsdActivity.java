package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class EnterPsdActivity extends Activity {

    private TextView tv_applock_enterpsd;
    private ImageView iv_applock_enterpad;
    private EditText et_applock_enterpsd;
    private Button btn_applock_enterpsd;
    private String pkgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterpsd);

        initUI();

        initData();
    }

    private void initData() {
        try {
            pkgName = getIntent().getStringExtra("pkgName");
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(pkgName, 0);
            String label = applicationInfo.loadLabel(packageManager).toString();
            Drawable icon = applicationInfo.loadIcon(packageManager);

            tv_applock_enterpsd.setText(label);
            iv_applock_enterpad.setImageDrawable(icon);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        tv_applock_enterpsd = (TextView) findViewById(R.id.tv_applock_enterpsd);
        iv_applock_enterpad = (ImageView) findViewById(R.id.iv_applock_enterpad);
        et_applock_enterpsd = (EditText) findViewById(R.id.et_applock_enterpsd);
        btn_applock_enterpsd = (Button) findViewById(R.id.btn_applock_enterpsd);

        btn_applock_enterpsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_applock_enterpsd.getText().toString();
                if (!psd.isEmpty()) {
                    if (psd.equals("123")) {
                        //发送应用密码通过广播至AppLockService
                        // new Intent("android.action...SKIP")  --自定义广播
                        // intent.putExtu("relievePkg", pkgName);
                        finish();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误！");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码！");
                }
            }
        });

    }

    /**
     * 自定义返回逻辑
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
