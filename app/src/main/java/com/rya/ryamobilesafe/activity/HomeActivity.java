package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.lidroid.xutils.db.table.Id;
import com.rya.ryamobilesafe.R;

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

                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
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
