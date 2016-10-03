package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.domain.AppInfo;
import com.rya.ryamobilesafe.engin.AppInfoProvider;
import com.rya.ryamobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    private ArrayList<AppInfo> mAppInfoArrayList;
    private List<AppInfo> mUserApps;
    private List<AppInfo> mSystemApps;
    private ListView lv_appmanager;
    private TextView tv_appmanager_des;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAppAdapter == null) {
                mAppAdapter = new AppAdapter();

                if (tv_appmanager_des != null && mUserApps != null) {
                    tv_appmanager_des.setText("用户应用(" + mUserApps.size() + ")");
                }
                lv_appmanager.setAdapter(mAppAdapter);
            } else {
                mAppAdapter.notifyDataSetChanged();
            }
        }
    };
    private AppInfo mAppInfo;
    private PopupWindow mPopupWindow;
    private AppAdapter mAppAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);

        initTitle();

        initAppList();
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    }

    private void initAppList() {
        lv_appmanager = (ListView) findViewById(R.id.lv_appmanager);
        tv_appmanager_des = (TextView) findViewById(R.id.tv_appmanager_des);

        lv_appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * 设置应用描述栏
             *
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

        lv_appmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((position == 0 || (position) == (mUserApps.size() + 1))) {
                    return;
                } else {
                    if (position < (mUserApps.size() + 1)) {
                        mAppInfo = mUserApps.get(position - 1);
                    } else {
                        mAppInfo = mSystemApps.get(position - mUserApps.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupView = View.inflate(getApplicationContext(), R.layout.view_appmanager_popup, null);

        TextView tv_appmanager_popup_uninstall = (TextView) popupView.findViewById(R.id.tv_appmanager_popup_uninstall);
        TextView tv_appmanager_popup_start = (TextView) popupView.findViewById(R.id.tv_appmanager_popup_start);
        TextView tv_appmanager_popup_share = (TextView) popupView.findViewById(R.id.tv_appmanager_popup_share);

        tv_appmanager_popup_uninstall.setOnClickListener(this);
        tv_appmanager_popup_start.setOnClickListener(this);
        tv_appmanager_popup_share.setOnClickListener(this);

        //设置popupwindow动画（透明渐变）、（缩放动画）
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(600);
        alphaAnimation.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(600);
        scaleAnimation.setFillAfter(true);

        //动画集合(参数shareinf -- 统一显示规则函数)
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        popupView.startAnimation(animationSet);
        //设置popupwindow
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //方案一
        // popwindow中嵌套的View获取了焦点，设置view的setOnKeyListener方法
        /*view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)
                        && (mPopupWindow != null && mPopupWindow.isShowing())) {
                    mPopupWindow.dismiss();// 点击返回键的popWin退出就行
                    return true;
                }
                return false;
            }
        });*/
        //方案二

        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.showAsDropDown(view, view.getWidth() / 6, -view.getHeight());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_appmanager_popup_uninstall:
                if (mAppInfo.getIsSystem()) {
                    ToastUtil.show(getApplicationContext(), "不可卸载系统软件！");
                    return;
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;

            case R.id.tv_appmanager_popup_start:
                PackageManager packageManager = this.getPackageManager();
                Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    ToastUtil.show(getApplicationContext(), "不能开启此应用！");
                }
                break;

            case R.id.tv_appmanager_popup_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享应用 >>> " + mAppInfo.getName());
                intent.setType("txt/plain");
                startActivity(intent);
                break;
        }
        //点击功能后，取消显示PopupWindow
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }

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
            return mUserApps.size() + mSystemApps.size() + 2;
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
