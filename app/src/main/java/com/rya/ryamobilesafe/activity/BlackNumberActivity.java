package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.dao.BlackNumberDao;
import com.rya.ryamobilesafe.db.domain.BlackNumber;
import com.rya.ryamobilesafe.utils.ToastUtil;

import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class BlackNumberActivity extends Activity {

    private ListView lv_blacknumber;
    private Button btn_add_blacknumber;
    private List<BlackNumber> mBlackNumberList;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter = new MyAdapter();
            lv_blacknumber.setAdapter(mAdapter);
        }
    };
    private MyAdapter mAdapter;
    private BlackNumberDao mBlackNumberDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blacknumber);

        initUI();

        initDate();

        btn_add_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddToast();
            }
        });


    }

    private void initAddToast() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_blacknumber_add, null);
        alertDialog.setView(view);
        alertDialog.show();

        final EditText et_blacknumber_add = (EditText) view.findViewById(R.id.et_blacknumber_add);
        final RadioGroup rg_blacknumbergroup = (RadioGroup) view.findViewById(R.id.rg_blacknumbergroup);
        Button btn_add_blacknumber_submit = (Button) view.findViewById(R.id.btn_add_blacknumber_submit);
        Button btn_add_blacknumber_cancel = (Button) view.findViewById(R.id.btn_add_blacknumber_cancel);

        btn_add_blacknumber_submit.setOnClickListener(new View.OnClickListener() {
            int mode;
            @Override
            public void onClick(View v) {
                int checkedRadioButtonId = rg_blacknumbergroup.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rb_blacknumber_sms:
                        mode = 0;
                        break;
                    case R.id.rb_blacknumber_phone:
                        mode = 1;
                        break;
                    case R.id.rb_blacknumber_all:
                        mode = 2;
                        break;
                }

                String phone = et_blacknumber_add.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    mBlackNumberDao.insert(phone,Integer.toString(mode));
                    // 同步数据到listview！
                    BlackNumber blackNumber = new BlackNumber();
                    blackNumber.setPhone(phone);
                    blackNumber.setState(Integer.toString(mode));
                    mBlackNumberList.add(0,blackNumber);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    alertDialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入号码！");
                    return;
                }
            }
        });

        btn_add_blacknumber_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void initDate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBlackNumberDao = BlackNumberDao.create(getApplicationContext());
                mBlackNumberList = mBlackNumberDao.searchAll();

                mHandler.sendEmptyMessage(0);

            }
        }).start();
    }

    private void initUI() {
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        btn_add_blacknumber = (Button) findViewById(R.id.btn_add_blacknumber);

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
            TextView tv_blacknumber_phone = (TextView) view.findViewById(R.id.tv_blacknumber_phone);
            TextView tv_blacknumber_state = (TextView) view.findViewById(R.id.tv_blacknumber_state);
            ImageView iv_blacknumber_delete = (ImageView) view.findViewById(R.id.iv_blacknumber_delete);

            iv_blacknumber_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBlackNumberDao.delete(mBlackNumberList.get(position).getPhone());
                    mBlackNumberList.remove(position);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

            String phone = mBlackNumberList.get(position).getPhone();
                tv_blacknumber_phone.setText(phone);
                String state = mBlackNumberList.get(position).getState();
                switch (state) {
                    case "0":
                        tv_blacknumber_state.setText("拦截短信");
                        break;
                    case "1":
                        tv_blacknumber_state.setText("拦截电话");
                        break;
                    case "2":
                        tv_blacknumber_state.setText("拦截所有");
                        break;
                }
            return view;
        }
    }
}
