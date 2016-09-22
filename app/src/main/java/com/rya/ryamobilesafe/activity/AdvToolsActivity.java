package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.engin.BackupSms;

import java.io.File;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AdvToolsActivity extends Activity {

    private TextView tv_advtools_addrquery;
    private TextView tv_backup_sms;
    private TextView tv_commomnumber_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advtools);

        initUI();

        initAddrQuery();

        initBackUpSms();

        initCommomNumberSearch();
    }

    private void initCommomNumberSearch() {
        tv_commomnumber_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommomNumberSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initAddrQuery() {
        tv_advtools_addrquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddrQueryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUI() {
        tv_advtools_addrquery = (TextView) findViewById(R.id.tv_advtools_addrquery);
        tv_backup_sms = (TextView) findViewById(R.id.tv_backup_sms);
        tv_commomnumber_search = (TextView) findViewById(R.id.tv_commomnumber_search);
    }

    private void initBackUpSms() {

        tv_backup_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackupDialog();
            }
        });
    }

    private void showSmsBackupDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_bksms);

        progressDialog.setIcon(R.drawable.logo);
        progressDialog.setTitle("短信备份");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = getFilesDir().toString() + File.separator + "backup";
                File file = new File(path);
                boolean mkdir = file.mkdirs();
                BackupSms.backup(getApplicationContext(), path, new BackupSms.CallBack() {
                    @Override
                    public void setMax(int index) {
                        progressDialog.setMax(index);
                        progressBar.setMax(index);
                    }

                    @Override
                    public void setProgress(int index) {
                        progressDialog.setProgress(index);
                        progressBar.setProgress(index);
                    }
                });
                progressDialog.dismiss();
            }
        }).start();
    }
}
