package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
 * <p>
 * 复用converview
 * 对finviewById次数进行优化
 */
public class BlackNumberActivity extends Activity {

    private ListView lv_blacknumber;
    private Button btn_add_blacknumber;
    private List<BlackNumber> mBlackNumberList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAdapter == null) {
                mAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    private MyAdapter mAdapter;
    private BlackNumberDao mBlackNumberDao;
    private boolean isLoad = false;

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
                    mBlackNumberDao.insert(phone, Integer.toString(mode));
                    // 同步数据到listview！
                    BlackNumber blackNumber = new BlackNumber();
                    blackNumber.setPhone(phone);
                    blackNumber.setState(Integer.toString(mode));

                    //从list的首位置插入数据
                    mBlackNumberList.add(0, blackNumber);
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
                mBlackNumberList = mBlackNumberDao.searchPart(0);

                mHandler.sendEmptyMessage(0);

            }
        }).start();
    }

    private void initUI() {
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        btn_add_blacknumber = (Button) findViewById(R.id.btn_add_blacknumber);

        // lv_blacknumber.setOnScrollListener 设置监听
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                AbsListView.OnScrollListener.SCROLL_STATE_FLING       自然滚动
//                AbsListView.OnScrollListener.SCROLL_STATE_IDLE        空闲
//                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL    拖动（触摸）

                //空闲状态 + 最后一条位置>=总条数 + 不处于加载数据状态
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (lv_blacknumber.getLastVisiblePosition() >= (mBlackNumberList.size() - 1))
                        && !isLoad) {
                    if (mBlackNumberList.size() < mBlackNumberDao.countAll()) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                List<BlackNumber> newBlackNumberList = mBlackNumberDao.searchPart(mBlackNumberList.size());
                                mBlackNumberList.addAll(newBlackNumberList);

                                mHandler.sendEmptyMessage(0);
                            }
                        }.start();
                    }

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
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
            View view;
            ViewHolder viewHolder = null;

            //复用ViewHolder，减少FindViewById()的次数
            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);

                viewHolder = new ViewHolder();

                viewHolder.tv_blacknumber_phone = (TextView) view.findViewById(R.id.tv_blacknumber_phone);
                viewHolder.tv_blacknumber_state = (TextView) view.findViewById(R.id.tv_blacknumber_state);
                viewHolder.iv_blacknumber_delete = (ImageView) view.findViewById(R.id.iv_blacknumber_delete);

                //  把ViewHolder(textView、imageView..)放入View当中，
                // 待日后converView服用直接取出ViewHolder
                view.setTag(viewHolder);
            }


            viewHolder.iv_blacknumber_delete.setOnClickListener(new View.OnClickListener() {
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
            viewHolder.tv_blacknumber_phone.setText(phone);
            String state = mBlackNumberList.get(position).getState();
            switch (state) {
                case "0":
                    viewHolder.tv_blacknumber_state.setText("拦截短信");
                    break;
                case "1":
                    viewHolder.tv_blacknumber_state.setText("拦截电话");
                    break;
                case "2":
                    viewHolder.tv_blacknumber_state.setText("拦截所有");
                    break;
            }
            return view;
        }
    }

    private static class ViewHolder {
        TextView tv_blacknumber_phone;
        TextView tv_blacknumber_state;
        ImageView iv_blacknumber_delete;
    }
}
