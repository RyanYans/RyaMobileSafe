package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class HomeActivity extends Activity {

    private String[] mTitleName;
    private int[] mTitleSrcId;
    private GridView gv_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        initData();
    }

    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    private void initData() {
        mTitleName = new String[]{
                "手机防盗", "通信卫士", "软件管理",
                "进程管理", "流量统计", "手机杀毒",
                "缓存管理", "高级工具", "设置中心"
        };
        mTitleSrcId = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings
        };
        gv_home.setAdapter(new MyAdapter());

        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:
                        Intent intent_advtools = new Intent(getApplicationContext(), AdvToolsActivity.class);
                        startActivity(intent_advtools);
                        break;
                    case 8:
                        Intent intent_setting = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent_setting);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        String psd = SPUtil.getString(getApplicationContext(), ConstantValues.MOBILE_SAFE_PSD, "");
        if (psd.isEmpty()) {
            settingPsd();
        } else {
            confirmPsd();
        }
    }

    /**
     * 没设置密码, 初始设置密码界面对话框
     */
    private void settingPsd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        final View view = View.inflate(getApplicationContext(), R.layout.dialog_setting_psd, null);
        alertDialog.setView(view);
        alertDialog.show();

        Button bt_setting_submit = (Button) view.findViewById(R.id.bt_setting_submit);
        Button bt_setting_cancel = (Button) view.findViewById(R.id.bt_setting_cancel);

        bt_setting_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_setting_psd = (EditText) view.findViewById(R.id.et_setting_psd);
                EditText et_setting_repsd = (EditText) view.findViewById(R.id.et_setting_repsd);
                String psd = et_setting_psd.getText().toString();
                String repsd = et_setting_repsd.getText().toString();

                if (!psd.isEmpty() & !repsd.isEmpty()) {
                    if (psd.equals(repsd)) {
                        SPUtil.putString(getApplicationContext(), ConstantValues.MOBILE_SAFE_PSD, psd);
                        goSafeActivity();
                        alertDialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "确认密码不匹配！");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码！");
                }
            }
        });

        bt_setting_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void goSafeActivity() {
        Intent intent = new Intent(getApplicationContext(), SafeActivity.class);
        startActivity(intent);
    }

    /**
     * 已设置密码, 确认密码界面对话框
     */
    private void confirmPsd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        final View view = View.inflate(getApplicationContext(), R.layout.dialog_confirm_psd, null);
        alertDialog.setView(view);
        alertDialog.show();

        Button bt_confirm_submit = (Button) view.findViewById(R.id.bt_confirm_submit);
        Button bt_confirm_cancel = (Button) view.findViewById(R.id.bt_confirm_cancel);

        bt_confirm_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd = et_confirm_psd.getText().toString();
                if (psd != null) {
                    checkPsd(psd);
                    alertDialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码！");
                }
            }
        });

        bt_confirm_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void checkPsd(String psd) {
        String r_psd = SPUtil.getString(getApplicationContext(), ConstantValues.MOBILE_SAFE_PSD, "");
        if (psd.equals(r_psd)) {
            goSafeActivity();
        } else {
            ToastUtil.show(getApplicationContext(), "密码错误！");
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTitleName.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleSrcId[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            ImageView iv_item = (ImageView)view.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

            iv_item.setBackgroundResource(mTitleSrcId[position]);
            tv_item.setText(mTitleName[position]);

            return view;
        }
    }
}
