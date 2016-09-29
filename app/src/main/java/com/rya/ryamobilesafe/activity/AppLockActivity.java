package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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


            /* 设置已加锁listview */
            mLockAdapter = new AppLockAdapter(true);
            lv_applock_lock.setAdapter(mLockAdapter);

            /* 设置未加锁listview */
            mUnlockAdapter = new AppLockAdapter(false);
            lv_applock_unlock.setAdapter(mUnlockAdapter);
        }
    };
    private Button btn_applock_unlock;
    private Button btn_applock_lock;
    private AppLockAdapter mLockAdapter;
    private AppLockAdapter mUnlockAdapter;
    private TranslateAnimation mItemAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);

        initUI();

        initAnimation();

        initData();
    }

    private void initAnimation() {
        mItemAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mItemAnimation.setDuration(500);
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

        /*设置已加锁按钮监听*/
        btn_applock_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_applock_unlock.setBackgroundResource(R.drawable.tab_left_default);
                btn_applock_lock.setBackgroundResource(R.drawable.tab_right_pressed);

                ll_applock_unlock.setVisibility(View.GONE);
                ll_applock_lock.setVisibility(View.VISIBLE);

                mLockAdapter.notifyDataSetChanged();
            }
        });

        /*设置未加锁按钮监听*/
        btn_applock_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_applock_lock.setBackgroundResource(R.drawable.tab_left_default);
                btn_applock_unlock.setBackgroundResource(R.drawable.tab_right_pressed);

                ll_applock_lock.setVisibility(View.GONE);
                ll_applock_unlock.setVisibility(View.VISIBLE);

                mUnlockAdapter.notifyDataSetChanged();
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
            //更新界面
            if (isLock) {
                tv_applock_lock_count.setText("应用数：" + mLockApp.size());
                return mLockApp.size();
            } else {
                tv_applock_unlock_count.setText("应用数：" + mUnlockApp.size());
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
            final AppInfo info = getItem(position);
            viewHolder.iv_item_applock_icon.setImageDrawable(info.getIcon());
            viewHolder.tv_item_applock_name.setText(info.getName());
            if (isLock) {
                viewHolder.iv_item_applock_islock.setImageResource(R.drawable.lock);
            } else {
                viewHolder.iv_item_applock_islock.setImageResource(R.drawable.unlock);
            }

            final View finalView = view;
            viewHolder.iv_item_applock_islock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finalView.startAnimation(mItemAnimation);

                    mItemAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        // 当动画结束后执行逻辑
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock) {
                                //lock集合中移除条目信息
                                mLockApp.remove(info);
                                //unlock集合中添加条目信息
                                mUnlockApp.add(info);
                                //数据库已加锁信息移除
                                mAppLockDao.delete(info.getPackageName());
                                // 更新适配器
                                mLockAdapter.notifyDataSetChanged();
                            } else {
                                //lock集合中移除条目信息
                                mUnlockApp.remove(info);
                                //unlock集合中添加条目信息
                                mLockApp.add(info);
                                //数据库已加锁信息移除
                                mAppLockDao.insert(info.getPackageName());
                                // 更新适配器
                                mUnlockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });

            return view;
        }

        private class ViewHolder {
            ImageView iv_item_applock_icon;
            TextView tv_item_applock_name;
            ImageView iv_item_applock_islock;
        }

    }

}
