package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.domain.ProcessInfo;
import com.rya.ryamobilesafe.engin.ProcessProvider;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ProcessManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_process_count;
    private TextView tv_process_memory;
    private ListView lv_process;
    private Button btn_processmanager_all;
    private Button btn_processmanager_reverse;
    private Button btn_processmanager_clean;
    private Button btn_processmanager_setting;
    private TextView tv_process_des;
    private List<ProcessInfo> mProcessInfos;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mProcessAdapter == null) {
                if (tv_process_des != null && mUserProcess != null) {
                    tv_process_des.setText("用户应用(" + mUserProcess.size() + ")");
                }

                mProcessAdapter = new ProcessAdapter();
                lv_process.setAdapter(mProcessAdapter);
            } else {
                mProcessAdapter.notifyDataSetChanged();
            }
        }
    };
    private List<ProcessInfo> mUserProcess;
    private List<ProcessInfo> mSystemProcess;
    private ProcessAdapter mProcessAdapter;
    private ProcessInfo mProcessInfo;
    private int runningProcess;
    private long totleProcessSpace;
    private long availProcessSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmanager);

        initUI();

        initData();

        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProcessInfos = ProcessProvider.getProcessInfo(getApplicationContext());

                mUserProcess = new ArrayList<ProcessInfo>();
                mSystemProcess = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : mProcessInfos) {
                    if (info.getSystem()) {
                        //系统应用
                        mSystemProcess.add(info);
                    } else {
                        // 用户应用
                        mUserProcess.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initListView() {

        lv_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((position == 0) || (position == (mUserProcess.size() + 1))) {
                    return;
                } else {
                    if (position < mUserProcess.size() + 1) {
                        mProcessInfo = mUserProcess.get(position - 1);
                    } else {
                        mProcessInfo = mSystemProcess.get(position - mUserProcess.size() - 2);
                    }
                    if (mProcessInfo != null) {
                        //不能删除本应用
                        if (!mProcessInfo.getPackageName().equals(getApplicationContext().getPackageName())) {
                            mProcessInfo.setIscheck(!mProcessInfo.ischeck());

                            CheckBox cb_process_item = (CheckBox) view.findViewById(R.id.cb_process_item);
                            cb_process_item.setChecked(mProcessInfo.ischeck());
                        }
                    }
                }
            }
        });
    }

    private void initData() {
        runningProcess = ProcessProvider.getRunningProcess(getApplicationContext());
        totleProcessSpace = ProcessProvider.getTotleProcessSpace(getApplicationContext());
        availProcessSpace = ProcessProvider.getAvailProcessSpace(getApplicationContext());

        String total = Formatter.formatFileSize(getApplicationContext(), totleProcessSpace);
        String avail = Formatter.formatFileSize(getApplicationContext(), availProcessSpace);

        tv_process_count.setText("进程总数：" + runningProcess);
        tv_process_memory.setText("总共/剩余：" + total + "/" + avail);


    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_process_memory = (TextView) findViewById(R.id.tv_process_memory);
        lv_process = (ListView) findViewById(R.id.lv_process);
        tv_process_des = (TextView) findViewById(R.id.tv_process_des);

        btn_processmanager_all = (Button) findViewById(R.id.btn_processmanager_all);
        btn_processmanager_reverse = (Button) findViewById(R.id.btn_processmanager_reverse);
        btn_processmanager_clean = (Button) findViewById(R.id.btn_processmanager_clean);
        btn_processmanager_setting = (Button) findViewById(R.id.btn_processmanager_setting);

        btn_processmanager_all.setOnClickListener(this);
        btn_processmanager_reverse.setOnClickListener(this);
        btn_processmanager_clean.setOnClickListener(this);
        btn_processmanager_setting.setOnClickListener(this);

        lv_process.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (tv_process_des != null && mSystemProcess != null && mUserProcess != null) {
                    if (firstVisibleItem > mUserProcess.size()) {
                        tv_process_des.setText("系统进程(" + mSystemProcess.size() + ")");
                    } else {
                        tv_process_des.setText("用户应用(" + mUserProcess.size() + ")");
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_processmanager_all:
                selectAll();
                break;
            case R.id.btn_processmanager_reverse:
                reverse();
                break;
            case R.id.btn_processmanager_clean:
                cleanProcess();
                break;
            case R.id.btn_processmanager_setting:
                setting();
                break;
        }
    }

    private void setting() {
        Intent intent = new Intent(getApplicationContext(), ProcessSettingActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mProcessAdapter != null) {
            mProcessAdapter.notifyDataSetChanged();
        }
    }

    private void cleanProcess() {
        List<ProcessInfo> killProcessList = new ArrayList<>();
        long releaseSpace = 0;

        for (ProcessInfo info : mUserProcess) {
            if (!info.getPackageName().equals(getApplicationContext().getPackageName())) {
                if (info.ischeck()) {
                    killProcessList.add(info);
                }
            }
        }
        for (ProcessInfo info : mSystemProcess) {
            if (info.ischeck()) {
                killProcessList.add(info);
            }
        }

        for (ProcessInfo info : killProcessList) {
            releaseSpace += info.getMemorySize();
            if (mUserProcess.contains(info)) {
                mUserProcess.remove(info);
            } else if (mSystemProcess.contains(info)) {
                mSystemProcess.remove(info);
            }
        }
        //杀死进程，释放内存
        ProcessProvider.killProcessList(getApplicationContext(), killProcessList);

        int killProcessCount = killProcessList.size();
        runningProcess -= killProcessCount;

        availProcessSpace += releaseSpace;
        String release = Formatter.formatFileSize(getApplicationContext(), releaseSpace);
        String avail = Formatter.formatFileSize(getApplicationContext(), availProcessSpace);
        String total = Formatter.formatFileSize(getApplicationContext(), totleProcessSpace);

        tv_process_count.setText("进程总数：" + runningProcess);
        tv_process_memory.setText("总共/剩余：" + total + "/" + avail);

        mProcessAdapter.notifyDataSetChanged();

        ToastUtil.show(getApplicationContext(), String.format("杀死进程总数%d, 释放内存%s", killProcessCount, release));

    }

    private void reverse() {
        for (ProcessInfo info : mUserProcess) {
            if (!info.getPackageName().equals(getApplicationContext().getPackageName())) {
                info.setIscheck(!info.ischeck());
            }
        }
        for (ProcessInfo info : mSystemProcess) {
            info.setIscheck(!info.ischeck());
        }
        if (mProcessAdapter != null) {
            mProcessAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {
        for (ProcessInfo info : mUserProcess) {
            if (!info.getPackageName().equals(getApplicationContext().getPackageName())) {
                info.setIscheck(true);
            }
        }
        for (ProcessInfo info : mSystemProcess) {
            info.setIscheck(true);
        }
        if (mProcessAdapter != null) {
            mProcessAdapter.notifyDataSetChanged();
        }
    }

    private class ProcessAdapter extends BaseAdapter {

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
            if ((position == 0) || (position == (mUserProcess.size() + 1))) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getCount() {
            boolean isHideSystem = SPUtil.getBoolean(getApplicationContext(), ConstantValues.SYSTEMPROCESS_HIDE, false);
            if (isHideSystem) {
                return mUserProcess.size() + 1;
            } else {
                return mUserProcess.size() + mSystemProcess.size() + 2;
            }

        }

        @Override
        public ProcessInfo getItem(int position) {
            if ((position != 0) || (position != (mUserProcess.size() + 1))) {
                if (position < mUserProcess.size() + 1) {
                    return mUserProcess.get(position - 1);
                } else {
                    return mSystemProcess.get(position - mUserProcess.size() - 2);
                }
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (getItemViewType(position) == 1) {
                ViewHolder viewHolder = new ViewHolder();

                view = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                //必须要通过View去寻到里边的控件
                viewHolder.iv_process_item = (ImageView) view.findViewById(R.id.iv_process_item);
                viewHolder.tv_process_item_title = (TextView) view.findViewById(R.id.tv_process_item_title);
                viewHolder.tv_process_item_des = (TextView) view.findViewById(R.id.tv_process_item_des);
                viewHolder.cb_process_item = (CheckBox) view.findViewById(R.id.cb_process_item);

                view.setTag(viewHolder);
                if (convertView != null) {
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                }

                viewHolder.iv_process_item.setImageDrawable(getItem(position).getIcon());
                viewHolder.tv_process_item_title.setText(getItem(position).getName());

                long lMemorySize = getItem(position).getMemorySize();
                String memotySize = Formatter.formatFileSize(getApplicationContext(), lMemorySize);
                viewHolder.tv_process_item_des.setText(memotySize);

                /*if (getItem(position).getSystem()) {
                    viewHolder.tv_process_item_des.setText("系统进程");
                } else {
                    viewHolder.tv_process_item_des.setText("用户进程");
                }*/

                viewHolder.cb_process_item.setChecked(getItem(position).ischeck());

            } else {
                view = View.inflate(getApplicationContext(), R.layout.listview_process_text, null);
                TextView tv_process_list = (TextView) view.findViewById(R.id.tv_process_list);
                if (position == 0) {
                    tv_process_list.setText("用户应用(" + mUserProcess.size() + ")");
                } else {
                    tv_process_list.setText("系统应用(" + mSystemProcess.size() + ")");
                }
            }
            return view;
        }
    }

    private class ViewHolder {
        ImageView iv_process_item;
        TextView tv_process_item_title;
        TextView tv_process_item_des;
        CheckBox cb_process_item;

    }
}
