package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.dao.AppLockDao;
import com.rya.ryamobilesafe.db.domain.AppInfo;
import com.rya.ryamobilesafe.engin.AppInfoProvider;
import com.rya.ryamobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppLockActivity extends Activity {

    private LinearLayout ll_applock_unlock;
    private LinearLayout ll_applock_lock;
    private TextView tv_applock_unlock_count;
    private TextView tv_applock_lock_count;
    private ListView lv_applock_unlock;
    private ListView lv_applock_lock;
    private ArrayList<AppInfo> mLockApp;
    private ArrayList<AppInfo> mUnlockApp;
    private AppLockDao mAppLockDao;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //更新界面
            tv_applock_unlock_count.setText("应用数：" + mUnlockApp.size());
            tv_applock_lock_count.setText("应用数：" + mLockApp.size());

            AppLockAdapter lockAdapter = new AppLockAdapter(true);
            lv_applock_unlock.setAdapter(lockAdapter);

            AppLockAdapter unlockAdapter = new AppLockAdapter(false);
            lv_applock_unlock.setAdapter(unlockAdapter);
        }
    };
    private Button btn_applock_unlock;
    private Button btn_applock_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);

        initUI();

        initData();
    }

    private void initData() {
        mAppLockDao = AppLockDao.create(getApplicationContext());
        List<String> pkgNameList = mAppLockDao.searchAll();
        mLockApp = new ArrayList<>();
        mUnlockApp = new ArrayList<>();
        ArrayList<AppInfo> appInfos = AppInfoProvider.getAppInfo(getApplicationContext());
        for (AppInfo info : appInfos) {
            if (pkgNameList.contains(info.getPackageName())) {
                mLockApp.add(info);
            } else {
                mUnlockApp.add(info);
            }
        }
        mHandler.sendEmptyMessage(0);
    }

    private void initUI() {
        btn_applock_unlock = (Button) findViewById(R.id.btn_applock_unlock);
        btn_applock_lock = (Button) findViewById(R.id.btn_applock_lock);

        ll_applock_unlock = (LinearLayout) findViewById(R.id.ll_applock_unlock);
        ll_applock_lock = (LinearLayout) findViewById(R.id.ll_applock_lock);

        tv_applock_unlock_count = (TextView) findViewById(R.id.tv_applock_unlock_count);
        tv_applock_lock_count = (TextView) findViewById(R.id.tv_applock_lock_count);

        lv_applock_unlock = (ListView) findViewById(R.id.lv_applock_unlock);
        lv_applock_lock = (ListView) findViewById(R.id.lv_applock_lock);

        btn_applock_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_applock_unlock.setBackgroundResource(R.drawable.tab_left_default);
                btn_applock_lock.setBackgroundResource(R.drawable.tab_right_pressed);

                ll_applock_unlock.setVisibility(View.GONE);
                ll_applock_lock.setVisibility(View.VISIBLE);
            }
        });

        btn_applock_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_applock_lock.setBackgroundResource(R.drawable.tab_left_default);
                btn_applock_unlock.setBackgroundResource(R.drawable.tab_right_pressed);

                ll_applock_lock.setVisibility(View.GONE);
                ll_applock_unlock.setVisibility(View.VISIBLE);
            }
        });
    }

    private class AppLockAdapter extends BaseAdapter {
        private boolean isLock;

        public AppLockAdapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                return mLockApp.size();
            } else {
                return mUnlockApp.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockApp.get(position);
            } else {
                return mUnlockApp.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder viewHolder = null;
            if (convertView != null) {
                view = convertView;

                viewHolder = (ViewHolder) view.getTag();
            } else {
                viewHolder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.listview_applock, null);

                viewHolder.iv_item_applock_icon = (ImageView) view.findViewById(R.id.iv_item_applock_icon);
                viewHolder.tv_item_applock_name = (TextView) view.findViewById(R.id.tv_item_applock_name);
                viewHolder.iv_item_applock_islock = (ImageView) view.findViewById(R.id.iv_item_applock_islock);

                view.setTag(viewHolder);
            }
            viewHolder.iv_item_applock_icon.setImageDrawable(getItem(position).getIcon());
            viewHolder.tv_item_applock_name.setText(getItem(position).getName());
            if (isLock) {
                viewHolder.iv_item_applock_islock.setImageResource(R.drawable.lock);
            } else {
                viewHolder.iv_item_applock_islock.setImageResource(R.drawable.unlock);
            }
            return view;
        }

        private class ViewHolder {
            ImageView iv_item_applock_icon;
            TextView tv_item_applock_name;
            ImageView iv_item_applock_islock;
        }

    }
}
