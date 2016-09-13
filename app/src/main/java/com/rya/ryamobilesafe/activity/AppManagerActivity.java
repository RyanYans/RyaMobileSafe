package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.domain.AppInfo;
import com.rya.ryamobilesafe.engin.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppManagerActivity extends Activity {
    private ArrayList<AppInfo> mAppInfoArrayList;
    private List<AppInfo> mUserApps;
    private List<AppInfo> mSystemApps;
    private ListView lv_appmanager;
    private TextView tv_appmanager_des;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppAdapter appAdapter = new AppAdapter();

            if (tv_appmanager_des != null && mUserApps != null) {
                tv_appmanager_des.setText("用户应用(" + mUserApps.size() + ")");
            }

            lv_appmanager.setAdapter(appAdapter);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);

        initTitle();

        initAppList();
    }

    private void initAppList() {
        lv_appmanager = (ListView) findViewById(R.id.lv_appmanager);
        tv_appmanager_des = (TextView) findViewById(R.id.tv_appmanager_des);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppInfoArrayList = AppInfoProvider.getAppInfo(getApplicationContext());

                mUserApps = new ArrayList<AppInfo>();
                mSystemApps = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mAppInfoArrayList) {
                    if (appInfo.getIsSystem()) {
                        //系统应用
                        mSystemApps.add(appInfo);
                    } else {
                        // 用户应用
                        mUserApps.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);

            }
        }).start();

        lv_appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * @param view
             * @param firstVisibleItem      显示在屏幕上第一条
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserApps != null && mSystemApps != null) {
                    if (firstVisibleItem > mUserApps.size()) {
                        tv_appmanager_des.setText("系统应用(" + mSystemApps.size() + ")");
                    } else {
                        tv_appmanager_des.setText("用户应用(" + mUserApps.size() + ")");
                    }
                }
            }
        });


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
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        /**
         * @param position
         * @return 0 -- 代表纯文本条目
         * 1 -- 代表图片+文本条目
         */
        @Override
        public int getItemViewType(int position) {
            if ((position == 0) || (position == (mUserApps.size() + 1))) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mUserApps.size() + mSystemApps.size();
        }

        @Override
        public AppInfo getItem(int position) {
            // position = 0 || =muserApps.size()+1 返回null(纯文本控件)
            if ((position == 0) || (position == (mUserApps.size() + 1))) {
                return null;
            }
            if (position < (mUserApps.size() + 1)) {
                //减去首条目
                return mUserApps.get(position - 1);
            } else {
                //减去首条目2条
                return mSystemApps.get(position - mUserApps.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (getItemViewType(position) == 1) {
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

                viewHolder.iv_item_appmanager.setImageDrawable(getItem(position).getIcon());
                viewHolder.tv_appmanager_name.setText(getItem(position).getName());
                if (getItem(position).getIsSystem()) {
                    viewHolder.tv_appmanager_path.setText("系统应用");
                } else {
                    viewHolder.tv_appmanager_path.setText("用户应用");
                }

            } else {
                view = View.inflate(getApplicationContext(), R.layout.listview_appmanager_text, null);
                TextView tv_appmanager_list = (TextView) view.findViewById(R.id.tv_appmanager_list);
                if (position == 0) {
                    tv_appmanager_list.setText("用户应用(" + mUserApps.size() + ")");
                } else {
                    tv_appmanager_list.setText("系统应用(" + mSystemApps.size() + ")");
                }
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
