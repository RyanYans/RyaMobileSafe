package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);

        initTitle();
    }

    private void initTitle() {
        String path = Environment.getDataDirectory().getAbsolutePath();
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();

        long memory = getAvilSpace(path);
        long sdmemory = getAvilSpace(sdpath);

        String memorySize = Formatter.formatFileSize(this, memory);
        String sdMemorySize = Formatter.formatFileSize(this, sdmemory);

        TextView tv_appmanager_memory = (TextView) findViewById(R.id.tv_appmanager_memory);
        TextView tv_appmanager_sdmemory = (TextView) findViewById(R.id.tv_appmanager_sdmemory);

        String textMemorySize = tv_appmanager_memory.getText().toString() + memorySize;
        String textSdMemorySize = tv_appmanager_sdmemory.getText().toString() + sdMemorySize;
        tv_appmanager_memory.setText(textMemorySize);
        tv_appmanager_sdmemory.setText(textSdMemorySize);
    }

    private long getAvilSpace(String path) {
        StatFs statFs = new StatFs(path);

        long blockCountLong = statFs.getAvailableBlocksLong();
        //size 返回是byte类型
        long blockSizeLong = statFs.getBlockSizeLong();

        return blockCountLong*blockSizeLong;
    }
}
