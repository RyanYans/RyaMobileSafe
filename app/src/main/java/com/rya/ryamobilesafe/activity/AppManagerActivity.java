package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.domain.AppInfo;
import com.rya.ryamobilesafe.engin.AppInfoProvider;

import java.util.ArrayList;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppManagerActivity extends Activity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppAdapter appAdapter = new AppAdapter();
            ListView lv_appmanager = (ListView)findViewById(R.id.lv_appmanager);
            lv_appmanager.setAdapter(appAdapter);
        }
    };
    private ArrayList<AppInfo> appInfoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);

        initTitle();

        initAppList();
    }

    private void initAppList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfoArrayList = AppInfoProvider.getAppInfo(getApplicationContext());
                mHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    private void initTitle() {
        String path = Environment.getDataDirectory().getPath();
//        String sdpath = Environment.getExternalStorageDirectory().getPath();      高版本sd卡取消概念？

        long tmemory = getTotalSpace(path);
        long memory = getAvilSpace(path);

        String memorySize = Formatter.formatFileSize(this, memory);
        String sdMemorySize = Formatter.formatFileSize(this, tmemory);

        TextView tv_appmanager_totalmemory = (TextView) findViewById(R.id.tv_appmanager_totalmemory);
        TextView tv_appmanager_avilmemory = (TextView) findViewById(R.id.tv_appmanager_avilmemory);

        String textMemorySize = tv_appmanager_avilmemory.getText().toString() + memorySize;
        String textSdMemorySize = tv_appmanager_totalmemory.getText().toString() + sdMemorySize;
        tv_appmanager_avilmemory.setText(textMemorySize);
        tv_appmanager_totalmemory.setText(textSdMemorySize);
    }

    private long getAvilSpace(String path) {
        StatFs statFs = new StatFs(path);

        long blockCountLong = statFs.getAvailableBlocksLong();
        //size 返回是byte类型
        long blockSizeLong = statFs.getBlockSizeLong();

        return blockCountLong * blockSizeLong;
    }

    private long getTotalSpace(String path) {
        StatFs statFs = new StatFs(path);

        long blockCountLong = statFs.getBlockCountLong();
        //size 返回是byte类型
        long blockSizeLong = statFs.getBlockSizeLong();

        return blockCountLong * blockSizeLong;
    }


    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appInfoArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return appInfoArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.listview_appmanager_item, null);
                //必须要通过View去寻到里边的控件
                viewHolder.iv_item_appmanager = (ImageView) view.findViewById(R.id.iv_item_appmanager);
                viewHolder.tv_appmanager_name = (TextView) view.findViewById(R.id.tv_appmanager_name);
                viewHolder.tv_appmanager_path = (TextView) view.findViewById(R.id.tv_appmanager_path);

                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.iv_item_appmanager.setImageDrawable(appInfoArrayList.get(position).getIcon());
            viewHolder.tv_appmanager_name.setText(appInfoArrayList.get(position).getName());
            if (appInfoArrayList.get(position).getIsSystem()) {
                viewHolder.tv_appmanager_path.setText("系统应用");
            } else {
                viewHolder.tv_appmanager_path.setText("用户应用");
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_item_appmanager;
        TextView tv_appmanager_name;
        TextView tv_appmanager_path;
    }
}
