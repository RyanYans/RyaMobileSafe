package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.dao.CommomNumberDao;

import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class CommomNumberSearchActivity extends Activity {

    private List<CommomNumberDao.numberGroup> mGroup;
    private ExpandableListView elv_commomnumber;
    private CommomNumberAdapter mCommAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advtools_commomnumber_search);

        initUI();

        initData();

        initListener();

    }

    private void initListener() {
        elv_commomnumber.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String number = mCommAdapter.getChild(groupPosition, childPosition).number;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);

                return false;
            }
        });
    }

    private void initData() {
        CommomNumberDao commomNumberDao = new CommomNumberDao();
        mGroup = commomNumberDao.getGroup();

        mCommAdapter = new CommomNumberAdapter();
        elv_commomnumber.setAdapter(mCommAdapter);
    }

    private void initUI() {
        elv_commomnumber = (ExpandableListView) findViewById(R.id.elv_commomnumber);

    }

    private class CommomNumberAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroup.get(groupPosition).childLists.size();
        }

        @Override
        public CommomNumberDao.numberGroup getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public CommomNumberDao.child getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition).childLists.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView != null) {
                textView = (TextView) convertView;
                textView.setText(getGroup(groupPosition).name);
            } else {
                textView = new TextView(CommomNumberSearchActivity.this);
                textView.setPadding(100,15,15,15);
                textView.setText(getGroup(groupPosition).name);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = null;
            viewHolder viewHolder = null;
            if (convertView != null) {
                view = convertView;
                viewHolder = (CommomNumberAdapter.viewHolder) view.getTag();
            } else {
                viewHolder = new viewHolder();
                view = View.inflate(getApplicationContext(), R.layout.elistview_commomnumber_child, null);
                viewHolder.tv_commomnumber_child_name = (TextView) view.findViewById(R.id.tv_commomnumber_child_name);
                viewHolder.tv_commomnumber_child_number = (TextView) view.findViewById(R.id.tv_commomnumber_child_number);
                view.setTag(viewHolder);
            }

            viewHolder.tv_commomnumber_child_name.setText(getChild(groupPosition, childPosition).name);
            viewHolder.tv_commomnumber_child_number.setText(getChild(groupPosition, childPosition).number);

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class viewHolder {
            TextView tv_commomnumber_child_name;
            TextView tv_commomnumber_child_number;
        }
    }



}
